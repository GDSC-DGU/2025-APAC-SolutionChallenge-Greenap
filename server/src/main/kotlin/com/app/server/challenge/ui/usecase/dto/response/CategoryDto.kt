package com.app.server.challenge.ui.usecase.dto.response

data class CategoryDto(
    val id: Long?,
    val title: String,
    val description: String,
    val imageUrl: String,
    val challenges: List<ChallengesSimpleDto>,
)

data class ChallengesSimpleDto(
    val id: Long?,
    val title: String,
    val preDescription: String,
    val mainImageUrl: String?,
)
