package com.sample.android.composebasics.architecture.offlinefirst

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
)
