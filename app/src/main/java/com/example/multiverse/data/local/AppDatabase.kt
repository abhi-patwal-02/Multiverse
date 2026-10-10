package com.example.multiverse.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.multiverse.data.local.dao.CacheDao
import com.example.multiverse.data.local.entity.CacheEntry

@Database(entities = [CacheEntry::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cacheDao(): CacheDao

    companion object {
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "multiverse_cache.db"
            ).build()
        }
    }
}
