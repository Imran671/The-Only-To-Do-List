package com.ebookfrenzy.theonlytodolist

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private val tasks = mutableListOf<Task>()
    private val sharedPrefsKey = "task_prefs"
    private val taskListKey = "tasks"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        val editTextTask = findViewById<EditText>(R.id.editTextTask)
        val buttonAdd = findViewById<Button>(R.id.buttonAdd)

        // Load saved tasks from SharedPreferences
        loadTasks()

        // Drag & reorder support
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                val task = tasks.removeAt(fromPos)
                tasks.add(toPos, task)
                taskAdapter.notifyItemMoved(fromPos, toPos)
                saveTasks()
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Do nothing
            }
        })

        // Adapter setup
        taskAdapter = TaskAdapter(
            tasks,
            onDeleteClick = { position ->
                tasks.removeAt(position)
                taskAdapter.notifyItemRemoved(position)
                saveTasks()
            },
            onStartDrag = { viewHolder ->
                itemTouchHelper.startDrag(viewHolder)
            }
        )
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // Add task logic
        buttonAdd.setOnClickListener {
            val taskText = editTextTask.text.toString()
            if (taskText.isNotBlank()) {
                tasks.add(Task(taskText))
                taskAdapter.notifyItemInserted(tasks.size - 1)
                editTextTask.text.clear()
                saveTasks()
            }
        }
    }

    // Save list to SharedPreferences
    private fun saveTasks() {
        val sharedPrefs = getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(tasks)
        editor.putString(taskListKey, json)
        editor.apply()
    }

    // Load list from SharedPreferences
    private fun loadTasks() {
        val sharedPrefs = getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE)
        val json = sharedPrefs.getString(taskListKey, null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            val loadedTasks: MutableList<Task> = Gson().fromJson(json, type)
            tasks.addAll(loadedTasks)
        }
    }
}
