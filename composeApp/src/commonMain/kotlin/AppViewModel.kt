import data.repository.task.TaskApi
import data.repository.task.TaskRecord
import io.github.aakira.napier.Napier
import io.github.agrevster.pocketbaseKotlin.PocketbaseClient
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class AppViewModel(
    val supabaseClient: SupabaseClient,
    val pocketbaseClient: PocketbaseClient,
    private val taskApi: TaskApi
) : ViewModel() {
    val sessionStatus = supabaseClient.auth.sessionStatus
    val loginAlert = MutableStateFlow<String?>(null)
    val tasks = MutableStateFlow<List<TaskRecord>>(emptyList())

    //Auth
    val authFlow = pocketbaseClient.authStore.authFlow

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                supabaseClient.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
            }.onSuccess {
                loginAlert.value =
                    "Successfully registered! Check your E-Mail to verify your account."
            }.onFailure {
                loginAlert.value = "There was an error while registering: ${it.message}"
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                supabaseClient.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
            }.onFailure {
                it.printStackTrace()
                loginAlert.value =
                    "There was an error while logging in. Check your credentials and try again."
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            kotlin.runCatching {
                supabaseClient.auth.signInWith(Google)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            kotlin.runCatching {
                supabaseClient.auth.signOut()
                tasks.value = emptyList()
            }
        }
    }

    fun disconnectFromRealtime() {
        viewModelScope.launch {
            kotlin.runCatching {
                supabaseClient.realtime.disconnect()
            }
        }
    }
}