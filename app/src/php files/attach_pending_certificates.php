<?php
/**
 * attach_pending_certificates.php
 * 
 * This file is called AFTER a user successfully signs up.
 * It checks if any issuer-issued certificates exist for the user's email
 * and automatically attaches them to the user's account.
 */

include "db.php";
include "apply_skill_mapping_direct.php";

/**
 * Attach pending issuer certificates to newly registered user
 *
 * @param mysqli $conn
 * @param string $user_uid
 * @param string $email
 */
function attachPendingCertificates($conn, $user_uid, $email) {

    // Fetch pending certificates for this email
    $query = mysqli_query($conn, "
        SELECT * FROM pending_certificates
        WHERE email = '$email'
    ");

    if (mysqli_num_rows($query) === 0) {
        return; // No pending certificates
    }

    while ($row = mysqli_fetch_assoc($query)) {

        // Insert into main certificates table (AUTO-VERIFIED)
        $stmt = $conn->prepare("
            INSERT INTO certificates (
                certificate_uid,
                user_uid,
                certificate_title,
                certificate_type,
                issuing_organization,
                issue_date,
                expiry_date,
                credential_id,
                certificate_file,
                source,
                verification_status,
                verified_by,
                verified_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'issuer', 'verified', ?, NOW())
        ");

        $stmt->bind_param(
            "ssssssssss",
            $row['certificate_uid'],
            $user_uid,
            $row['certificate_title'],
            $row['certificate_type'],
            $row['issuing_organization'],
            $row['issue_date'],
            $row['expiry_date'],
            $row['credential_id'],
            $row['certificate_file'],
            $row['issued_by']
        );

        $stmt->execute();

        // 🔥 Apply skill mapping immediately (trusted issuer)
        applySkillMappingDirect(
            $conn,
            $row['issuing_organization'],
            $row['certificate_title'],
            $user_uid
        );

        // Remove from pending table
        mysqli_query($conn, "
            DELETE FROM pending_certificates
            WHERE id = {$row['id']}
        ");
    }
}
