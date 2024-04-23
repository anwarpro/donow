package ui.screen

sealed interface LoginAction {
    data class Login(val email: String, val password: String) : LoginAction
}

sealed interface LoginEvent {
    data object LoginSuccess : LoginEvent
}

data class LoginState(
    val isLoading: Boolean = false
)