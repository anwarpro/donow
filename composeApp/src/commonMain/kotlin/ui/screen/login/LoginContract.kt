package ui.screen.login

sealed interface LoginAction {
    data class Login(val email: String, val password: String) : LoginAction
    data object LoginWithGoogle : LoginAction
}

sealed interface LoginEvent {
    data object LoginSuccess : LoginEvent
}

data class LoginState(
    val isLoading: Boolean = false
)