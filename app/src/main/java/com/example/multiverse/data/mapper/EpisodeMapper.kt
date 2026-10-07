package com.example.multiverse.data.mapper

import com.example.multiverse.data.remote.dto.EpisodeDto
import com.example.multiverse.domain.model.EpisodeModel

fun EpisodeDto.toDomain(): EpisodeModel {
    return EpisodeModel(
        id = id,
        name = name,
        episodeCode = episode
    )
}