<?php
header("Content-Type: application/json");
include "auth_session.php";
include "db.php";
include "attach_pending_certificates.php";

/* ---------- AUTH CHECK ---------- */
// auth_session.php handles basic auth check.

$user_uid = $_SESSION['user_uid'];

/* ---------- FETCH EMAIL ---------- */
// Since email is not in session, get it from DB
$userQuery = mysqli_query($conn, "SELECT email FROM users WHERE user_uid = '$user_uid'");
$userData = mysqli_fetch_assoc($userQuery);

if (!$userData) {
    echo json_encode([
        "status" => "error",
        "message" => "User not found"
    ]);
    exit;
}

$email = $userData['email'];

/* ---------- ATTACH CERTIFICATES ---------- */
// This function returns the number of certificates attached
$count = attachPendingCertificates($conn, $user_uid, $email);

if ($count > 0) {
    echo json_encode([
        "status" => "success",
        "message" => "Found and added $count certificate(s).",
        "count" => $count
    ]);
} else {
    echo json_encode([
        "status" => "success", // Success because the check worked, even if result is 0
        "message" => "No pending certificates found.",
        "count" => 0
    ]);
}
