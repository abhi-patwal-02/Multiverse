package com.example.multiverse.di

import com.example.multiverse.data.remote.RetrofitClient
import com.example.multiverse.data.repository.CharacterRepository
import com.example.multiverse.data.repository.CharacterRepositoryImpl

class AppContainer {

    private val api = RetrofitClient.api

    val characterRepository = CharacterRepositoryImpl(api)
}