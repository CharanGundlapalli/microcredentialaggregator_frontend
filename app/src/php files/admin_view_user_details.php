<?php
header("Content-Type: application/json");
session_start();
include "db.php";

/* ---------- ADMIN AUTH CHECK ---------- */
if (!isset($_SESSION['user_uid']) || $_SESSION['role'] !== 'admin') {
    echo json_encode([
        "status" => "error",
        "message" => "Unauthorized access"
    ]);
    exit;
}

/* ---------- INPUT VALIDATION ---------- */
$user_uid = $_GET['user_uid'] ?? '';

if (empty($user_uid)) {
    echo json_encode([
        "status" => "error",
        "message" => "user_uid is required"
    ]);
    exit;
}

/* ---------- FETCH USER DETAILS ONLY ---------- */
$query = mysqli_query($conn, "
    SELECT
        user_uid,
        full_name,
        email,
        role,
        status,
        dob,
        gender,
        created_at
    FROM users
    WHERE user_uid = '$user_uid'
");

if (mysqli_num_rows($query) === 0) {
    echo json_encode([
        "status" => "error",
        "message" => "User not found"
    ]);
    exit;
}

$user = mysqli_fetch_assoc($query);

/* ---------- RESPONSE ---------- */
echo json_encode([
    "status" => "success",
    "user" => $user
]);
