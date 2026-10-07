package com.example.multiverse.ui.characterdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multiverse.data.repository.CharacterRepository
import com.example.multiverse.domain.model.CharacterModel
import com.example.multiverse.domain.model.EpisodeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CharacterDetailUiState(
    val isLoading: Boolean = true,
    val character: CharacterModel? = null,
    val episodes: List<EpisodeModel> = emptyList(),
    val error: String? = null
)

class CharacterDetailViewModel(
    private val repository: CharacterRepository,
    private val characterId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CharacterDetailUiState()
    )

    val uiState: StateFlow<CharacterDetailUiState> =
        _uiState.asStateFlow()

    init {
        loadCharacter()
    }

    fun loadCharacter() {

        viewModelScope.launch {

            _uiState.value = CharacterDetailUiState(
                isLoading = true
            )

            try {

                val character =
                    repository.getCharacter(characterId)

                val episodeIds = character.episodeUrls
                    .mapNotNull { url ->
                        url.substringAfterLast("/")
                            .toIntOrNull()
                    }

                val episodes =
                    repository.getEpisodes(episodeIds)

                _uiState.value = CharacterDetailUiState(
                    isLoading = false,
                    character = character,
                    episodes = episodes
                )

            } catch (e: Exception) {

                _uiState.value = CharacterDetailUiState(
                    isLoading = false,
                    error = e.message ?: "Something went wrong"
                )
            }
        }
    }
}