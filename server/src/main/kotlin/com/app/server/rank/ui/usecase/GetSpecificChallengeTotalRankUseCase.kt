package com.app.server.rank.ui.usecase

import com.app.server.rank.ui.dto.SpecificChallengeTotalRankResponseDto

interface GetSpecificChallengeTotalRankUseCase {
    fun execute(challengeId: Long): SpecificChallengeTotalRankResponseDto
}