<?php
header("Content-Type: application/json");
session_start();
include "db.php";

/* ---------- AUTH CHECK ---------- */
if (!isset($_SESSION['user_uid']) || !isset($_SESSION['role'])) {
    echo json_encode([
        "status" => "error",
        "message" => "Unauthorized"
    ]);
    exit;
}

$user_uid = $_SESSION['user_uid'];
$role = $_SESSION['role'];

/* ---------- USER DASHBOARD ---------- */
if ($role === 'user') {

    $total = mysqli_fetch_assoc(
        mysqli_query($conn, "SELECT COUNT(*) AS count FROM certificates WHERE user_uid='$user_uid'")
    )['count'];

    $verified = mysqli_fetch_assoc(
        mysqli_query($conn, "SELECT COUNT(*) AS count FROM certificates 
                             WHERE user_uid='$user_uid' AND verification_status='verified'")
    )['count'];

    $pending = mysqli_fetch_assoc(
        mysqli_query($conn, "SELECT COUNT(*) AS count FROM certificates 
                             WHERE user_uid='$user_uid' AND verification_status='pending'")
    )['count'];

    echo json_encode([
        "status" => "success",
        "role" => "user",
        "total_certificates" => $total,
        "verified_certificates" => $verified,
        "pending_certificates" => $pending
    ]);
    exit;
}

/* ---------- ADMIN DASHBOARD ---------- */
if ($role === 'admin') {

    $totalUsers = mysqli_fetch_assoc(
        mysqli_query($conn, "SELECT COUNT(*) AS count FROM users WHERE status='active'")
    )['count'];

    $totalCertificates = mysqli_fetch_assoc(
        mysqli_query($conn, "SELECT COUNT(*) AS count FROM certificates")
    )['count'];

    $pendingCertificates = mysqli_fetch_assoc(
        mysqli_query($conn, "SELECT COUNT(*) AS count FROM certificates WHERE verification_status='pending'")
    )['count'];

    echo json_encode([
        "status" => "success",
        "role" => "admin",
        "total_users" => $totalUsers,
        "total_certificates" => $totalCertificates,
        "pending_certificates" => $pendingCertificates
    ]);
    exit;
}

/* ---------- EMPLOYER DASHBOARD ---------- */
if ($role === 'employer') {

    $totalUsers = mysqli_fetch_assoc(
        mysqli_query($conn, "SELECT COUNT(*) AS count FROM users WHERE status='active'")
    )['count'];

    $verifiedCertificates = mysqli_fetch_assoc(
        mysqli_query($conn, "SELECT COUNT(*) AS count FROM certificates 
                             WHERE verification_status='verified'")
    )['count'];

    echo json_encode([
        "status" => "success",
        "role" => "employer",
        "total_users" => $totalUsers,
        "verified_certificates" => $verifiedCertificates
    ]);
    exit;
}

/* ---------- ISSUER DASHBOARD (NEW) ---------- */
if ($role === 'issuer') {

    // certificates issued by this issuer
    $issued = mysqli_fetch_assoc(
        mysqli_query($conn, "
            SELECT COUNT(*) AS count 
            FROM certificates 
            WHERE source='issuer' AND verified_by='$user_uid'
        ")
    )['count'];

    // pending certificates for users not yet registered
    $pendingUsers = mysqli_fetch_assoc(
        mysqli_query($conn, "
            SELECT COUNT(*) AS count 
            FROM pending_certificates 
            WHERE issued_by='$user_uid'
        ")
    )['count'];

    echo json_encode([
        "status" => "success",
        "role" => "issuer",
        "certificates_issued" => $issued,
        "pending_user_certificates" => $pendingUsers
    ]);
    exit;
}

/* ---------- FALLBACK ---------- */
echo json_encode([
    "status" => "error",
    "message" => "Invalid role"
]);
