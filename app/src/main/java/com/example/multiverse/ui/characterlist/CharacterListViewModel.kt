package com.example.multiverse.ui.characterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.multiverse.data.repository.CharacterRepository
import com.example.multiverse.domain.model.CharacterModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest

class CharacterListViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status.asStateFlow()

    private val _gender = MutableStateFlow<String?>(null)
    val gender: StateFlow<String?> = _gender.asStateFlow()

    private val pagingCache =
        mutableMapOf<CharacterFilter, Flow<PagingData<CharacterModel>>>()

    private val filters = combine(
        _searchQuery,
        _status,
        _gender
    ) { query, status, gender ->
        CharacterFilter(
            query = query.trim().ifBlank { null },
            status = status,
            gender = gender
        )
    }
        .debounce(500)
        .distinctUntilChanged()

    val characters = filters
        .flatMapLatest { filter ->

            pagingCache.getOrPut(filter) {
                repository.getCharacters(
                    name = filter.query,
                    status = filter.status,
                    gender = filter.gender
                ).cachedIn(viewModelScope)
            }
        }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateStatus(status: String?) {
        _status.value = status
    }

    fun updateGender(gender: String?) {
        _gender.value = gender
    }


    private data class CharacterFilter(
        val query: String?,
        val status: String?,
        val gender: String?
    )
}