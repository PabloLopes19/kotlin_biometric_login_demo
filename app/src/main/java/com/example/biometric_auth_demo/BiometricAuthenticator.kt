package com.example.biometric_auth_demo

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executors

fun isBiometricAvailable(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    return biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS
}

fun biometricAuthenticator (
    activity: FragmentActivity,
    onAuthenticationSuccess: () -> Unit,
    onAuthenticationError: (String) -> Unit
): BiometricPrompt {
    val executor = Executors.newSingleThreadExecutor()

    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onAuthenticationSuccess()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationFailed()
            onAuthenticationError(errString.toString())
        }
    }

    return BiometricPrompt(activity, executor, callback)
}

fun createPromptInfo(context: Context): PromptInfo {
    return PromptInfo.Builder()
        .setTitle(context.getString(R.string.auth_dialog_title))
        .setSubtitle(context.getString(R.string.auth_dialog_subtitle))
        .setNegativeButtonText(context.getString(R.string.auth_dialog_cancel))
        .build()
}