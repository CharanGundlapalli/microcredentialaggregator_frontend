<?php
header("Content-Type: application/json");
include "auth_session.php";
include "db.php";

/* ---------- AUTH CHECK ---------- */
// auth_session handles check

$user_uid = $_SESSION['user_uid'];
$data = json_decode(file_get_contents("php://input"), true);

$full_name = $data['full_name'] ?? '';

if (empty($full_name)) {
    echo json_encode(["status" => "error", "message" => "Name cannot be empty"]);
    exit;
}

// sanitize
$full_name = mysqli_real_escape_string($conn, $full_name);

// update
$query = "UPDATE users SET full_name='$full_name' WHERE user_uid='$user_uid'";

if (mysqli_query($conn, $query)) {
    // Also update issuer name if it's an issuer (although issuers can't edit via UI, good to have sync if they could)
    // Actually, requirement says Issuers can't edit. 
    // But if Admin edits their profile? Admin might use a different endpoint.
    // For now, this is for self-edit.
    
    echo json_encode(["status" => "success", "message" => "Profile updated"]);
} else {
    echo json_encode(["status" => "error", "message" => "Update failed"]);
}
?>
