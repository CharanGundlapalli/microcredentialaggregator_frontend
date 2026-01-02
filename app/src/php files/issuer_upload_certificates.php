<?php
header("Content-Type: application/json");
session_start();

include "db.php";
include "apply_skill_mapping_direct.php";

// --------------------
// 1. AUTH & ROLE CHECK
// --------------------
if (!isset($_SESSION['user_uid']) || $_SESSION['role'] !== 'issuer') {
    echo json_encode(["status" => "error", "message" => "Unauthorized"]);
    exit;
}

$issuer_uid = $_SESSION['user_uid'];

// check issuer verification
$issuerCheck = mysqli_query($conn, "
    SELECT verified FROM issuers WHERE user_uid = '$issuer_uid'
");
$issuer = mysqli_fetch_assoc($issuerCheck);

if (!$issuer || $issuer['verified'] != 1) {
    echo json_encode([
        "status" => "error",
        "message" => "Issuer not approved by admin"
    ]);
    exit;
}

// --------------------
// 2. ZIP FILE CHECK
// --------------------
if (!isset($_FILES['zip_file'])) {
    echo json_encode(["status" => "error", "message" => "ZIP file missing"]);
    exit;
}

$zipTmp = $_FILES['zip_file']['tmp_name'];
$zipName = $_FILES['zip_file']['name'];

if (pathinfo($zipName, PATHINFO_EXTENSION) !== 'zip') {
    echo json_encode(["status" => "error", "message" => "Only ZIP files allowed"]);
    exit;
}

// --------------------
// 3. EXTRACT ZIP
// --------------------
$extractPath = "C:/xampp/htdocs/microcredentialaggregator/tmp/" . uniqid("issuer_");

mkdir($extractPath, 0777, true);

$zip = new ZipArchive();
if ($zip->open($zipTmp) !== TRUE) {
    echo json_encode(["status" => "error", "message" => "Unable to open ZIP"]);
    exit;
}
$zip->extractTo($extractPath);
$zip->close();

// expected structure
$excelFile = $extractPath . "/users.csv";
$certFolder = $extractPath . "/certificates";

if (!file_exists($excelFile) || !is_dir($certFolder)) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid ZIP structure. Required: users.csv and certificates/"
    ]);
    exit;
}

// --------------------
// 4. PROCESS CSV
// --------------------
$handle = fopen($excelFile, "r");
$header = fgetcsv($handle); // skip header

$success = 0;
$failed = 0;
$errors = [];

while (($row = fgetcsv($handle)) !== false) {

    // CSV format:
    // email,certificate_file,certificate_title,certificate_type,issuing_organization,issue_date,expiry_date,credential_id

    [
        $email,
        $certificateFile,
        $title,
        $type,
        $issuer,
        $issueDate,
        $expiryDate,
        $credentialId
    ] = $row;

    $filePath = $certFolder . "/" . $certificateFile;

    if (!file_exists($filePath)) {
        $failed++;
        $errors[] = "File not found: $certificateFile";
        continue;
    }

    // generate certificate UID
    $certificate_uid = "CERT" . time() . rand(100, 999);

    // move file to permanent storage
    $newFileName = "certificate_" . $certificate_uid . "." . pathinfo($certificateFile, PATHINFO_EXTENSION);
    $destPath = "C:/xampp/htdocs/microcredentialaggregator/certificates/" . $newFileName;

    copy($filePath, $destPath);

    // check if user exists
    $userQ = mysqli_query($conn, "
        SELECT user_uid FROM users WHERE email = '$email'
    ");
    $user = mysqli_fetch_assoc($userQ);

    if ($user) {
        // --------------------
        // USER EXISTS → DIRECT INSERT
        // --------------------
        mysqli_query($conn, "
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
                verified_by,
                verified_at
            ) VALUES (
                '$certificate_uid',
                '{$user['user_uid']}',
                '$title',
                '$type',
                '$issuer',
                '$issueDate',
                '$expiryDate',
                '$credentialId',
                '$newFileName',
                'issuer',
                'verified',
                '$issuer_uid',
                NOW()
            )
        ");

        // apply skills immediately
        applySkillMappingDirect($conn, $issuer, $title, $user['user_uid']);

        $success++;

    } else {
        // --------------------
        // USER NOT EXISTS → PENDING
        // --------------------
        mysqli_query($conn, "
            INSERT INTO pending_certificates (
                email,
                certificate_uid,
                certificate_title,
                certificate_type,
                issuing_organization,
                issue_date,
                expiry_date,
                credential_id,
                certificate_file,
                issued_by
            ) VALUES (
                '$email',
                '$certificate_uid',
                '$title',
                '$type',
                '$issuer',
                '$issueDate',
                '$expiryDate',
                '$credentialId',
                '$newFileName',
                '$issuer_uid'
            )
        ");

        $success++;
    }
}

fclose($handle);

// --------------------
// 5. FINAL RESPONSE
// --------------------
echo json_encode([
    "status" => "success",
    "message" => "Issuer upload processed",
    "issued" => $success,
    "failed" => $failed,
    "errors" => $errors
]);
