<?php
session_start();
include "db.php";

if (isset($_SESSION['user_uid'])) {
    $user_uid = $_SESSION['user_uid'];
    $session_token = session_id();

    mysqli_query($conn, "
        UPDATE login_logs 
        SET logout_time = NOW()
        WHERE user_uid='$user_uid' AND session_token='$session_token'
    ");
}

session_destroy();

echo json_encode([
    "status" => "success",
    "message" => "Logged out successfully"
]);
