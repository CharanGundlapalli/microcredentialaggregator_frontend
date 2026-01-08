<?php
header("Content-Type: application/json");
include "auth_session.php";
include "db.php";

/* ---------- AUTH CHECK ---------- */
if ($_SESSION['role'] !== 'employer') {
    echo json_encode(["status" => "error", "message" => "Unauthorized"]);
    exit;
}

$data = json_decode(file_get_contents("php://input"), true);

$search = trim($data['search'] ?? '');

if ($search === '') {
    echo json_encode(["status" => "error", "message" => "Search value required"]);
    exit;
}

// search by user_uid OR email
$query = "
SELECT user_uid, full_name, email
FROM users
WHERE user_uid = '$search' OR email = '$search'
LIMIT 1
";

$result = mysqli_query($conn, $query);

if (mysqli_num_rows($result) === 0) {
    echo json_encode(["status" => "error", "message" => "User not found"]);
    exit;
}

$user = mysqli_fetch_assoc($result);

echo json_encode([
    "status" => "success",
    "user" => $user
]);
