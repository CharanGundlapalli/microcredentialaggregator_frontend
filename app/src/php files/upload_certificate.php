<?php
header("Content-Type: application/json");
include "auth_session.php";
include "db.php";

/* ---------- AUTH CHECK ---------- */
// auth_session.php handles basic auth check.

$user_uid = $_SESSION['user_uid'];

/* ---------- REQUIRED FIELDS ---------- */
$required = [
    'certificate_title',
    'certificate_type',
    'issuing_organization',
    'issue_date'
];

foreach ($required as $field) {
    if (empty($_POST[$field])) {
        echo json_encode([
            "status" => "error",
            "message" => "Missing $field"
        ]);
        exit;
    }
}

if (!isset($_FILES['certificate'])) {
    echo json_encode([
        "status" => "error",
        "message" => "Certificate file missing"
    ]);
    exit;
}

/* ---------- HANDLE ISSUER SELECTION ---------- */
$issuing_org = $_POST['issuing_organization'];
$issuer_verified = 1;

// If user selected "Other"
if ($issuing_org === 'OTHER') {

    if (empty($_POST['new_issuer_name'])) {
        echo json_encode([
            "status" => "error",
            "message" => "New issuer name required"
        ]);
        exit;
    }

    $issuing_org = trim($_POST['new_issuer_name']);
    $issuer_verified = 0;

} else {
    // Double-check issuer exists & is verified (backend safety)
    $checkIssuer = mysqli_query($conn, "
        SELECT 1 FROM issuers 
        WHERE issuer_name = '$issuing_org' AND verified = 1
    ");

    if (mysqli_num_rows($checkIssuer) === 0) {
        echo json_encode([
            "status" => "error",
            "message" => "Invalid issuer selection"
        ]);
        exit;
    }
}

/* ---------- FILE UPLOAD ---------- */
$certificate_uid = "CERT" . time() . rand(100, 999);

$upload_dir = "C:/xampp/htdocs/microcredentialaggregator/certificates/";
$file_ext = strtolower(pathinfo($_FILES['certificate']['name'], PATHINFO_EXTENSION));
$allowed = ['pdf', 'jpg', 'jpeg', 'png'];

if (!in_array($file_ext, $allowed)) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid file type"
    ]);
    exit;
}

$file_name = "certificate_" . $certificate_uid . "." . $file_ext;

if (!move_uploaded_file($_FILES['certificate']['tmp_name'], $upload_dir . $file_name)) {
    echo json_encode([
        "status" => "error",
        "message" => "File upload failed"
    ]);
    exit;
}

/* ---------- INSERT CERTIFICATE ---------- */
$stmt = $conn->prepare("
    INSERT INTO certificates (
        certificate_uid,
        user_uid,
        certificate_title,
        certificate_type,
        issuing_organization,
        issue_date,
        expiry_date,
        credential_id,
        certificate_file,
        source,
        verification_status,
        issuer_verified
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'user', 'pending', ?)
");

$stmt->bind_param(
    "sssssssssi",
    $certificate_uid,
    $user_uid,
    $_POST['certificate_title'],
    $_POST['certificate_type'],
    $issuing_org,
    $_POST['issue_date'],
    $_POST['expiry_date'],
    $_POST['credential_id'],
    $file_name,
    $issuer_verified
);

$stmt->execute();

/* ---------- FINAL RESPONSE ---------- */
echo json_encode([
    "status" => "success",
    "message" => $issuer_verified
        ? "Certificate uploaded. Awaiting verification."
        : "Certificate uploaded. Issuer will be verified by admin.",
    "certificate_uid" => $certificate_uid
]);
