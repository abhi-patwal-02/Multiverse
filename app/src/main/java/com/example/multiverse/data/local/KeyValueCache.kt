package com.example.multiverse.data.local

import com.example.multiverse.data.local.dao.CacheDao
import com.example.multiverse.data.local.entity.CacheEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class KeyValueCache(
    private val dao: CacheDao,
    private val gson: Gson = Gson()
) {

    suspend fun <T> put(key: String, data: T) {
        val json = gson.toJson(data)
        dao.put(CacheEntry(key = key, value = json))
    }

    suspend fun <T> get(key: String, typeOfT: Type): T? {
        val entry = dao.get(key) ?: return null
        return runCatching {
            gson.fromJson<T>(entry.value, typeOfT)
        }.getOrNull()
    }

    suspend inline fun <reified T> get(key: String): T? {
        val type = object : TypeToken<T>() {}.type
        return get(key, type)
    }

    suspend fun delete(key: String) {
        dao.delete(key)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
