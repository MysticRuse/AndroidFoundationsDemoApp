package com.sample.android.composebasics.networkingdatapersistence.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Note::class, Folder::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
