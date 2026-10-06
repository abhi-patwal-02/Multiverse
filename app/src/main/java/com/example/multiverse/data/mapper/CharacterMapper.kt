package com.example.multiverse.data.mapper

import com.example.multiverse.data.remote.dto.CharacterDto
import com.example.multiverse.domain.model.CharacterModel

fun CharacterDto.toDomain(): CharacterModel {
    return CharacterModel(
        id = id,
        name = name,
        status = status,
        species = species,
        gender = gender,
        origin = origin.name,
        location = location.name,
        imageUrl = image,
        episodeUrls = episode
    )
}