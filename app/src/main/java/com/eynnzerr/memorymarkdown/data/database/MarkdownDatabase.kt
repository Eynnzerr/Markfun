package com.eynnzerr.memorymarkdown.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, exportSchema = false, entities = [MarkdownData::class])
abstract class MarkdownDatabase: RoomDatabase() {
    abstract fun getDao(): MarkdownDao

    companion object {
        private const val databaseName = "eynnzerr-markdown"
        private var INSTANCE: MarkdownDatabase? = null

        @Synchronized
        fun getInstance(context: Context): MarkdownDatabase {
            return INSTANCE?: Room.databaseBuilder(context.applicationContext, MarkdownDatabase::class.java, databaseName)
                .build()
        }
    }
}