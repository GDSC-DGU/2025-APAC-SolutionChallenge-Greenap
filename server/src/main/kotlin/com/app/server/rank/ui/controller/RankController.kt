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
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Rank API", description = "랭킹 관련 API")
@RequestMapping("/api/v1/ranks")
class RankController (
    private val getTotalRankUseCase: GetTotalRankUseCase,
    private val getUserTotalRankUseCase: GetUserTotalRankUseCase,
    private val getSpecificChallengeTotalRankUseCase: GetSpecificChallengeTotalRankUseCase,
    private val getUserSpecificChallengeTotalRankUseCase: GetUserSpecificChallengeTotalRankUseCase,
){

    @GetMapping
    @Operation(
        summary = "전체 랭킹 조회",
        description = "전체 랭킹을 조회합니다."
    )
    fun getTotalRank(): ApiResponse<TotalRankResponseDto> {
        return ApiResponse.success(
            getTotalRankUseCase.execute()
        )
    }

    @Operation(
        summary = "특정 챌린지 전체 랭킹 조회",
        description = "특정 챌린지의 전체 랭킹을 조회합니다."
    )
    @GetMapping("/{challengeId}")
    fun getSpecificChallengeTotalRank(
        @PathVariable challengeId: Long
    ) : ApiResponse<SpecificChallengeTotalRankResponseDto> {
        return ApiResponse.success(
            getSpecificChallengeTotalRankUseCase.execute(challengeId)
        )
    }

    @Operation(
        summary = "전체 사용자 중 특정 사용자의 랭킹 조회",
        description = "해당하는 사용자가 전체 사용자 중 연속 참여일수 기준으로 몇 위인지 조회합니다."
    )
    @GetMapping("/user")
    fun getUserRankInTotalRank(
        @UserId userId: Long
    ) : ApiResponse<UserRankInTotalRankResponseDto>{
        return ApiResponse.success(
            getUserTotalRankUseCase.executeOfUserIs(userId)
        )
    }

    @Operation(
        summary = "특정 챌린지 내에서의 특정 사용자 랭킹 조회",
        description = "해당하는 사용자가 특정 챌린지 내에서 총 참여일수 기준으로 몇 위인지 조회합니다."
    )
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