package com.finanteq.exercise.ui.presentation.add_new_task

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.finanteq.exercise.R
import com.finanteq.exercise.models.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class AddNewTask : Fragment() {

    private val viewModel: AddNewTaskViewModel by viewModel()
    private lateinit var taskName: EditText
    private lateinit var taskCategory: Spinner
    private lateinit var taskDate: EditText
    private lateinit var addButton: Button
    private lateinit var cancelButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_new_task_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initEditTexts()
        initButtons()
        initSpinner()
        setInputTypes()

        taskDate.setOnClickListener{ showDateDialog(taskDate) }
        cancelButton.setOnClickListener{ findNavController().popBackStack(R.id.taskListFragment, false) }
        addButton.setOnClickListener{
            if(taskName.text.isEmpty() || taskDate.text.isEmpty() || taskCategory.selectedItem.toString().isEmpty()){
                callError()
            }else{
                insertData()
            }
        }

    }

    private fun callError() {
        val alert = AlertDialog.Builder(context, R.style.DialogStyle)
        alert.setTitle("Alert !")
        alert.setMessage("Error occured when adding task to database. Do you want to try again ?")
        alert.setNegativeButton("No") { dialog, _ ->
            findNavController().popBackStack(R.id.taskListFragment, false)
            dialog.dismiss()
        }
        alert.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
        }
        alert.show()
    }

    private fun insertData() {
        lifecycleScope.launch(Dispatchers.IO){
            val date: Date = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(taskDate.text.toString())
            val task: Task = Task(taskName.text.toString(), date, taskCategory.selectedItem.toString())
            viewModel.insertTask(task)
        }
        findNavController().popBackStack(R.id.taskListFragment, false)
        Toast.makeText(context, "Task added successfully!", Toast.LENGTH_LONG).show()
    }

    private fun showDateDialog(taskDate: EditText) {
        val calendar: Calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
                taskDate.setText(format.format(calendar.time))
            }
            TimePickerDialog(context, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
        DatePickerDialog(requireContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun initEditTexts(){
        taskName = view?.findViewById(R.id.add_name)!!
        taskDate = view?.findViewById(R.id.add_date)!!
    }

    private fun initButtons(){
        addButton = view?.findViewById(R.id.add_new_task_button)!!
        cancelButton = view?.findViewById(R.id.cancel_button)!!
    }

    private fun initSpinner(){
        taskCategory = view?.findViewById(R.id.add_category)!!
        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.add_category_content, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        taskCategory.adapter = adapter
    }

    private fun setInputTypes(){
        taskName.inputType = InputType.TYPE_CLASS_TEXT
        taskDate.inputType = InputType.TYPE_NULL
    }

}