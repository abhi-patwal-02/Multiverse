package com.example.multiverse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.multiverse.data.local.entity.CacheEntry

@Dao
interface CacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entry: CacheEntry)

    @Query("SELECT * FROM key_value_cache WHERE `key` = :key LIMIT 1")
    suspend fun get(key: String): CacheEntry?

    @Query("DELETE FROM key_value_cache WHERE `key` = :key")
    suspend fun delete(key: String)

    @Query("DELETE FROM key_value_cache")
    suspend fun clearAll()
}
