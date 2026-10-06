package com.example.multiverse.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.multiverse.data.remote.CharacterPagingSource
import com.example.multiverse.data.remote.RickAndMortyApi
import com.example.multiverse.domain.model.CharacterModel
import kotlinx.coroutines.flow.Flow

class CharacterRepository(
    private val api: RickAndMortyApi
) {

    fun getCharacters(
        name: String?,
        status: String?,
        gender: String?
    ): Flow<PagingData<CharacterModel>> {

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CharacterPagingSource(
                    api = api,
                    name = name,
                    status = status,
                    gender = gender
                )
            }
        ).flow
    }
}