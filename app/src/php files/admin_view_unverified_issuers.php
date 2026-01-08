<?php
header("Content-Type: application/json");
include "auth_session.php";
include "db.php";

/* ---------- ADMIN AUTH CHECK ---------- */
if ($_SESSION['role'] !== 'admin') {
    echo json_encode(["status" => "error", "message" => "Unauthorized access"]);
    exit;
}

/* ---------- FETCH UNVERIFIED ISSUERS (MINIMAL FIELDS) ---------- */
$query = "
    SELECT
        u.user_uid,
        COALESCE(i.issuer_name, u.full_name) as issuer_name,
        i.issuer_id,
        u.created_at
    FROM users u
    LEFT JOIN issuers i ON u.user_uid = i.user_uid
    WHERE u.role = 'issuer' AND (i.verified = 0 OR i.verified IS NULL)
    ORDER BY u.created_at DESC
";

$result = mysqli_query($conn, $query);

if (!$result) {
    echo json_encode([
        "status" => "error",
        "message" => "Database error"
    ]);
    exit;
}

$issuers = [];

while ($row = mysqli_fetch_assoc($result)) {
    $issuers[] = [
        "issuer_id"   => $row['issuer_id'], // Might be null
        "user_uid"    => $row['user_uid'],
        "issuer_name" => $row['issuer_name'],
        "created_at"  => $row['created_at']
    ];
}

/* ---------- RESPONSE ---------- */
echo json_encode([
    "status" => "success",
    "count" => count($issuers),
    "issuers" => $issuers
]);
