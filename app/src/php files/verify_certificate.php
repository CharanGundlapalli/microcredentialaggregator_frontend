<?php
header("Content-Type: application/json");
include "auth_session.php";
include "db.php";
include "apply_skill_mapping_direct.php";

$data = json_decode(file_get_contents("php://input"), true);

if (empty($data['certificate_uid']) || empty($data['status'])) {
    echo json_encode(["status"=>"error","message"=>"Missing fields"]);
    exit;
}

$certificate_uid = mysqli_real_escape_string($conn, $data['certificate_uid']);
$status = $data['status'];
$admin_uid = $_SESSION['user_uid'];

if (!in_array($status,['verified','rejected'])) {
    echo json_encode(["status"=>"error","message"=>"Invalid status"]);
    exit;
}

// fetch certificate
$certQ = mysqli_query($conn,"
SELECT user_uid, issuing_organization, certificate_title, source
FROM certificates WHERE certificate_uid='$certificate_uid'
");
$cert = mysqli_fetch_assoc($certQ);

// 🔒 issuer certificates cannot be manually verified
if ($cert['source'] === 'issuer') {
    echo json_encode(["status"=>"error","message"=>"Issuer certificates are auto-verified"]);
    exit;
}

// update certificate
mysqli_query($conn,"
UPDATE certificates
SET verification_status='$status',
verified_by='$admin_uid',
verified_at=NOW()
WHERE certificate_uid='$certificate_uid'
");

// 🔥 APPLY SKILL MAPPING ONLY ON APPROVAL
if ($status === 'verified') {
    applySkillMappingDirect(
        $conn,
        $cert['issuing_organization'],
        $cert['certificate_title'],
        $cert['user_uid']
    );
}

echo json_encode([
    "status"=>"success",
    "message"=>"Certificate $status successfully"
]);
