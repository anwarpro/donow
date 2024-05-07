package ui.screen.login

import io.github.aakira.napier.Napier
import io.github.agrevster.pocketbaseKotlin.PocketbaseClient
import io.github.agrevster.pocketbaseKotlin.Untested
import io.github.agrevster.pocketbaseKotlin.dsl.login
import io.github.agrevster.pocketbaseKotlin.models.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
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
    val supabaseClient: SupabaseClient,
    val pocketbaseClient: PocketbaseClient
) : ViewModel() {
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

    @OptIn(Untested::class)
    private fun executeAction(action: LoginAction) = viewModelScope.launch {
        kotlin.runCatching {
            when (action) {
                is LoginAction.Login -> {
                    //Using the admin auth service to log in
                    val authData = pocketbaseClient.records.authWithPassword<User>(
                        collection = "users",
                        email = action.email,
                        password = action.password
                    )

                    pocketbaseClient.login { token = authData.token }
                }

                LoginAction.LoginWithGoogle -> {
                    val loginToken = pocketbaseClient.records.authWithOauth2<User>(
                        "collectionName",
                        "provider",
                        "code",
                        "codeVerifier",
                        "redirectUrl"
                    ).token

                    pocketbaseClient.login { token = loginToken }
                }
            }
        }.onSuccess {
            Napier.i(tag = "LoginViewModel") { "success => $action" }
        }.onFailure {
            Napier.e(throwable = it, tag = "LoginViewModel") { "" }
        }
    }
}