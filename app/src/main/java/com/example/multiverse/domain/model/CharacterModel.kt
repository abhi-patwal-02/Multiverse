package com.example.multiverse.domain.model

data class CharacterModel(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val origin: String,
    val location: String,
    val imageUrl: String,
    val episodeUrls: List<String>
)