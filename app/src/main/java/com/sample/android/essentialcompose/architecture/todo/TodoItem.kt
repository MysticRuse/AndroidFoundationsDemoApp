package com.sample.android.essentialcompose.architecture.todo

data class TodoItem(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false
)
