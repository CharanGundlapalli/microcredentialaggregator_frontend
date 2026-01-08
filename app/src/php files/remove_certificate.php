<?php
header("Content-Type: application/json");
include "auth_session.php";
include "db.php";

/* ---------- AUTH CHECK ---------- */
if (!isset($_SESSION['user_uid'])) {
    // Note: auth_session handles user_uid check but safer to keep specific logic if needed
    // Actually auth_session exits if unauthorized.
    // Keeping minimal structure.
}

$user_uid = $_SESSION['user_uid'];
$role = $_SESSION['role'] ?? 'user';

// read input
$data = json_decode(file_get_contents("php://input"), true);

if (empty($data['certificate_uid'])) {
    echo json_encode([
        "status" => "error",
        "message" => "certificate_uid required"
    ]);
    exit;
}

$certificate_uid = mysqli_real_escape_string($conn, $data['certificate_uid']);

// fetch certificate
$query = "
SELECT certificate_file, user_uid
FROM certificates
WHERE certificate_uid = '$certificate_uid'
";

$result = mysqli_query($conn, $query);

if (mysqli_num_rows($result) === 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Certificate not found"
    ]);
    exit;
}

$cert = mysqli_fetch_assoc($result);

// allow only owner or admin
if ($cert['user_uid'] !== $user_uid && $role !== 'admin') {
    echo json_encode([
        "status" => "error",
        "message" => "Permission denied"
    ]);
    exit;
}

// delete file
$file_path = "C:/xampp/htdocs/microcredentialaggregator/certificates/" . $cert['certificate_file'];
if (file_exists($file_path)) {
    unlink($file_path);
}

// delete certificate record
mysqli_query($conn, "
DELETE FROM certificates
WHERE certificate_uid = '$certificate_uid'
");

// ⚠ OPTIONAL BUT IMPORTANT
// If you want PERFECT accuracy, skills should be recalculated
// (simplest approach: clear user skills and re-map remaining certificates)

echo json_encode([
    "status" => "success",
    "message" => "Certificate removed successfully",
    "certificate_uid" => $certificate_uid
]);
