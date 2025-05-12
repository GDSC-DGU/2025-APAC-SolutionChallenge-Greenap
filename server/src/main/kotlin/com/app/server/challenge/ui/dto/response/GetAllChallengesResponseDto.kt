package com.app.server.challenge.ui.dto.response

import com.app.server.challenge.ui.usecase.dto.response.CategoryDto
import com.fasterxml.jackson.annotation.JsonProperty

data class GetAllChallengesResponseDto(
    val categories: List<CategoryResponseDto>,
){
    companion object {
        fun of(categories: List<CategoryDto>) : GetAllChallengesResponseDto{
            return GetAllChallengesResponseDto(
                categories = categories.map {
                    CategoryResponseDto(
                        id = it.id,
                        title = it.title,
                        description = it.description,
                        imageUrl = it.imageUrl,
                        challenges = it.challenges.map { challenge ->
                            ChallengesSimpleResponseDto(
                                id = challenge.id,
                                title = challenge.title,
                                preDescription = challenge.preDescription,
                                mainImageUrl = challenge.mainImageUrl
                            )
                        }
                    )
                }
            )
        }
    }
}

data class CategoryResponseDto(
    val id: Long?,
    val title: String,
    val description: String,
    @JsonProperty("image_url")
    val imageUrl: String,
    val challenges: List<ChallengesSimpleResponseDto>,
)

data class ChallengesSimpleResponseDto(
    val id: Long?,
    val title: String,
    val preDescription: String,
    @JsonProperty("main_image_url")
    val mainImageUrl: String?
)