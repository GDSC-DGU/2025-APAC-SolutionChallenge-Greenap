package com.app.server.challenge_certification.ui.usecase

import com.app.server.challenge_certification.application.dto.command.CertificationCommand
import com.app.server.challenge_certification.application.service.CertificationService
import com.app.server.challenge_certification.ui.dto.request.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.response.GetCertificatedImageUrlResponseDto
import org.springframework.stereotype.Component
import java.time.LocalDate

interface CertificationUseCase {

    fun execute(
        certificationRequestDto: CertificationRequestDto,
        certificationDate: LocalDate
    ): GetCertificatedImageUrlResponseDto

}

@Component
class CertificationUseCaseImpl(
    private val certificationService: CertificationService
) : CertificationUseCase {

    override fun execute(
        certificationRequestDto: CertificationRequestDto,
        certificationDate: LocalDate
    ): GetCertificatedImageUrlResponseDto {

        val command = CertificationCommand(
            userChallengeId = certificationRequestDto.userChallengeId,
            image = certificationRequestDto.image,
            certificationDate = certificationDate
        )

        val result = certificationService.certificateImage(command)

        val isFinishedDay = LocalDate.now().isEqual(certificationDate)

        return GetCertificatedImageUrlResponseDto(
            imageUrl = result,
            isFinished = isFinishedDay
        )
    }
}
