<?php
header("Content-Type: application/json");
include "auth_session.php";
include "db.php";

/* ---------- ADMIN AUTH CHECK ---------- */
if ($_SESSION['role'] !== 'admin') {
    echo json_encode(["status" => "error", "message" => "Unauthorized access"]);
    exit;
}

/* ---------- GET USER UID (CLICKED USER) ---------- */
$user_uid = $_GET['user_uid'] ?? '';

if (empty($user_uid)) {
    echo json_encode([
        "status" => "error",
        "message" => "user_uid is required"
    ]);
    exit;
}

/* ---------- FETCH USER CERTIFICATES ---------- */
$query = "
    SELECT 
        certificate_uid,
        certificate_title,
        issue_date,
        expiry_date,
        verification_status
    FROM certificates
    WHERE user_uid = '$user_uid'
    ORDER BY created_at DESC
";

$result = mysqli_query($conn, $query);

if (!$result) {
    echo json_encode([
        "status" => "error",
        "message" => "Database error"
    ]);
    exit;
}

$certificates = [];

while ($row = mysqli_fetch_assoc($result)) {
    $certificates[] = $row;
}

/* ---------- RESPONSE ---------- */
echo json_encode([
    "status" => "success",
    "count" => count($certificates),
    "certificates" => $certificates
]);
