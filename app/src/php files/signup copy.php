<?php
header("Content-Type: application/json");
include "db.php";

$data = json_decode(file_get_contents("php://input"), true);

// validate input
if (
    empty($data['full_name']) ||
    empty($data['email']) ||
    empty($data['password']) ||
    empty($data['role'])
) {
    echo json_encode([
        "status" => "error",
        "message" => "Missing required fields"
    ]);
    exit;
}

$full_name = mysqli_real_escape_string($conn, $data['full_name']);
$email = mysqli_real_escape_string($conn, $data['email']);
$password = password_hash($data['password'], PASSWORD_BCRYPT);
$role = mysqli_real_escape_string($conn, $data['role']);

// allowed roles
$allowed_roles = ['user', 'admin', 'employer', 'issuer'];
if (!in_array($role, $allowed_roles)) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid role"
    ]);
    exit;
}

// generate user UID
$user_uid = "U" . date("Y") . rand(10000, 99999);

// check email
$check = mysqli_query($conn, "SELECT id FROM users WHERE email='$email'");
if (mysqli_num_rows($check) > 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Email already registered"
    ]);
    exit;
}

// insert user
$query = "
    INSERT INTO users (user_uid, full_name, email, password, role)
    VALUES ('$user_uid', '$full_name', '$email', '$password', '$role')
";

if (!mysqli_query($conn, $query)) {
    echo json_encode([
        "status" => "error",
        "message" => "Signup failed"
    ]);
    exit;
}

// 🏢 CREATE ISSUER RECORD IF ROLE = ISSUER
if ($role === 'issuer') {
    mysqli_query($conn, "
        INSERT INTO issuers (user_uid, issuer_name, verified)
        VALUES ('$user_uid', '$full_name', 0)
    ");
}

echo json_encode([
    "status" => "success",
    "message" => $role === 'issuer'
        ? "Signup successful. Awaiting admin approval."
        : "Signup successful",
    "user_uid" => $user_uid
]);
