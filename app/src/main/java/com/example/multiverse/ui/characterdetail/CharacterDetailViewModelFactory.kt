package com.example.multiverse.ui.characterdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.multiverse.data.repository.CharacterRepository

class CharacterDetailViewModelFactory(
    private val repository: CharacterRepository,
    private val characterId: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                CharacterDetailViewModel::class.java
            )
        ) {
            return CharacterDetailViewModel(
                repository = repository,
                characterId = characterId
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}