package com.app.server.challenge.ui.controller

import com.app.server.challenge.ui.dto.response.GetAllChallengesResponseDto
import com.app.server.challenge.ui.dto.response.GetChallengeResponseDto
import com.app.server.challenge.ui.usecase.GetChallengeUseCase
import com.app.server.challenge.ui.usecase.GetListChallengesUseCase
import com.app.server.challenge.ui.usecase.dto.response.CategoryDto
import com.app.server.challenge.ui.usecase.dto.response.ChallengeDetailResponseDto
import com.app.server.common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Challenge API", description = "챌린지 관련 조회 API")
@RestController
@RequestMapping("/api/v1")
class ChallengeController(
    private val getChallengeUseCase: GetChallengeUseCase,
    private val getListChallengesUseCase: GetListChallengesUseCase,
) {

    @Operation(summary = "챌린지 목록 조회", description = "카테고리별 챌린지 목록을 조회합니다.")
    @GetMapping("/challenges")
    fun getChallenges(): ApiResponse<GetAllChallengesResponseDto> {
        val challenges: List<CategoryDto> = getListChallengesUseCase.execute()
        return ApiResponse.Companion.success(GetAllChallengesResponseDto.Companion.of(challenges))
    }

    @Operation(summary = "특정 챌린지 상세 조회", description = "ID로 챌린지 상세 정보를 조회합니다.")
    @Parameter(name = "id", description = "조회할 챌린지의 ID", required = true)
    @GetMapping("/challenges/{id}")
    fun getChallenge(@Valid @PathVariable id: Long): ApiResponse<GetChallengeResponseDto> {
        val challenge: ChallengeDetailResponseDto = getChallengeUseCase.execute(id)
        return ApiResponse.Companion.success(GetChallengeResponseDto.Companion.of(challenge))
    }
}