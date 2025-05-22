package com.ebookfrenzy.theonlytodolist

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog
import android.graphics.Paint
import androidx.recyclerview.widget.ItemTouchHelper

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onDeleteClick: (Int) -> Unit,
    private val onStartDrag: (RecyclerView.ViewHolder) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.textTaskName)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.buttonDelete)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxTask)
        val editBtn: ImageButton = itemView.findViewById(R.id.buttonEdit)
        val dragHandle: ImageButton = itemView.findViewById(R.id.dragHandle) // NEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.taskName.text = task.name

        // Checkbox logic
        holder.checkBox.isChecked = task.isCompleted
        holder.taskName.paintFlags = if (task.isCompleted)
            holder.taskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else
            holder.taskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            notifyItemChanged(position)
        }

        // Delete logic
        holder.deleteBtn.setOnClickListener {
            onDeleteClick(position)
        }

        // Edit logic
        holder.editBtn.setOnClickListener {
            val context = holder.itemView.context
            val editText = EditText(context)
            editText.setText(task.name)

            val container = FrameLayout(context).apply {
                val params = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.marginStart = 40
                params.marginEnd = 40
                editText.layoutParams = params
                addView(editText)
            }

            AlertDialog.Builder(context)
                .setTitle("Edit Task")
                .setView(container)
                .setPositiveButton("Update") { _, _ ->
                    val newName = editText.text.toString()
                    if (newName.isNotBlank()) {
                        task.name = newName
                        notifyItemChanged(position)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // 🔃 Drag handle logic
        holder.dragHandle.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                onStartDrag(holder)
            }
            false
        }
    }

    override fun getItemCount(): Int = tasks.size
}
