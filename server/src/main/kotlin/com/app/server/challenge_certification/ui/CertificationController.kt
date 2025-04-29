package com.app.server.challenge_certification.ui

import com.app.server.challenge_certification.ui.dto.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.UserChallengeIceRequestDto
import com.app.server.challenge_certification.usecase.CertificationUseCase
import com.app.server.challenge_certification.usecase.UsingIceUseCase
import com.app.server.common.enums.CommonResultCode
import com.app.server.common.enums.ResultCode
import com.app.server.common.response.ApiResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/challenge/user/{userChallengeId}")
class CertificationController(
    private val certificationUseCase: CertificationUseCase,
    private val usingIceUseCase: UsingIceUseCase
) {

    @PostMapping("/certification")
    fun certificateDailyUserChallenge(
        @RequestBody certificationRequestDto: CertificationRequestDto
    ): ApiResponse<ResultCode> {
        certificationUseCase.certificateChallengeWithDate(
            certificationRequestDto = certificationRequestDto,
            certificationDate = LocalDate.now()
        )

        return ApiResponse.success(CommonResultCode.SUCCESS)
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

        return ApiResponse.success(CommonResultCode.SUCCESS)
    }

}