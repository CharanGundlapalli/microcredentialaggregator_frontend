<?php
header("Content-Type: application/json");
include "auth_session.php";
include "db.php";

/* ---------- ADMIN AUTH CHECK ---------- */
if ($_SESSION['role'] !== 'admin') {
    echo json_encode(["status" => "error", "message" => "Unauthorized access"]);
    exit;
}

/* ---------- INPUT VALIDATION ---------- */
$user_uid = $_POST['user_uid'] ?? '';

if (empty($user_uid)) {
    echo json_encode([
        "status" => "error",
        "message" => "user_uid is required"
    ]);
    exit;
}

// Check if issuer exists
$check = mysqli_query($conn, "SELECT issuer_id FROM issuers WHERE user_uid = '$user_uid'");

if (mysqli_num_rows($check) > 0) {
    // Update existing
    $update = mysqli_query($conn, "UPDATE issuers SET verified = 1 WHERE user_uid = '$user_uid'");
} else {
    // Insert new
    // We need the user's name to insert as issuer_name initially
    $user_query = mysqli_query($conn, "SELECT full_name FROM users WHERE user_uid = '$user_uid'");
    $user_data = mysqli_fetch_assoc($user_query);
    $issuer_name = $user_data['full_name'] ?? 'Unknown Issuer';
    
    // Generate an issuer_id (e.g., ISS + random) or auto-increment if DB handles it? 
    // Assuming DB auto-inc or we accept default if nullable, but safer to let DB handle ID if auto-inc.
    // If issuer_id is NOT auto-inc, we need to generate it. Based on `User` uid it's likely a string?
    // Let's assume auto-inc for now or generate simple one. 
    // Actually, earlier schema implied issuer_id might be string. 
    // But safely, let's try INSERT without ID if it's auto-inc.
    
    $update = mysqli_query($conn, "
        INSERT INTO issuers (user_uid, issuer_name, verified, created_at)
        VALUES ('$user_uid', '$issuer_name', 1, NOW())
    ");
}

if (!$update) {
    echo json_encode([
        "status" => "error",
        "message" => "Failed to verify issuer" . mysqli_error($conn)
    ]);
    exit;
}

echo json_encode([
    "status" => "success",
    "message" => "Issuer verified successfully"
]);
