<?php
header("Content-Type: application/json");
include "auth_session.php";
include "db.php";

/* ---------- AUTH CHECK ---------- */
if ($_SESSION['role'] !== 'issuer') {
    echo json_encode(["status" => "error", "message" => "Unauthorized"]);
    exit;
}

$issuer_uid = $_SESSION['user_uid'];

/* ---------- FETCH HISTORY ---------- */
$history = [];

// 1. Fetch from Active Certificates (Users who exist)
// We join with users table to get the recipient's name/email
$queryActive = "
    SELECT 
        c.certificate_uid,
        c.certificate_title,
        c.issue_date,
        'Active' as status,
        u.item_value as recipient, -- users doesn't have email in some schemas? Wait, let's check user table.
        u.email as recipient_email,
        u.full_name as recipient_name
    FROM certificates c
    JOIN users u ON c.user_uid = u.user_uid
    WHERE c.verified_by = '$issuer_uid'
";

// Wait, I need to be sure about 'users' schema. 
// In login.php: SELECT * FROM users WHERE email...
// In signup.php: INSERT INTO users (user_uid, full_name, email...)
// So 'users' has 'full_name' and 'email'.

$queryActive = "
    SELECT 
        c.certificate_uid,
        c.certificate_title,
        c.issue_date,
        'Issued' as status,
        u.full_name as recipient_name,
        u.email as recipient_email
    FROM certificates c
    JOIN users u ON c.user_uid = u.user_uid
    WHERE c.verified_by = '$issuer_uid'
";

$resultActive = mysqli_query($conn, $queryActive);
if ($resultActive) {
    while ($row = mysqli_fetch_assoc($resultActive)) {
        $history[] = [
            'uid' => $row['certificate_uid'],
            'title' => $row['certificate_title'],
            'date' => $row['issue_date'],
            'status' => $row['status'],
            'recipient' => $row['recipient_name'] . " (" . $row['recipient_email'] . ")"
        ];
    }
}

// 2. Fetch from Pending Certificates (Users who don't exist yet)
$queryPending = "
    SELECT 
        certificate_uid,
        certificate_title,
        issue_date,
        email as recipient_email
    FROM pending_certificates
    WHERE issued_by = '$issuer_uid'
";

$resultPending = mysqli_query($conn, $queryPending);
if ($resultPending) {
    while ($row = mysqli_fetch_assoc($resultPending)) {
        $history[] = [
            'uid' => $row['certificate_uid'],
            'title' => $row['certificate_title'],
            'date' => $row['issue_date'],
            'status' => 'Pending Signup',
            'recipient' => $row['recipient_email']
        ];
    }
}

// Sort by Date DESC
usort($history, function($a, $b) {
    return strtotime($b['date']) - strtotime($a['date']);
});

echo json_encode([
    "status" => "success",
    "count" => count($history),
    "data" => $history
]);
