<?php
include 'db.php';
$result = mysqli_query($conn, "SHOW COLUMNS FROM users");
while($row = mysqli_fetch_assoc($result)) {
    print_r($row);
    echo "\n";
}
?>
