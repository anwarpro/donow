package ui.screen.home

import data.repository.task.Task

sealed interface HomeAction {
    data object GetTasks : HomeAction
    data class SaveTask(val title: String, val description: String) : HomeAction
}

sealed interface HomeEvent {

}

data class HomeState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList()
)