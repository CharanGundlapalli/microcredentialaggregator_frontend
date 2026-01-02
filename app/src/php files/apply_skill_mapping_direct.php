<?php
function applySkillMappingDirect($conn, $issuer, $certificate_title, $user_uid) {

    // fetch skill mappings
    $mapQuery = mysqli_query($conn, "
        SELECT skill_id, nsqf_level
        FROM certificate_skill_mapping
        WHERE issuer = '$issuer'
          AND certificate_title = '$certificate_title'
    ");

    if (mysqli_num_rows($mapQuery) === 0) {
        return; // no mapping exists
    }

    while ($map = mysqli_fetch_assoc($mapQuery)) {

        $skill_id = $map['skill_id'];
        $nsqf = $map['nsqf_level'];

        // check existing user skill
        $check = mysqli_query($conn, "
            SELECT nsqf_level FROM user_skills
            WHERE user_uid = '$user_uid'
              AND skill_id = $skill_id
        ");

        if (mysqli_num_rows($check) > 0) {
            $existing = mysqli_fetch_assoc($check);

            // keep highest level
            if ($nsqf > $existing['nsqf_level']) {
                mysqli_query($conn, "
                    UPDATE user_skills
                    SET nsqf_level = $nsqf
                    WHERE user_uid = '$user_uid'
                      AND skill_id = $skill_id
                ");
            }
        } else {
            mysqli_query($conn, "
                INSERT INTO user_skills (user_uid, skill_id, nsqf_level)
                VALUES ('$user_uid', $skill_id, $nsqf)
            ");
        }
    }
}
