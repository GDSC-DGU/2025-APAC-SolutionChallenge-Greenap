package com.app.server.user_challenge.ui

import com.app.server.common.annotation.UserId
import com.app.server.common.enums.CommonResultCode
import com.app.server.common.enums.ResultCode
import com.app.server.common.response.ApiResponse
import com.app.server.user_challenge.application.usecase.ParticipantChallengeUseCase
import com.app.server.user_challenge.ui.dto.ChallengeParticipantRequestDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class UserChallengeController (
    private val participantChallengeUseCase: ParticipantChallengeUseCase
) {

    @PostMapping("/challenge")
    fun participateChallenge(@UserId userId: Long, @RequestBody requestBody: ChallengeParticipantRequestDto) : ApiResponse<ResultCode> {
        val challengeParticipantDto = requestBody.toChallengeParticipantDto(userId)
        participantChallengeUseCase.execute(challengeParticipantDto)
        return ApiResponse.success(CommonResultCode.SUCCESS)
    }
}