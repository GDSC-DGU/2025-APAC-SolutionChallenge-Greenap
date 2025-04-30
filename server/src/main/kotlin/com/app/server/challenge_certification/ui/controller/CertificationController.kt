package com.app.server.challenge_certification.ui.controller

import com.app.server.challenge_certification.ui.dto.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.UserChallengeIceRequestDto
import com.app.server.challenge_certification.ui.usecase.CertificationUseCase
import com.app.server.common.annotation.UserId
import com.app.server.common.enums.CommonResultCode
import com.app.server.common.enums.ResultCode
import com.app.server.common.response.ApiResponse
import com.app.server.user_challenge.ui.usecase.UsingIceUseCase
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/challenges/user/{userChallengeId}")
class CertificationController(
    private val certificationUseCase: CertificationUseCase
) {

    @PostMapping("/certification")
    fun certificateDailyUserChallenge(
        @UserId userId: Long,
        @RequestBody certificationRequestDto: CertificationRequestDto
    ): ApiResponse<ResultCode> {
        certificationUseCase.certificateChallengeWithDate(
            userId = userId,
            certificationRequestDto = certificationRequestDto,
            certificationDate = LocalDate.now()
        )

        return ApiResponse.Companion.success(CommonResultCode.SUCCESS)
    }
}