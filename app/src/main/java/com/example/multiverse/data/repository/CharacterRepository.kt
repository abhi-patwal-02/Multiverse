package com.example.multiverse.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.multiverse.data.mapper.toDomain
import com.example.multiverse.data.remote.CharacterPagingSource
import com.example.multiverse.data.remote.RickAndMortyApi
import com.example.multiverse.domain.model.CharacterModel
import com.example.multiverse.domain.model.EpisodeModel
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    fun getCharacters(
        name: String?,
        status: String?,
        gender: String?
    ): Flow<PagingData<CharacterModel>>

    suspend fun getCharacter(
        id: Int
    ): CharacterModel

    suspend fun getEpisodes(
        episodeIds: List<Int>
    ): List<EpisodeModel>
}