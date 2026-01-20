package com.simats.microcredential.utils;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

public class BiometricHelper {

    public interface BiometricCallback {
        void onSuccess();

        void onFailure();
    }

    public static void authenticate(FragmentActivity activity, BiometricCallback callback) {
        Executor executor = ContextCompat.getMainExecutor(activity);

        BiometricManager biometricManager = BiometricManager.from(activity);
        switch (biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                // proceed
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Fallback or error
                Toast.makeText(activity, "Biometric authentication not available", Toast.LENGTH_SHORT).show();
                return;
        }

        BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(activity, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                        callback.onFailure();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        callback.onSuccess();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(activity, "Authentication failed", Toast.LENGTH_SHORT).show();
                        callback.onFailure();
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock App")
                .setSubtitle("Log in using your biometric credential")
                // .setNegativeButtonText("Cancel") // Cannot use NegativeButton with
                // DEVICE_CREDENTIAL
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG
                        | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}
