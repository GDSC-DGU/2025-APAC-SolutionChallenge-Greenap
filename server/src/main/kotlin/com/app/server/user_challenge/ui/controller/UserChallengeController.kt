package com.app.server.user_challenge.ui.controller

import com.app.server.challenge_certification.ui.dto.request.UserChallengeIceRequestDto
import com.app.server.common.annotation.UserId
import com.app.server.common.enums.CommonResultCode
import com.app.server.common.enums.ResultCode
import com.app.server.common.response.ApiResponse
import com.app.server.user_challenge.application.dto.response.UserChallengeResponseDto
import com.app.server.user_challenge.ui.dto.request.ChallengeParticipantRequestDto
import com.app.server.user_challenge.ui.dto.response.GetTotalUserChallengeResponseDto
import com.app.server.user_challenge.ui.usecase.GetTotalUserChallengeUseCase
import com.app.server.user_challenge.ui.usecase.ParticipantChallengeUseCase
import com.app.server.user_challenge.ui.usecase.UsingIceUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@Tag(name = "User Challenge API", description = "사용자 챌린지 관련 API")
@RequestMapping("/api/v1")
class UserChallengeController(
    private val participantChallengeUseCase: ParticipantChallengeUseCase,
    private val getTotalUserChallengeUseCase: GetTotalUserChallengeUseCase,
    private val usingIceUseCase: UsingIceUseCase
) {

    @Operation(
        summary = "챌린지 참여",
        description = "챌린지에 참여합니다. 챌린지 ID와 함께 요청하세요."
    )
    @PostMapping("/challenges")
    fun participateChallenge(
        @UserId userId: Long,
        @RequestBody requestBody: ChallengeParticipantRequestDto
    ): ApiResponse<UserChallengeResponseDto> {
        val challengeParticipantDto = requestBody.toChallengeParticipantDto(userId)
        return ApiResponse.success(participantChallengeUseCase.execute(challengeParticipantDto))
    }

    @Operation(
        summary = "챌린지 참여 내역 조회",
        description = "사용자가 참여한 챌린지 내역을 조회합니다."
    )
    @GetMapping("/challenges/user")
    fun getTotalUserChallenge(
        @UserId userId: Long,
        @RequestParam (name="search_date", required = false)searchDate: LocalDate?
    ): ApiResponse<GetTotalUserChallengeResponseDto> {
        val totalUserChallenge: GetTotalUserChallengeResponseDto =
            getTotalUserChallengeUseCase.execute(
                userId,
                searchDate ?: LocalDate.now()
            )
        return ApiResponse.success(totalUserChallenge)
    }

    @Operation(
        summary = "챌린지 얼리기",
        description = "챌린지를 얼릴 수 있습니다."
    )
    @PostMapping("/challenges/user/{userChallengeId}/ice")
    fun iceDailyUserChallenge(
        @PathVariable userChallengeId: Long,
        @RequestParam(name = "today_date", required = false) todayDate: LocalDate?
    ): ApiResponse<ResultCode> {
        val iceRequest = UserChallengeIceRequestDto(
            userChallengeId = userChallengeId
        )
        usingIceUseCase.processAfterCertificateIce(
            iceRequestDto = iceRequest,
            certificationDate = todayDate ?: LocalDate.now()
        )

        return ApiResponse.success(CommonResultCode.SUCCESS)
    }

}