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

    suspend fun getCharacter(
        id: Int
    ): CharacterModel {
        return api.getCharacter(id).toDomain()
    }

    suspend fun getEpisodes(
        episodeIds: List<Int>
    ): List<EpisodeModel> {

        if (episodeIds.isEmpty()) {
            return emptyList()
        }

        return if (episodeIds.size == 1) {

            listOf(
                api.getEpisode(episodeIds.first()).toDomain()
            )

        } else {

            val ids = episodeIds.joinToString(",")

            api.getEpisodes(ids)
                .map { it.toDomain() }
        }
    }
}