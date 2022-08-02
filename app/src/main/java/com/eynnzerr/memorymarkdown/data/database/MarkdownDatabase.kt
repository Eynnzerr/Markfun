package com.eynnzerr.memorymarkdown.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

/**
 * markdown文件有2种：自己创建的和从外部打开的。区别在于前者没有uri而只存在于本地数据库中；后者具有uri，是真实存在的file
 * 前者可以通过高级设置自动导出为具有uri的文件，也可以另存为，从而转化为后者
 * 数据库只起到缓存的作用：可以避免多次操作uri
 * 具体逻辑：打开文件时，检阅Uri是否有效。无效，说明这是本地数据库中的文件且没有进行导出，直接操作数据库修改即可。
 * 有效，则无论是本地导出的还是外部导入的，都通过uri操作，并将最终内容缓存到最近打开/最近删除/收藏文件夹
 */
@Database(
    version = 2,
    entities = [MarkdownData::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
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