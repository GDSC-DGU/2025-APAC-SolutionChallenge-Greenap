package com.app.server.challenge.application.usecase.dto.response

import com.app.server.challenge.domain.enums.EChallengeStatus

data class ChallengeDetailResponseDto (
    val id: Long?,
    val title: String,
    val preDescription: String,
    val description: String,
    val certificationMethodDescription: String,
    val status: EChallengeStatus = EChallengeStatus.RUNNING,
    val mainImageUrl: String?,
    val certificationExampleImageUrl: String?,
    val categoryId: Long?,
    val categoryTitle: String,
    val percentOfCompletedUser: Double = 0.0,
) {
    companion object {
        fun of(
            challengeDetailDto: ChallengeDetailDto,
            percentOfCompletedUser: Double
        ): ChallengeDetailResponseDto {
            return ChallengeDetailResponseDto(
                id = challengeDetailDto.id,
                title = challengeDetailDto.title,
                preDescription = challengeDetailDto.preDescription,
                description = challengeDetailDto.description,
                certificationMethodDescription = challengeDetailDto.certificationMethodDescription,
                status = challengeDetailDto.status,
                mainImageUrl = challengeDetailDto.mainImageUrl,
                certificationExampleImageUrl = challengeDetailDto.certificationExampleImageUrl,
                categoryId = challengeDetailDto.categoryId,
                categoryTitle = challengeDetailDto.categoryTitle,
                percentOfCompletedUser = percentOfCompletedUser
            )
        }
    }
}