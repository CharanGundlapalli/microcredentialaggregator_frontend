package com.example.microcredential;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.microcredential.utils.AppLockManager;
import com.example.microcredential.utils.BiometricHelper;

public class MyApplication extends Application implements LifecycleObserver {

    private Activity currentActivity;
    private boolean isAuthenticating = false;

    @Override
    public void onCreate() {
        super.onCreate();
        com.example.microcredential.utils.ThemeHelper.applyTheme(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                currentActivity = activity;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                currentActivity = activity;
                checkAppLock(activity);
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        AppLockManager appLockManager = AppLockManager.getInstance(this);
        if (appLockManager.isAppLockEnabled()) {
            appLockManager.setAppLocked(true);
            if (currentActivity != null) {
                checkAppLock(currentActivity);
            }
        }
    }

    private void checkAppLock(Activity activity) {
        if (activity instanceof SplashActivity)
            return; // Ignore Splash

        AppLockManager appLockManager = AppLockManager.getInstance(this);
        if (appLockManager.isAppLockEnabled() && appLockManager.isAppLocked() && !isAuthenticating) {
            if (activity instanceof FragmentActivity) {
                isAuthenticating = true;
                BiometricHelper.authenticate((FragmentActivity) activity,
                        new BiometricHelper.BiometricCallback() {
                            @Override
                            public void onSuccess() {
                                isAuthenticating = false;
                                appLockManager.setAppLocked(false);
                            }

                            @Override
                            public void onFailure() {
                                isAuthenticating = false;
                                activity.moveTaskToBack(true);
                            }
                        });
            }
        }
    }
}
