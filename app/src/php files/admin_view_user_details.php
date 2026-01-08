<?php
header("Content-Type: application/json");
include "auth_session.php";
include "db.php";

/* ---------- AUTH CHECK ---------- */
if ($_SESSION['role'] !== 'admin') {
    echo json_encode(["status" => "error", "message" => "Unauthorized"]);
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
$user_uid_safe = mysqli_real_escape_string($conn, $user_uid);

$query = mysqli_query($conn, "
    SELECT
        u.user_uid,
        u.full_name,
        u.email,
        u.role,
        u.status,
        u.dob,
        u.gender,
        u.created_at,
        i.issuer_id,
        i.verified AS issuer_verified
    FROM users u
    LEFT JOIN issuers i ON u.user_uid = i.user_uid
    WHERE u.user_uid = '$user_uid_safe'
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
