package com.example.microcredential.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppLockManager {
    private static final String PREF_NAME = "AppLockPrefs";
    private static final String KEY_IS_APP_LOCK_ENABLED = "is_app_lock_enabled";

    private static AppLockManager instance;
    private SharedPreferences prefs;

    // Runtime state
    private boolean isAppLocked = false;

    private AppLockManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized AppLockManager getInstance(Context context) {
        if (instance == null) {
            instance = new AppLockManager(context);
        }
        return instance;
    }

    public boolean isAppLockEnabled() {
        return prefs.getBoolean(KEY_IS_APP_LOCK_ENABLED, true);
    }

    public void setAppLockEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_IS_APP_LOCK_ENABLED, enabled).apply();
    }

    public boolean isAppLocked() {
        return isAppLocked;
    }

    public void setAppLocked(boolean locked) {
        isAppLocked = locked;
    }
}
