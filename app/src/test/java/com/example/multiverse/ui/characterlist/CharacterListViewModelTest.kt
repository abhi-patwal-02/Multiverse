package com.example.multiverse.ui.characterlist

import com.example.multiverse.data.repository.FakeCharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `rapid search typing is debounced`() = runTest(testDispatcher) {

        val repository = FakeCharacterRepository()
        val viewModel = CharacterListViewModel(repository)

        val job = backgroundScope.launch {
            viewModel.characters.collect {}
        }

        // Start collecting the Flow.
        runCurrent()

        viewModel.updateSearchQuery("r")
        runCurrent()
        advanceTimeBy(100.milliseconds)

        viewModel.updateSearchQuery("ri")
        runCurrent()
        advanceTimeBy(100.milliseconds)

        viewModel.updateSearchQuery("ric")
        runCurrent()
        advanceTimeBy(100.milliseconds)

        viewModel.updateSearchQuery("rick")
        runCurrent()

        // Only 499ms have passed since "rick".
        advanceTimeBy(499.milliseconds)
        runCurrent()

        assertEquals(
            0,
            repository.requestedFilters.size
        )

        // Complete the 400ms debounce.
        advanceTimeBy(1.milliseconds)
        runCurrent()

        assertEquals(
            1,
            repository.requestedFilters.size
        )

        assertEquals(
            Triple("rick", null, null),
            repository.requestedFilters.first()
        )

        job.cancel()
    }

    @Test
    fun `same search query twice does not create another request`() = runTest(testDispatcher) {

        val repository = FakeCharacterRepository()
        val viewModel = CharacterListViewModel(repository)

        val job = backgroundScope.launch {
            viewModel.characters.collect {}
        }

        runCurrent()

        // First "rick"
        viewModel.updateSearchQuery("rick")
        runCurrent()

        advanceTimeBy(500.milliseconds)
        runCurrent()

        assertEquals(
            1,
            repository.requestedFilters.size
        )

        // Same "rick" again
        viewModel.updateSearchQuery("rick")
        runCurrent()

        advanceTimeBy(500.milliseconds)
        runCurrent()

        // Still only one request.
        assertEquals(
            1,
            repository.requestedFilters.size
        )

        assertEquals(
            Triple("rick", null, null),
            repository.requestedFilters.first()
        )

        job.cancel()
    }

    @Test
    fun `retyping previous search query reuses cached paging flow`() = runTest(testDispatcher) {

        val repository = FakeCharacterRepository()
        val viewModel = CharacterListViewModel(repository)

        val job = backgroundScope.launch {
            viewModel.characters.collect {}
        }

        runCurrent()

        // First search: "rick"
        viewModel.updateSearchQuery("rick")
        runCurrent()

        advanceTimeBy(500.milliseconds)
        runCurrent()

        assertEquals(
            1,
            repository.requestedFilters.size
        )

        // Delete the search query.
        viewModel.updateSearchQuery("")
        runCurrent()

        advanceTimeBy(500.milliseconds)
        runCurrent()

        assertEquals(
            2,
            repository.requestedFilters.size
        )

        // Search for "rick" again.
        viewModel.updateSearchQuery("rick")
        runCurrent()

        advanceTimeBy(500.milliseconds)
        runCurrent()

        // Still only two repository calls.
        assertEquals(
            2,
            repository.requestedFilters.size
        )

        assertEquals(
            Triple("rick", null, null),
            repository.requestedFilters[0]
        )

        assertEquals(
            Triple(null, null, null),
            repository.requestedFilters[1]
        )

        job.cancel()
    }

    @Test
    fun `search and filters are passed together to repository`() = runTest(testDispatcher) {

        val repository = FakeCharacterRepository()
        val viewModel = CharacterListViewModel(repository)

        val job = backgroundScope.launch {
            viewModel.characters.collect {}
        }

        runCurrent()

        viewModel.updateSearchQuery("rick")
        viewModel.updateStatus("Alive")
        viewModel.updateGender("Male")

        runCurrent()

        advanceTimeBy(500.milliseconds)
        runCurrent()

        assertEquals(
            1,
            repository.requestedFilters.size
        )

        assertEquals(
            Triple("rick", "Alive", "Male"),
            repository.requestedFilters.first()
        )

        job.cancel()
    }


}