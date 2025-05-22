package com.ebookfrenzy.theonlytodolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog
import android.widget.EditText
import android.widget.FrameLayout
import android.graphics.Paint
class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.textTaskName)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.buttonDelete)
        val checkBox: android.widget.CheckBox = itemView.findViewById(R.id.checkBoxTask)
        val editBtn: ImageButton = itemView.findViewById(R.id.buttonEdit)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        val task = tasks[position]
        // Set checkbox state and strike-through
        holder.checkBox.isChecked = task.isCompleted
        holder.taskName.paintFlags = if (task.isCompleted)
            holder.taskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else
            holder.taskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

        // Toggle checkbox state
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            notifyItemChanged(position)
        }
        holder.taskName.text = task.name



        holder.deleteBtn.setOnClickListener {
            onDeleteClick(position)
        }

        holder.editBtn.setOnClickListener {
            val context = holder.itemView.context

            val editText = EditText(context)
            editText.setText(task.name)

            val container = FrameLayout(context)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.marginStart = 40
            params.marginEnd = 40
            editText.layoutParams = params
            container.addView(editText)

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
    }

    override fun getItemCount(): Int = tasks.size
}
