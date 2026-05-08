package com.sample.android.essentialcompose.networkingdatapersistence.room

data class NoteWithFolder(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Long,
    val folderId: Long,
    val folderName: String
)
