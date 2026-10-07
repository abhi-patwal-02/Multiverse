package com.example.multiverse.data.repository

import androidx.paging.PagingData
import com.example.multiverse.domain.model.CharacterModel
import com.example.multiverse.domain.model.EpisodeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeCharacterRepository : CharacterRepository {

    val requestedFilters =
        mutableListOf<Triple<String?, String?, String?>>()

    private val pagingData =
        MutableSharedFlow<PagingData<CharacterModel>>(replay = 1)

    override fun getCharacters(
        name: String?,
        status: String?,
        gender: String?
    ): Flow<PagingData<CharacterModel>> {

        requestedFilters.add(
            Triple(name, status, gender)
        )

        return pagingData
    }

    override suspend fun getCharacter(
        id: Int
    ): CharacterModel {
        throw NotImplementedError()
    }

    override suspend fun getEpisodes(
        episodeIds: List<Int>
    ): List<EpisodeModel> {
        throw NotImplementedError()
    }
}