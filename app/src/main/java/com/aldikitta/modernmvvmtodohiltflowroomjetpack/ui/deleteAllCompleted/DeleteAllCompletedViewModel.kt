package com.aldikitta.modernmvvmtodohiltflowroomjetpack.ui.deleteAllCompleted

import androidx.lifecycle.ViewModel
import com.aldikitta.modernmvvmtodohiltflowroomjetpack.data.TaskDao
import com.aldikitta.modernmvvmtodohiltflowroomjetpack.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllCompletedViewModel @Inject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {
    fun onConfirmClick() = applicationScope.launch {
        taskDao.deleteCompletedTask()
    }
}