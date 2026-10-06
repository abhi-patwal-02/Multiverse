package com.example.multiverse.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.multiverse.data.mapper.toDomain
import com.example.multiverse.data.remote.dto.CharacterDto
import com.example.multiverse.domain.model.CharacterModel
import retrofit2.HttpException
import java.io.IOException

class CharacterPagingSource(
    private val api: RickAndMortyApi,
    private val name: String?,
    private val status: String?,
    private val gender: String?
) : PagingSource<Int, CharacterModel>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, CharacterModel> {

        val page = params.key ?: 1

        return try {

            val response = api.getCharacters(
                page = page,
                name = name,
                status = status,
                gender = gender
            )

            LoadResult.Page(
                data = response.results.map { it.toDomain() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.info.next == null) {
                    null
                } else {
                    page + 1
                }
            )

        } catch (e: IOException) {

            LoadResult.Error(e)

        } catch (e: HttpException) {

            LoadResult.Error(e)

        }
    }

    override fun getRefreshKey(
        state: PagingState<Int, CharacterModel>
    ): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.let { page ->
                page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
            }
        }
    }
}