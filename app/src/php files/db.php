<?php
$host = "localhost";
$user = "root";
$password = "";
$database = "micro_credential_db";

$conn = mysqli_connect($host, $user, $password, $database);

if (!$conn) {
    die(json_encode([
        "status" => "error",
        "message" => "Database connection failed"
    ]));
}
?>
