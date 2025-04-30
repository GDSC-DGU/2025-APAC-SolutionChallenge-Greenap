package com.app.server.user_challenge.ui

import com.app.server.common.annotation.UserId
import com.app.server.common.enums.CommonResultCode
import com.app.server.common.enums.ResultCode
import com.app.server.common.response.ApiResponse
import com.app.server.user_challenge.application.usecase.GetTotalUserChallengeUseCase
import com.app.server.user_challenge.application.usecase.ParticipantChallengeUseCase
import com.app.server.user_challenge.ui.dto.ChallengeParticipantRequestDto
import com.app.server.user_challenge.ui.dto.GetTotalUserChallengeResponseDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class UserChallengeController (
    private val participantChallengeUseCase: ParticipantChallengeUseCase,
    private val getTotalUserChallengeUseCase: GetTotalUserChallengeUseCase
) {

    @PostMapping("/challenges")
    fun participateChallenge(@UserId userId: Long, @RequestBody requestBody: ChallengeParticipantRequestDto) : ApiResponse<ResultCode> {
        val challengeParticipantDto = requestBody.toChallengeParticipantDto(userId)
        participantChallengeUseCase.execute(challengeParticipantDto)
        return ApiResponse.success(CommonResultCode.SUCCESS)
    }

    @GetMapping("/challenges/user")
    fun getTotalUserChallenge(@UserId userId: Long) : ApiResponse<GetTotalUserChallengeResponseDto> {
        val totalUserChallenge : GetTotalUserChallengeResponseDto = getTotalUserChallengeUseCase.execute(userId)
        return ApiResponse.success(totalUserChallenge)
    }
}