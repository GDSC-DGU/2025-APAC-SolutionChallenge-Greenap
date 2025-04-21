package com.app.server.challenge.application.usecase

import com.app.server.challenge.application.usecase.dto.response.ChallengeDetailResponseDto

interface GetChallengeUseCase {
    /**
     * 단일 챌린지의 상세 정보를 조회한다.
     */
    fun execute(challengeId: Long): ChallengeDetailResponseDto
}