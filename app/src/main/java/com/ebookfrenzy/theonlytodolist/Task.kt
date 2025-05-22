package com.ebookfrenzy.theonlytodolist

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    var name: String,
    var isCompleted: Boolean = false,
    var isPriority: Boolean = false
) : Parcelable
