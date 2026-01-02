package com.example.microcredential.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "MicroCredentialSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_UID = "user_uid";
    private static final String KEY_ROLE = "role";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_SESSION_ID = "sessionId";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Save login session
    public void createLoginSession(String userUid, String role, String fullName, String sessionId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_UID, userUid);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_SESSION_ID, sessionId);
        editor.apply();
    }

    // Check login
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Get user UID
    public String getUserUid() {
        return pref.getString(KEY_USER_UID, null);
    }

    // Get role
    public String getRole() {
        return pref.getString(KEY_ROLE, null);
    }

    // Get full name
    public String getFullName() {
        return pref.getString(KEY_FULL_NAME, null);
    }

    // Get session ID
    public String getSessionId() {
        return pref.getString(KEY_SESSION_ID, null);
    }

    // Logout
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
