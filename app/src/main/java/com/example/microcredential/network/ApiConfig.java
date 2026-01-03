package com.example.microcredential.network;

public class ApiConfig {

    // ⚠️ Change this according to where backend is hosted
    // For local XAMPP (Android Emulator)
    public static final String BASE_URL = "https://868n8vnb-80.inc1.devtunnels.ms/microcredentialaggregator/";

    // API Endpoints
    public static final String LOGIN_URL = BASE_URL + "login.php";
    public static final String SIGNUP_URL = BASE_URL + "signup.php";
    public static final String DASHBOARD_STATS_URL = BASE_URL + "dashboard_stats.php";
    public static final String LOGOUT_URL = BASE_URL + "logout.php";
    public static final String GET_ISSUERS_URL = BASE_URL + "get_issuing_organizations.php";
    public static final String UPLOAD_CERTIFICATE_URL = BASE_URL + "upload_certificate.php";
    public static final String VIEW_MY_CERTIFICATES_URL = BASE_URL + "view_my_certificates.php";
    public static final String VIEW_CERTIFICATE_DETAILS_URL = BASE_URL + "view_certificate_details.php";
    public static final String REMOVE_CERTIFICATE_URL = BASE_URL + "remove_certificate.php";
    public static final String ADMIN_GET_USERS_URL = BASE_URL + "admin_get_users_issuers.php";
    public static final String ADMIN_VIEW_USER_DETAILS_URL = BASE_URL + "admin_view_user_details.php";
    public static final String UPDATE_USER_STATUS_URL = BASE_URL + "update_user_status.php";
    public static final String ADMIN_VIEW_USER_CERTIFICATES_URL = BASE_URL + "admin_view_user_certificates.php";
    public static final String VERIFY_CERTIFICATE_URL = BASE_URL + "verify_certificate.php";
}
