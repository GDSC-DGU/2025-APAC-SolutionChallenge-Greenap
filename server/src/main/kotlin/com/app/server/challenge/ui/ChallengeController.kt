package com.app.server.challenge.ui

import com.app.server.challenge.application.usecase.GetChallengeUseCase
import com.app.server.challenge.application.usecase.GetListChallengesUseCase
import com.app.server.challenge.application.usecase.dto.response.CategoryDto
import com.app.server.challenge.application.usecase.dto.response.ChallengeDetailResponseDto
import com.app.server.challenge.ui.dto.response.GetAllChallengesResponseDto
import com.app.server.challenge.ui.dto.response.GetChallengeResponseDto
import com.app.server.common.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ChallengeController(
    private val getChallengeUseCase: GetChallengeUseCase,
    private val getListChallengesUseCase: GetListChallengesUseCase,
) {

    @GetMapping("/api/v1/challenges")
    fun getChallenges() : ApiResponse<GetAllChallengesResponseDto> {
        val challenges : List<CategoryDto> = getListChallengesUseCase.execute()
        return ApiResponse.success(GetAllChallengesResponseDto.of(challenges))
    }

    @GetMapping("/api/v1/challenges/{id}")
    fun getChallenge(@Valid @PathVariable id: Long) : ApiResponse<GetChallengeResponseDto> {
        val challenge : ChallengeDetailResponseDto = getChallengeUseCase.execute(id)
        return ApiResponse.success(GetChallengeResponseDto.of(challenge))
    }
}