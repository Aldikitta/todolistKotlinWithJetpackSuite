package com.aldikitta.modernmvvmtodohiltflowroomjetpack.ui.addEditTask

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aldikitta.modernmvvmtodohiltflowroomjetpack.R
import com.aldikitta.modernmvvmtodohiltflowroomjetpack.databinding.FragmentAddEditTaskBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditTaskFragment: Fragment(R.layout.fragment_add_edit_task) {
    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            editTextTaskName.setText(viewModel.taskName)
            checkboxImportant.isChecked = viewModel.taskImportant
            checkboxImportant.jumpDrawablesToCurrentState()
            textViewDateCreated.isVisible = viewModel.task != null
            textViewDateCreated.text = "Created: ${viewModel.task?.createdDateFormatted}"
        }
    }
}