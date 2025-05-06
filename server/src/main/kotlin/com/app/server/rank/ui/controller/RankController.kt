package com.app.server.rank.ui.controller

import com.app.server.common.annotation.UserId
import com.app.server.common.response.ApiResponse
import com.app.server.rank.ui.dto.SpecificChallengeTotalRankResponseDto
import com.app.server.rank.ui.dto.TotalRankResponseDto
import com.app.server.rank.ui.dto.UserRankInSpecificChallengeTotalRankResponseDto
import com.app.server.rank.ui.dto.UserRankInTotalRankResponseDto
import com.app.server.rank.ui.usecase.GetSpecificChallengeTotalRankUseCase
import com.app.server.rank.ui.usecase.GetTotalRankUseCase
import com.app.server.rank.ui.usecase.GetUserSpecificChallengeTotalRankUseCase
import com.app.server.rank.ui.usecase.GetUserTotalRankUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/ranks")
class RankController (
    private val getTotalRankUseCase: GetTotalRankUseCase,
    private val getUserTotalRankUseCase: GetUserTotalRankUseCase,
    private val getSpecificChallengeTotalRankUseCase: GetSpecificChallengeTotalRankUseCase,
    private val getUserSpecificChallengeTotalRankUseCase: GetUserSpecificChallengeTotalRankUseCase,
){

    @GetMapping
    fun getTotalRank(): ApiResponse<TotalRankResponseDto> {
        return ApiResponse.success(
            getTotalRankUseCase.execute()
        )
    }

    @GetMapping("/{challengeId}")
    fun getSpecificChallengeTotalRank(
        @PathVariable challengeId: Long
    ) : ApiResponse<SpecificChallengeTotalRankResponseDto> {
        return ApiResponse.success(
            getSpecificChallengeTotalRankUseCase.execute(challengeId)
        )
    }

    @GetMapping("/user")
    fun getUserRankInTotalRank(
        @UserId userId: Long
    ) : ApiResponse<UserRankInTotalRankResponseDto>{
        return ApiResponse.success(
            getUserTotalRankUseCase.executeOfUserIs(userId)
        )
    }

    @GetMapping("/user/{challengeId}")
    fun getUserRankInSpecificChallengeTotalRank(
        @UserId userId: Long,
        @PathVariable challengeId: Long
    ) : ApiResponse<UserRankInSpecificChallengeTotalRankResponseDto> {
        return ApiResponse.success(
            getUserSpecificChallengeTotalRankUseCase.execute(userId, challengeId)
        )
    }
}