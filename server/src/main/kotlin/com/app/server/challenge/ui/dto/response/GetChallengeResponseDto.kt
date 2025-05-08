package com.app.server.challenge.ui.dto.response

import com.app.server.challenge.ui.usecase.dto.response.ChallengeDetailResponseDto
import com.fasterxml.jackson.annotation.JsonProperty

data class GetChallengeResponseDto (
    val challenge: ChallengeResponseDto
){
    companion object {
        fun of(challenge: ChallengeDetailResponseDto): GetChallengeResponseDto {
            return GetChallengeResponseDto(
                challenge = ChallengeResponseDto(
                    id = challenge.id,
                    title = challenge.title,
                    preDescription = challenge.preDescription,
                    description = challenge.description,
                    mainImageUrl = challenge.mainImageUrl,
                    certificationExampleImageUrl = challenge.certificationExampleImageUrl,
                    certificationMethodDescription = challenge.certificationMethodDescription,
                    percentOfCompletedUser = challenge.percentOfCompletedUser
                )
            )
        }
    }
}

data class ChallengeResponseDto(
    val id: Long?,
    val title: String,
    @JsonProperty("pre_description")
    val preDescription: String,
    val description: String,
    @JsonProperty("main_image_url")
    val mainImageUrl: String?,
    @JsonProperty("certification_example_image_url")
    val certificationExampleImageUrl: String?,
    @JsonProperty("certification_method_description")
    val certificationMethodDescription: String,
    @JsonProperty("participation_dates")
    val participationDates: List<Int> = listOf(7, 10, 15, 30),
    @JsonProperty("percent_of_completed_user")
    val percentOfCompletedUser: Double
)
