<?php
header("Content-Type: application/json");
session_start();
include "db.php";

// employer check
if (
    !isset($_SESSION['user_uid']) ||
    !isset($_SESSION['role']) ||
    $_SESSION['role'] !== 'employer'
) {
    echo json_encode(["status" => "error", "message" => "Unauthorized"]);
    exit;
}

// user_uid to view
if (!isset($_GET['user_uid'])) {
    echo json_encode(["status" => "error", "message" => "user_uid required"]);
    exit;
}

$user_uid = mysqli_real_escape_string($conn, $_GET['user_uid']);

// fetch ONLY verified certificates
$query = "
SELECT 
    certificate_uid,
    certificate_title,
    certificate_type,
    issuing_organization,
    issue_date,
    expiry_date,
    verification_status
FROM certificates
WHERE user_uid = '$user_uid'
  AND verification_status = 'verified'
ORDER BY issue_date DESC
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
