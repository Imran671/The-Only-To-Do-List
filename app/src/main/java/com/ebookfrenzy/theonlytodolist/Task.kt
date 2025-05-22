package com.ebookfrenzy.theonlytodolist

data class Task(
    var name: String,
    var isCompleted: Boolean = false,
    var isPriority: Boolean = false
)
