package com.sample.android.composebasics.networkingdatapersistence.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :searchQuery || '%'")
    fun searchNotes(searchQuery: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE folderId = :folderId")
    fun getNotesByFolder(folderId: Long): Flow<List<Note>>

    @Query("""
        SELECT notes.*, folders.name as folderName 
        FROM notes 
        INNER JOIN folders ON notes.folderId = folders.id
    """)
    fun getNotesWithFolderNames(): Flow<List<NoteWithFolder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder)
}
