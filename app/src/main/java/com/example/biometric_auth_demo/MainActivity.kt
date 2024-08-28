package com.example.biometric_auth_demo

import android.os.Bundle
import android.provider.CalendarContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.biometric_auth_demo.ui.theme.Biometric_auth_demoTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Biometric_auth_demoTheme {
                BiometricAuthenticationScreen()
            }
        }
    }
}

@Composable
fun BiometricAuthenticationScreen() {
    val context = LocalContext.current
    val activity = context as FragmentActivity

    var authenticationSuccess by remember { mutableStateOf(false) }
    var authenticationError by remember { mutableStateOf<String?>(null) }

    val biometricPrompt = remember {
        biometricAuthenticator(
            activity,
            onAuthenticationSuccess = { authenticationSuccess = true },
            onAuthenticationError = { authenticationError = it }
        )
    }

    val promptInfo = remember { createPromptInfo(context) }

    if(!isBiometricAvailable(context)) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(stringResource(R.string.unsupported_biometric), color = Color.White)
        }
    }
    else {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(stringResource(R.string.app_title), color = Color.White)
            Button(
                onClick = {
                    biometricPrompt.authenticate(promptInfo)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.login_button_label))
            }

            authenticationError?.let {
                Text(
                    stringResource(R.string.biometric_error),
                    color = Color.Red
                )
            }

            if(authenticationSuccess) {
                Text(
                    stringResource(R.string.biometric_success),
                    color = Color.Green
                )
            }
        }
    }
}