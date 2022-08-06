package com.eynnzerr.memorymarkdown.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

@Database(
    version = 3,
    entities = [MarkdownData::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
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