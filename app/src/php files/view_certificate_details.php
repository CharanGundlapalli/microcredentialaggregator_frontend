<?php
header("Content-Type: application/json");
include "auth_session.php";
include "db.php";

/* ---------- AUTH CHECK ---------- */
// auth_session handles check

$user_uid = $_SESSION['user_uid'];
$role = $_SESSION['role'];

/* ---------- CERT UID CHECK ---------- */
if (empty($_GET['certificate_uid'])) {
    echo json_encode([
        "status" => "error",
        "message" => "certificate_uid required"
    ]);
    exit;
}

$certificate_uid = mysqli_real_escape_string($conn, $_GET['certificate_uid']);

/* ---------- ROLE-BASED QUERY ---------- */
$where = "";

if ($role === 'user') {
    $where = "AND user_uid = '$user_uid'";
}

if ($role === 'employer') {
    $where = "AND verification_status = 'verified'";
}

if ($role === 'issuer') {
    $where = "AND source = 'issuer' AND verified_by = '$user_uid'";
}

if ($role === 'admin') {
    $where = ""; // full access
}

/* ---------- FETCH CERTIFICATE ---------- */
$query = "
    SELECT 
        certificate_uid,
        user_uid,
        certificate_title,
        certificate_type,
        issuing_organization,
        issue_date,
        expiry_date,
        credential_id,
        certificate_file,
        verification_status,
        verified_by,
        verified_at,
        created_at
    FROM certificates
    WHERE certificate_uid = '$certificate_uid'
    $where
    LIMIT 1
";

$result = mysqli_query($conn, $query);

if (mysqli_num_rows($result) === 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Certificate not found or access denied"
    ]);
    exit;
}

$certificate = mysqli_fetch_assoc($result);

/* ---------- FILE URL ---------- */
$certificate['download_url'] =
    "http://localhost/microcredentialaggregator/certificates/" .
    $certificate['certificate_file'];

echo json_encode([
    "status" => "success",
    "certificate" => $certificate
]);
