package com.app.server.user_challenge.ui.controller

import com.app.server.challenge_certification.ui.dto.UserChallengeIceRequestDto
import com.app.server.common.annotation.UserId
import com.app.server.common.enums.CommonResultCode
import com.app.server.common.enums.ResultCode
import com.app.server.common.response.ApiResponse
import com.app.server.user_challenge.ui.dto.ChallengeParticipantRequestDto
import com.app.server.user_challenge.ui.dto.GetTotalUserChallengeResponseDto
import com.app.server.user_challenge.ui.usecase.GetTotalUserChallengeUseCase
import com.app.server.user_challenge.ui.usecase.ParticipantChallengeUseCase
import com.app.server.user_challenge.ui.usecase.UsingIceUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1")
class UserChallengeController(
    private val participantChallengeUseCase: ParticipantChallengeUseCase,
    private val getTotalUserChallengeUseCase: GetTotalUserChallengeUseCase,
    private val usingIceUseCase: UsingIceUseCase
) {

    @PostMapping("/challenges")
    fun participateChallenge(
        @UserId userId: Long,
        @RequestBody requestBody: ChallengeParticipantRequestDto
    ): ApiResponse<ResultCode> {
        val challengeParticipantDto = requestBody.toChallengeParticipantDto(userId)
        participantChallengeUseCase.execute(challengeParticipantDto)
        return ApiResponse.Companion.success(CommonResultCode.SUCCESS)
    }

    @GetMapping("/challenges/user")
    fun getTotalUserChallenge(@UserId userId: Long): ApiResponse<GetTotalUserChallengeResponseDto> {
        val totalUserChallenge: GetTotalUserChallengeResponseDto =
            getTotalUserChallengeUseCase.execute(
                userId,
                LocalDate.now()
            )
        return ApiResponse.Companion.success(totalUserChallenge)
    }

    @PostMapping("/ice")
    fun iceDailyUserChallenge(
        @PathVariable userChallengeId: Long
    ): ApiResponse<ResultCode> {
        val iceRequest = UserChallengeIceRequestDto(
            userChallengeId = userChallengeId
        )
        usingIceUseCase.processAfterCertificateIce(
            iceRequestDto = iceRequest,
            certificationDate = LocalDate.now()
        )

        return ApiResponse.Companion.success(CommonResultCode.SUCCESS)
    }

}