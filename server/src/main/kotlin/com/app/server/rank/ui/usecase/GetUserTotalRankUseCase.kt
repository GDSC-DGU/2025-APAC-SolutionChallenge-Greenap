package com.app.server.rank.ui.usecase

import com.app.server.rank.ui.dto.UserRankInTotalRankResponseDto

interface GetUserTotalRankUseCase {

    fun executeOfUserIs(userId: Long): UserRankInTotalRankResponseDto
}