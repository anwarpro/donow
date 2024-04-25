package ui.screen.home

import data.repository.task.TaskApi
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class HomeViewModel(
    private val taskApi: TaskApi,
    val supabaseClient: SupabaseClient
) : ViewModel() {
//    private val realtimeChannel: RealtimeChannel = supabaseClient.channel("tasks")

    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> get() = _state

    private val _events: Channel<HomeEvent> = Channel(Channel.BUFFERED)
    val events: Flow<HomeEvent> get() = _events.receiveAsFlow()

    private val _actions: Channel<HomeAction> = Channel(Channel.BUFFERED)
    private val actions: Flow<HomeAction> get() = _actions.receiveAsFlow()

    init {
        viewModelScope.launch {
            actions.onEach {
                Napier.i(tag = "HomeViewModel") { "Trigger => $it" }
            }.collect {
                executeAction(it)
            }
        }

        postAction(HomeAction.GetTasks)

//        connectToRealtime()
    }

    override fun onCleared() {
//        disconnectFromRealtime()
        super.onCleared()
    }

    private fun executeAction(action: HomeAction) = viewModelScope.launch {
        when (action) {
            HomeAction.GetTasks -> {
                kotlin.runCatching {
                    taskApi.retrieveTasks()
                }.onSuccess { tasks ->
                    _state.update {
                        it.copy(tasks = tasks)
                    }
                    Napier.i(tag = "HomeViewModel") { "state => ${_state.value}" }
                }.onFailure {
                    //check for 401
                    //refresh
                    supabaseClient.auth.refreshCurrentSession()
                    Napier.e(throwable = it, tag = "HomeViewModel") { "" }
                }
            }

            is HomeAction.SaveTask -> {
                runCatching {
                    taskApi.createTask(title = action.title, description = action.description)
                }.onSuccess {
                    postAction(HomeAction.GetTasks)
                    Napier.i(tag = "HomeViewModel") { "state => ${_state.value}" }
                }.onFailure {
                    Napier.e(throwable = it, tag = "HomeViewModel") { "" }
                }
            }
        }
    }

    fun postAction(action: HomeAction) {
        _actions.trySend(action)
    }

    //Realtime
    /*fun connectToRealtime() {
        viewModelScope.launch {
            kotlin.runCatching {
                realtimeChannel.postgresChangeFlow<PostgresAction>("public") {
                    table = "tasks"
                }.onEach {
                    val currentState = _state.value
                    when (it) {
                        is PostgresAction.Delete -> {
                            _state.update { homeState ->
                                homeState.copy(
                                    tasks = currentState.tasks.filter { message -> message.id != it.oldRecord["id"]!!.jsonPrimitive.int }
                                )
                            }
                        }

                        is PostgresAction.Insert -> {
                            _state.update { homeState ->
                                homeState.copy(
                                    tasks = currentState.tasks.toMutableList().apply {
                                        add(it.decodeRecord<Task>())
                                    }
                                )
                            }
                        }

                        is PostgresAction.Select -> error("Select should not be possible")
                        is PostgresAction.Update -> error("Update should not be possible")
                    }
                }.launchIn(viewModelScope)

                realtimeChannel.subscribe()
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun disconnectFromRealtime() {
        viewModelScope.launch {
            kotlin.runCatching {
                supabaseClient.realtime.disconnect()
            }
        }
    }*/
}