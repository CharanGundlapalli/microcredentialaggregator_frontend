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

/* ---------- FETCH USERS & ISSUERS WITH STATUS ---------- */
$query = "
    SELECT user_uid, full_name, role, status
    FROM users
    WHERE role IN ('user', 'issuer')
    ORDER BY role, full_name
";

$result = mysqli_query($conn, $query);

if (!$result) {
    echo json_encode([
        "status" => "error",
        "message" => "Database error"
    ]);
    exit;
}

$data = [];

while ($row = mysqli_fetch_assoc($result)) {
    $data[] = [
        "user_id" => $row['user_uid'],
        "name"    => $row['full_name'],
        "role"    => $row['role'],
        "status"  => $row['status']   // active / blocked
    ];
}

/* ---------- RESPONSE ---------- */
echo json_encode([
    "status" => "success",
    "count"  => count($data),
    "data"   => $data
]);
