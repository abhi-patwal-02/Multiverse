package com.example.multiverse.di

import com.example.multiverse.data.remote.RetrofitClient
import com.example.multiverse.data.repository.CharacterRepository

class AppContainer {

    private val api = RetrofitClient.api

    val characterRepository = CharacterRepository(api)
}