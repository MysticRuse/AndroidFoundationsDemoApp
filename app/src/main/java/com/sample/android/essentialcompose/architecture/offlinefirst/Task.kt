package com.sample.android.essentialcompose.architecture.offlinefirst

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
)
