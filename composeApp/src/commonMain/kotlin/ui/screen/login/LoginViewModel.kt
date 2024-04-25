package ui.screen.login

import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class LoginViewModel(
    val supabaseClient: SupabaseClient
) : ViewModel() {
    val sessionStatus = supabaseClient.auth.sessionStatus

    private val _state: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> get() = _state

    private val _events: Channel<LoginEvent> = Channel(Channel.BUFFERED)
    val events: Flow<LoginEvent> get() = _events.receiveAsFlow()

    private val _actions: Channel<LoginAction> = Channel(Channel.BUFFERED)
    private val actions: Flow<LoginAction> get() = _actions.receiveAsFlow()

    init {
        viewModelScope.launch {
            actions.onEach {
                Napier.i(tag = "LoginViewModel") { "Trigger => $it" }
            }.collect {
                executeAction(it)
            }
        }
    }

    fun postAction(action: LoginAction) = viewModelScope.launch {
        _actions.trySend(action)
    }

    private fun executeAction(action: LoginAction) = viewModelScope.launch {
        kotlin.runCatching {
            when (action) {
                is LoginAction.Login -> {
                    supabaseClient.auth.signInWith(Email) {
                        this.email = action.email
                        this.password = action.password
                    }
                }

                LoginAction.LoginWithGoogle -> {
                    
                }
            }
        }.onSuccess {
            Napier.i(tag = "LoginViewModel") { "success => $action" }
        }.onFailure {
            Napier.e(throwable = it, tag = "LoginViewModel") { "" }
        }
    }
}