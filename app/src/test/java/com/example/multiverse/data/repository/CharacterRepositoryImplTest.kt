package com.example.multiverse.data.repository

import com.example.multiverse.data.remote.RetrofitClient
import com.example.multiverse.data.remote.RickAndMortyApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CharacterRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: RickAndMortyApi
    private lateinit var repository: CharacterRepositoryImpl

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RickAndMortyApi::class.java)

        repository = CharacterRepositoryImpl(api)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getEpisodes batches multiple episode ids into one request`() = runTest {

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    [
                      {
                        "id": 1,
                        "name": "Pilot",
                        "air_date": "December 2, 2013",
                        "episode": "S01E01",
                        "characters": [],
                        "url": "",
                        "created": ""
                      },
                      {
                        "id": 2,
                        "name": "Lawnmower Dog",
                        "air_date": "December 9, 2013",
                        "episode": "S01E02",
                        "characters": [],
                        "url": "",
                        "created": ""
                      },
                      {
                        "id": 3,
                        "name": "Anatomy Park",
                        "air_date": "December 16, 2013",
                        "episode": "S01E03",
                        "characters": [],
                        "url": "",
                        "created": ""
                      }
                    ]
                    """.trimIndent()
                )
        )

        val episodes = repository.getEpisodes(
            listOf(1, 2, 3)
        )

        val request = mockWebServer.takeRequest()

        assertEquals(
            "/episode/1,2,3",
            request.path
        )

        assertEquals(3, episodes.size)
        assertEquals("Pilot", episodes[0].name)
        assertEquals("S01E01", episodes[0].episodeCode)
    }
}