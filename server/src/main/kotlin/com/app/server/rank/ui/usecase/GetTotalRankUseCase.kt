package com.app.server.rank.ui.usecase

import com.app.server.rank.ui.dto.TotalRankResponseDto

interface GetTotalRankUseCase {
    fun execute(): TotalRankResponseDto
}