package com.app.server.challenge_certification.ui.usecase

import com.app.server.challenge_certification.dto.ui.request.CertificationRequestDto
import com.app.server.challenge_certification.dto.ui.response.GetCertificatedImageUrlResponseDto
import java.time.LocalDate

interface CertificationUseCase {

    fun certificateChallengeWithDate(
        certificationRequestDto: CertificationRequestDto,
        certificationDate: LocalDate
    ): GetCertificatedImageUrlResponseDto

}
