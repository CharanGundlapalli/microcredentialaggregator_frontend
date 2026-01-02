<?php
header("Content-Type: application/json");
session_start();
include "db.php";

// check login
if (!isset($_SESSION['user_uid'])) {
    echo json_encode([
        "status" => "error",
        "message" => "Unauthorized"
    ]);
    exit;
}

$user_uid = $_SESSION['user_uid'];

// fetch user skills with skill names
$query = "
SELECT 
    s.skill_name,
    u.nsqf_level
FROM user_skills u
JOIN skills_master s ON u.skill_id = s.skill_id
WHERE u.user_uid = '$user_uid'
ORDER BY u.nsqf_level DESC
";

$result = mysqli_query($conn, $query);

$skills = [];

// convert NSQF → display level
function getLevelName($nsqf) {
    if ($nsqf <= 3) return "Beginner";
    if ($nsqf <= 5) return "Intermediate";
    if ($nsqf <= 7) return "Advanced";
    return "Expert";
}

while ($row = mysqli_fetch_assoc($result)) {
    $skills[] = [
        "skill_name" => $row['skill_name'],
        "nsqf_level" => (int)$row['nsqf_level'],
        "level" => getLevelName($row['nsqf_level'])
    ];
}

echo json_encode([
    "status" => "success",
    "count" => count($skills),
    "skills" => $skills
]);
