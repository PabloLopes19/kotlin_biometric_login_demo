
### Introdução

Esse aplicativo foi criado com o objetivo de servir de base para uma possível implementação de uma funcionalidade de autenticação com biometria em algum projeto futuro. O objetivo é simples, apenas implementar o funcionamento de, clicar em um botão dedicado para abrir um dialog informando o usuário para posicionar o dedo no leitor de digitais do dispositivo e retornar uma resposta positiva ou negativa daquele procedimento.

### Libs

Para implementar isso, foi necessário a inclusão de duas dependências no `build.gradle`. Elas são da própria Google e possui uma [documentação](https://developer.android.com/jetpack/androidx/releases/biometric?hl=pt-br) na plataforma de developers do Android.

``` Kotlin
// build.gradle

implementation("androidx.biometric:biometric:1.2.0-alpha04")  
implementation("androidx.compose.ui:ui:1.5.1")
```

Também precisei adicionar o `compat` para conseguir usar uma `FragmentActivity` como `MainActivity`. Segue abaixo a implementação do `build.gradle` e como ficou a activity depois.
#### Implementação

``` kotlin
// build.gradle

implementation("androidx.appcompat:appcompat:1.7.0")
```

``` kotlin
// MainActivity.kt

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
```

### Resultado

Com o ambiente configurado, um arquivo foi criado para conter os principais métodos que vão ser utilizados no `Composable`. No geral, são métodos para verificar se a biometria está disponível no dispositivo, um para de fato fazer as verificações necessárias, e outro para especificar as propriedades do `dialog` que vai ser exibido ao usuário.

``` kotlin
// BiometricAuthenticator.kt

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
        override fun onAuthenticationSucceeded(
	        result: BiometricPrompt.AuthenticationResult
	    ) {  
            super.onAuthenticationSucceeded(result)  
            onAuthenticationSuccess()  
        }  
  
        override fun onAuthenticationError(
	        errorCode: Int, 
	        errString: CharSequence
	    ) {  
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
```

Por fim, são criados os `states` dentro do `Composable` junto dos componentes da UI, e algumas tratativas de erros que podem acontecer, ou de comportamentos esperados da tela.

``` kotlin
// MainActivity.kt

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
```

### Fontes

[How to Implement Biometric Auth in Your Android App - Phillip Lackener](https://www.youtube.com/watch?v=_dCRQ9wta-I)
[Documentação Android Developers](https://developer.android.com/identity/sign-in/biometric-auth)
