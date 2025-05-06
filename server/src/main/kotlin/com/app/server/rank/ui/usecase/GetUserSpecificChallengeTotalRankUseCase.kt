package com.app.server.rank.ui.usecase

import com.app.server.rank.ui.dto.UserRankInSpecificChallengeTotalRankResponseDto

interface GetUserSpecificChallengeTotalRankUseCase {
    fun execute(challengeId: Long, userId: Long): UserRankInSpecificChallengeTotalRankResponseDto
}