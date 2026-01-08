<?php
header("Content-Type: application/json");
include "auth_session.php";
include "db.php";

// check login (handled by auth_session)
// if (!isset($_SESSION['user_uid'])) { ... }

$user_uid = $_SESSION['user_uid'];

// fetch only required fields
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

$certificates = [];

while ($row = mysqli_fetch_assoc($result)) {
    $certificates[] = $row;
}

echo json_encode([
    "status" => "success",
    "count" => count($certificates),
    "certificates" => $certificates
]);
