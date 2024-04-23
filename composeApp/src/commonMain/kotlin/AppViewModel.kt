import data.repository.task.Task
import data.repository.task.TaskApi
import io.github.aakira.napier.Napier
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
    private val taskApi: TaskApi
) : ViewModel() {
    val sessionStatus = supabaseClient.auth.sessionStatus
    val loginAlert = MutableStateFlow<String?>(null)
    val tasks = MutableStateFlow<List<Task>>(emptyList())

    //Auth

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

    //Interacting with the message api
    fun createMessage(title: String, description: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                taskApi.createTask(title, description)
            }.onFailure {
                Napier.e(it) { "Error while creating message" }
            }
        }
    }

    fun deleteMessage(id: Int) {
        viewModelScope.launch {
            kotlin.runCatching {
                taskApi.deleteTask(id)
            }.onFailure {
                Napier.e(it) { "Error while deleting message" }
            }
        }
    }

    fun retrieveMessages() {
        viewModelScope.launch {
            kotlin.runCatching {
                taskApi.retrieveTasks()
            }.onSuccess {
                tasks.value = it
            }.onFailure {
                Napier.e(it) { "Error while retrieving messages" }
            }
        }
    }
}