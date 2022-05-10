package com.aldikitta.modernmvvmtodohiltflowroomjetpack.ui.tasks

import androidx.lifecycle.*
import com.aldikitta.modernmvvmtodohiltflowroomjetpack.data.PreferencesManager
import com.aldikitta.modernmvvmtodohiltflowroomjetpack.data.SortOrder
import com.aldikitta.modernmvvmtodohiltflowroomjetpack.data.Task
import com.aldikitta.modernmvvmtodohiltflowroomjetpack.data.TaskDao
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksEventChannel = Channel<TaskEvent>()

    val tasksEvent = tasksEventChannel.receiveAsFlow()


    private val tasksFlow =
        combine(searchQuery.asFlow(), preferencesFlow) { query, filterPreferences ->
            Pair(query, filterPreferences)
        }.flatMapLatest { (query, filterPreferences) ->
            taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
        }

    val tasks = tasksFlow.asLiveData()


    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    infix fun onTaskSelected(task: Task) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }

    sealed class TaskEvent {
        object NavigateToAddTaskScreen: TaskEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TaskEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
    }
}

