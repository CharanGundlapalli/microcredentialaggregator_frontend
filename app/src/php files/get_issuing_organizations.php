<?php
header("Content-Type: application/json");
include "db.php";

// fetch only verified issuers
$result = mysqli_query($conn, "
    SELECT issuer_name 
    FROM issuers 
    WHERE verified = 1
    ORDER BY issuer_name ASC
");

$issuers = [];

while ($row = mysqli_fetch_assoc($result)) {
    $issuers[] = $row['issuer_name'];
}

echo json_encode([
    "status" => "success",
    "count" => count($issuers),
    "issuers" => $issuers
]);
