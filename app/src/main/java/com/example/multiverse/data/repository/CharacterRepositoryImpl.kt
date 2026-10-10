package com.example.multiverse.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.multiverse.data.local.KeyValueCache
import com.example.multiverse.data.mapper.toDomain
import com.example.multiverse.data.remote.CharacterPagingSource
import com.example.multiverse.data.remote.RickAndMortyApi
import com.example.multiverse.domain.model.CharacterModel
import com.example.multiverse.domain.model.EpisodeModel
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class CharacterRepositoryImpl(
    private val api: RickAndMortyApi,
    private val cache: KeyValueCache? = null
) : CharacterRepository {

    override fun getCharacters(
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
                    gender = gender,
                    cache = cache
                )
            }
        ).flow
    }

    override suspend fun getCharacter(
        id: Int
    ): CharacterModel {
        val key = "character:$id"
        return try {
            val character = api.getCharacter(id).toDomain()
            cache?.put(key, character)
            character
        } catch (e: IOException) {
            val cached = cache?.get<CharacterModel>(key)
            cached ?: throw e
        }
    }

    override suspend fun getEpisodes(
        episodeIds: List<Int>
    ): List<EpisodeModel> {

        if (episodeIds.isEmpty()) {
            return emptyList()
        }

        val key = "episodes:${episodeIds.sorted().joinToString(",")}"

        return try {
            val episodes = if (episodeIds.size == 1) {
                listOf(
                    api.getEpisode(episodeIds.first()).toDomain()
                )
            } else {
                val ids = episodeIds.joinToString(",")
                api.getEpisodes(ids)
                    .map { it.toDomain() }
            }
            cache?.put(key, episodes)
            episodes
        } catch (e: IOException) {
            val cached = cache?.get<List<EpisodeModel>>(key)
            cached ?: throw e
        }
    }
}