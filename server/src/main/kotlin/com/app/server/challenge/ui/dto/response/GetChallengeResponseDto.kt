package com.app.server.challenge.ui.dto.response

import com.app.server.challenge.application.usecase.dto.response.ChallengeDetailResponseDto

data class GetChallengeResponseDto (
    val challenge: ChallengeResponseDto
){
    companion object {
        fun of(challenge: ChallengeDetailResponseDto): GetChallengeResponseDto {
            return GetChallengeResponseDto(
                challenge = ChallengeResponseDto(
                    id = challenge.id,
                    title = challenge.title,
                    pre_description = challenge.preDescription,
                    description = challenge.description,
                    image_url = challenge.mainImageUrl,
                    certification_method_description = challenge.certificationMethodDescription,
                    percent_of_completed_user = challenge.percentOfCompletedUser
                )
            )
        }
    }
}

data class ChallengeResponseDto(
    val id: Long?,
    val title: String,
    val pre_description: String,
    val description: String,
    val image_url: String?,
    val certification_method_description: String,
    val participation_dates: List<Int> = listOf(7, 10, 15, 30),
    val percent_of_completed_user: Double
)
