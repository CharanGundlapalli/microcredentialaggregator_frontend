<?php
header("Content-Type: application/json");
session_start();
include "db.php";

// admin check
if (
    !isset($_SESSION['user_uid']) ||
    !isset($_SESSION['role']) ||
    $_SESSION['role'] !== 'admin'
) {
    echo json_encode([
        "status" => "error",
        "message" => "Unauthorized"
    ]);
    exit;
}

// read JSON input
$data = json_decode(file_get_contents("php://input"), true);

if (
    empty($data['user_uid']) ||
    empty($data['status'])
) {
    echo json_encode([
        "status" => "error",
        "message" => "Missing fields"
    ]);
    exit;
}

$user_uid = mysqli_real_escape_string($conn, $data['user_uid']);
$status   = mysqli_real_escape_string($conn, $data['status']);

// allow only valid values
if (!in_array($status, ['active', 'inactive'])) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid status"
    ]);
    exit;
}

// update user status
$query = "
UPDATE users
SET status = '$status'
WHERE user_uid = '$user_uid'
";

if (mysqli_query($conn, $query)) {
    echo json_encode([
        "status" => "success",
        "message" => "User status updated",
        "user_uid" => $user_uid,
        "new_status" => $status
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Failed to update user status"
    ]);
}
