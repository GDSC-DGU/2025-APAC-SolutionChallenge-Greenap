package com.app.server.challenge.ui.controller

import com.app.server.challenge.ui.dto.response.GetAllChallengesResponseDto
import com.app.server.challenge.ui.dto.response.GetChallengeResponseDto
import com.app.server.challenge.ui.usecase.GetChallengeUseCase
import com.app.server.challenge.ui.usecase.GetListChallengesUseCase
import com.app.server.challenge.ui.usecase.dto.response.CategoryDto
import com.app.server.challenge.ui.usecase.dto.response.ChallengeDetailResponseDto
import com.app.server.common.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class ChallengeController(
    private val getChallengeUseCase: GetChallengeUseCase,
    private val getListChallengesUseCase: GetListChallengesUseCase,
) {

    @GetMapping("/challenges")
    fun getChallenges() : ApiResponse<GetAllChallengesResponseDto> {
        val challenges : List<CategoryDto> = getListChallengesUseCase.execute()
        return ApiResponse.Companion.success(GetAllChallengesResponseDto.Companion.of(challenges))
    }

    @GetMapping("/challenges/{id}")
    fun getChallenge(@Valid @PathVariable id: Long) : ApiResponse<GetChallengeResponseDto> {
        val challenge : ChallengeDetailResponseDto = getChallengeUseCase.execute(id)
        return ApiResponse.Companion.success(GetChallengeResponseDto.Companion.of(challenge))
    }
}