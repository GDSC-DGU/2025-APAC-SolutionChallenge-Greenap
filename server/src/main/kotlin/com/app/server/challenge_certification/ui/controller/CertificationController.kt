package com.app.server.challenge_certification.ui.controller

import com.app.server.challenge_certification.ui.dto.request.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.response.GetCertificatedImageUrlResponseDto
import com.app.server.challenge_certification.ui.usecase.CertificationUseCase
import com.app.server.common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@RestController
@Tag(name = "Certification API", description = "챌린지 인증 관련 API")
@RequestMapping("/api/v1/challenges/user/{userChallengeId}")
class CertificationController(
    private val certificationUseCase: CertificationUseCase
) {

    @Operation(
        summary = "챌린지 인증",
        description = "챌린지 인증을 위한 이미지 업로드 API입니다. 이미지를 jpg, jpeg, png 파일로 보내주세요."
    )
    @PostMapping("/certification")
    fun certificateDailyUserChallenge(
        @PathVariable("userChallengeId") userChallengeId: Long,
        @RequestPart("image") image: MultipartFile,
        @RequestParam(name = "certification_date", required = false) certificationDate: LocalDate?
    ): ApiResponse<GetCertificatedImageUrlResponseDto> {

        val certificationRequestDto = CertificationRequestDto(
            userChallengeId = userChallengeId,
            image = image
        )

        return ApiResponse.success(
            certificationUseCase.execute(
                certificationRequestDto = certificationRequestDto,
                certificationDate = certificationDate ?: LocalDate.now()
            )
        )
    }
}