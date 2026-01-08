<?php
/**
 * auth_session.php
 * 
 * Centralized session management.
 * 1. Starts the session.
 * 2. Enforces a strict inactivity timeout.
 * 3. Updates the last activity timestamp.
 * 
 * To change the timeout duration, ONLY edit the $TIMEOUT value below.
 */

// 1. SET TIMEOUT DURATION (in seconds)
// 10 minutes = 600 seconds
// TESTING: 20 seconds
$TIMEOUT = 20; 

session_start();

// 2. CHECK IF LOGGED IN
if (!isset($_SESSION['user_uid'])) {
    echo json_encode([
        "status" => "error",
        "message" => "Unauthorized"
    ]);
    exit;
}

// 3. CHECK TIMEOUT
if (isset($_SESSION['last_activity']) && (time() - $_SESSION['last_activity'] > $TIMEOUT)) {
    // Session expired
    session_unset();     // Unset $_SESSION variable for the run-time 
    session_destroy();   // Destroy session data in storage
    
    echo json_encode([
        "status" => "session_expired", // Distinct status for app to handle
        "message" => "Session expired due to inactivity. Please login again."
    ]);
    exit;
}

// 4. UPDATE LAST ACTIVITY TIMESTAMP
$_SESSION['last_activity'] = time();
?>
