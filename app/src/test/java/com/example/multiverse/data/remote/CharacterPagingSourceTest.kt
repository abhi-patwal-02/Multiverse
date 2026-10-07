package com.example.multiverse.data.remote

import androidx.paging.PagingSource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CharacterPagingSourceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: RickAndMortyApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RickAndMortyApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `load page 1 returns characters and next page`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    {
                      "info": {
                        "count": 2,
                        "pages": 2,
                        "next": "https://rickandmortyapi.com/api/character?page=2",
                        "prev": null
                      },
                      "results": [
                        {
                          "id": 1,
                          "name": "Rick Sanchez",
                          "status": "Alive",
                          "species": "Human",
                          "type": "",
                          "gender": "Male",
                          "origin": {
                            "name": "Earth (C-137)",
                            "url": "https://rickandmortyapi.com/api/location/1"
                          },
                          "location": {
                            "name": "Citadel of Ricks",
                            "url": "https://rickandmortyapi.com/api/location/3"
                          },
                          "image": "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                          "episode": [
                            "https://rickandmortyapi.com/api/episode/1"
                          ],
                          "url": "https://rickandmortyapi.com/api/character/1",
                          "created": "2017-11-04T18:48:46.250Z"
                        }
                      ]
                    }
                    """.trimIndent()
                )
        )

        val pagingSource = CharacterPagingSource(
            api = api,
            name = null,
            status = null,
            gender = null
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)

        result as PagingSource.LoadResult.Page

        assertEquals(1, result.data.size)
        assertEquals("Rick Sanchez", result.data.first().name)
        assertEquals("Alive", result.data.first().status)

        assertEquals(null, result.prevKey)
        assertEquals(2, result.nextKey)
    }

    @Test
    fun `load last page returns null next key`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    {
                      "info": {
                        "count": 1,
                        "pages": 1,
                        "next": null,
                        "prev": null
                      },
                      "results": [
                        {
                          "id": 1,
                          "name": "Rick Sanchez",
                          "status": "Alive",
                          "species": "Human",
                          "type": "",
                          "gender": "Male",
                          "origin": {
                            "name": "Earth (C-137)",
                            "url": ""
                          },
                          "location": {
                            "name": "Citadel of Ricks",
                            "url": ""
                          },
                          "image": "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                          "episode": [],
                          "url": "",
                          "created": ""
                        }
                      ]
                    }
                    """.trimIndent()
                )
        )

        val pagingSource = CharacterPagingSource(
            api = api,
            name = null,
            status = null,
            gender = null
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)

        result as PagingSource.LoadResult.Page

        assertEquals(1, result.data.size)
        assertEquals(null, result.nextKey)
    }

    @Test
    fun `load returns error when server responds with error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
        )

        val pagingSource = CharacterPagingSource(
            api = api,
            name = null,
            status = null,
            gender = null
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error)
    }

    @Test
    fun `load sends search and filter query parameters`() = runTest {

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                {
                  "info": {
                    "count": 1,
                    "pages": 1,
                    "next": null,
                    "prev": null
                  },
                  "results": [
                    {
                      "id": 1,
                      "name": "Rick Sanchez",
                      "status": "Alive",
                      "species": "Human",
                      "type": "",
                      "gender": "Male",
                      "origin": {
                        "name": "Earth",
                        "url": ""
                      },
                      "location": {
                        "name": "Earth",
                        "url": ""
                      },
                      "image": "https://example.com/rick.png",
                      "episode": [],
                      "url": "",
                      "created": ""
                    }
                  ]
                }
                """.trimIndent()
                )
        )

        val pagingSource = CharacterPagingSource(
            api = api,
            name = "rick",
            status = "Alive",
            gender = "Male"
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)

        val request = mockWebServer.takeRequest()
        val url = request.requestUrl!!

        assertEquals("1", url.queryParameter("page"))
        assertEquals("rick", url.queryParameter("name"))
        assertEquals("Alive", url.queryParameter("status"))
        assertEquals("Male", url.queryParameter("gender"))
    }

    @Test
    fun `load requests the correct page number`() = runTest {

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                {
                  "info": {
                    "count": 2,
                    "pages": 2,
                    "next": "https://rickandmortyapi.com/api/character?page=3",
                    "prev": "https://rickandmortyapi.com/api/character?page=1"
                  },
                  "results": []
                }
                """.trimIndent()
                )
        )

        val pagingSource = CharacterPagingSource(
            api = api,
            name = null,
            status = null,
            gender = null
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 2,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)

        val request = mockWebServer.takeRequest()

        assertEquals(
            "2",
            request.requestUrl?.queryParameter("page")
        )
    }
}