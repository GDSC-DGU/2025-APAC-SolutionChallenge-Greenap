package com.app.server.challenge_certification.ui.controller

import com.app.server.challenge_certification.ui.dto.request.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.response.GetCertificatedImageUrlResponseDto
import com.app.server.challenge_certification.ui.usecase.CertificationUseCase
import com.app.server.common.response.ApiResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/challenges/user/{userChallengeId}")
class CertificationController(
    private val certificationUseCase: CertificationUseCase
) {

    @PostMapping("/certification")
    fun certificateDailyUserChallenge(
        @PathVariable("userChallengeId") userChallengeId: Long,
        @RequestPart("image") image: MultipartFile
    ): ApiResponse<GetCertificatedImageUrlResponseDto> {

        val certificationRequestDto = CertificationRequestDto(
            userChallengeId = userChallengeId,
            image = image
        )

        return ApiResponse.success(
            certificationUseCase.certificateChallengeWithDate(
                certificationRequestDto = certificationRequestDto,
                certificationDate = LocalDate.now()
            )
        )
    }
}