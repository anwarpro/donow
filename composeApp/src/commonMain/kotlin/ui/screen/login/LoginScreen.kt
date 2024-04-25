package ui.screen.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.compose.auth.ui.AuthForm
import io.github.jan.supabase.compose.auth.ui.LocalAuthState
import io.github.jan.supabase.compose.auth.ui.ProviderButtonContent
import io.github.jan.supabase.compose.auth.ui.email.OutlinedEmailField
import io.github.jan.supabase.compose.auth.ui.password.OutlinedPasswordField
import io.github.jan.supabase.compose.auth.ui.password.PasswordRule
import io.github.jan.supabase.compose.auth.ui.password.rememberPasswordRuleList
import io.github.jan.supabase.gotrue.providers.Google
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.koin.koinViewModel

@Composable
fun LoginScreen() {
    val loginViewModel: LoginViewModel = koinViewModel(vmClass = LoginViewModel::class)
    val state by loginViewModel.state.collectAsStateWithLifecycle()

    LoginScreenContent(
        state = state
    ) {
        loginViewModel.postAction(it)
    }
}

@OptIn(SupabaseExperimental::class, ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    state: LoginState,
    postAction: (LoginAction) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AuthForm {
            var password by remember { mutableStateOf("34722645") }
            var email by remember { mutableStateOf("anwar@programming-hero.com") }
            var phone by remember { mutableStateOf("") }

            val authState = LocalAuthState.current

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedEmailField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-Mail") },
                    mandatory = email.isNotBlank() //once an email is entered, it is mandatory. (which enable validation)
                )

                /*OutlinedPhoneField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") }
                )*/

                OutlinedPasswordField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    rules = rememberPasswordRuleList(
                        PasswordRule.minLength(6)
                    )
                )

                /* FormComponent("accept_terms") { valid ->
                     Row(
                         verticalAlignment = Alignment.CenterVertically,
                     ) {
                         Checkbox(
                             checked = valid.value,
                             onCheckedChange = { valid.value = it },
                         )
                         Text("Accept Terms")
                     }
                 }*/

                Button(
                    onClick = {
                        postAction(LoginAction.Login(email = email, password = password))
                    }, //Login with email and password,
                    enabled = authState.validForm,
                ) {
                    Text("Login")
                }
                OutlinedButton(
                    onClick = {}, //Login with Google,
                    content = { ProviderButtonContent(Google) }
                )
            }
        }
    }
}