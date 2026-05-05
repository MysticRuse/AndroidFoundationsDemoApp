package com.sample.android.composebasics.architecture.todo

data class TodoItem(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false
)
