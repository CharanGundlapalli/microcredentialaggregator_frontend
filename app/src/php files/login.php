<?php
header("Content-Type: application/json");
session_start();
include "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$email = $data['email'] ?? '';
$password = $data['password'] ?? '';

if (!$email || !$password) {
    echo json_encode(["status" => "error", "message" => "Missing fields"]);
    exit;
}

// fetch user
$query = mysqli_query($conn, "
    SELECT * FROM users 
    WHERE email='$email' AND status='active'
");
$user = mysqli_fetch_assoc($query);

if (!$user || !password_verify($password, $user['password'])) {
    echo json_encode(["status" => "error", "message" => "Invalid login"]);
    exit;
}

// 🔐 ISSUER VERIFICATION CHECK
if ($user['role'] === 'issuer') {
    $issuerCheck = mysqli_query($conn, "
        SELECT verified 
        FROM issuers 
        WHERE user_uid='{$user['user_uid']}'
    ");
    $issuer = mysqli_fetch_assoc($issuerCheck);

    if (!$issuer || $issuer['verified'] == 0) {
        echo json_encode([
            "status" => "error",
            "message" => "Issuer account pending admin approval"
        ]);
        exit;
    }
}

// create session
$_SESSION['user_uid'] = $user['user_uid'];
$_SESSION['login_time'] = time();
$_SESSION['role'] = $user['role'];
$_SESSION['last_activity'] = time(); // Initialize timeout timer
$_SESSION['expire_time'] = time() + (30 * 60); // Keeping original for reference, but auth_session uses last_activity

// device + IP
$ip = $_SERVER['REMOTE_ADDR'];
$device = $_SERVER['HTTP_USER_AGENT'];
$session_token = session_id();

// 🌍 location (safe fallback)
$country = "Unknown";
$city = "Unknown";

$location = @json_decode(@file_get_contents("http://ip-api.com/json/$ip"), true);
if ($location) {
    $country = $location['country'] ?? 'Unknown';
    $city = $location['city'] ?? 'Unknown';
}

// store login log
mysqli_query($conn, "
    INSERT INTO login_logs 
    (user_uid, login_time, ip_address, device_info, country, city, session_token)
    VALUES
    ('{$user['user_uid']}', NOW(), '$ip', '$device', '$country', '$city', '$session_token')
");

echo json_encode([
    "status" => "success",
    "message" => "Login successful",
    "user_uid" => $user['user_uid'],
    "name" => $user['full_name'],
    "role" => $user['role']
]);
