package com.example.multiverse.di

import android.content.Context
import com.example.multiverse.data.local.AppDatabase
import com.example.multiverse.data.local.KeyValueCache
import com.example.multiverse.data.remote.RetrofitClient
import com.example.multiverse.data.repository.CharacterRepositoryImpl

class AppContainer(context: Context) {

    private val api = RetrofitClient.api

    private val database = AppDatabase.create(context)

    val cache = KeyValueCache(database.cacheDao())

    val characterRepository = CharacterRepositoryImpl(api, cache)
}