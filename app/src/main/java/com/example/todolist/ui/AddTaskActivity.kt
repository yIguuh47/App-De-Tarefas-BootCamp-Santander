package com.example.todolist.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityAddTaskBinding
import com.example.todolist.datasource.TaskDataSource
import com.example.todolist.extensions.format
import com.example.todolist.extensions.text
import com.example.todolist.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class AddTaskActivity : AppCompatActivity(){

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)){
            val taskId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findById(taskId)?.let {
                binding.etTitulo.text = it.title
                binding.etDescricao.text = it.desc
                binding.etData.text = it.date
                binding.etHora.text = it.hour
            }
        }
        insertListeners()
    }

    private fun insertListeners() {
        binding.etData.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                binding.etData.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.etHora.editText?.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()

            timePicker.addOnPositiveButtonClickListener {
                val minute =
                    if (timePicker.minute in 0..9) "0${timePicker.minute}" else timePicker.minute
                val hour = if (timePicker.hour in 0..9) "0${timePicker.hour}" else timePicker.hour
                binding.etHora.text = "$hour : $minute"
            }
            timePicker.show(supportFragmentManager, null)
        }

        binding.btnNewTask.setOnClickListener {
            val titulo = binding.etTitulo.text.toString()
            val data = binding.etData.text.toString()
            if (titulo.length != 0  && data.length != 0) {
                val task = Task(
                    title = binding.etTitulo.text,
                    desc = binding.etDescricao.text,
                    date = binding.etData.text,
                    hour = binding.etHora.text,
                    id = intent.getIntExtra(TASK_ID, 0)
                )
                TaskDataSource.insertTask(task)
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(applicationContext, "Preencha os campos requisitados", Toast.LENGTH_SHORT).show()
                binding.etTitulo.error = "Requisito!"
                binding.etData.error = "Requisito!"
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.ivNavigate.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val TASK_ID = "task_id"
    }
}