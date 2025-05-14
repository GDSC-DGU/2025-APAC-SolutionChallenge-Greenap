package com.app.server.challenge_certification.ui.usecase

import com.app.server.challenge_certification.application.dto.command.CertificationCommand
import com.app.server.challenge_certification.application.service.CertificationService
import com.app.server.challenge_certification.ui.dto.request.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.response.GetCertificatedImageUrlResponseDto
import com.app.server.user_challenge.application.service.UserChallengeService
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
    private val certificationService: CertificationService,
    private val userChallengeService: UserChallengeService
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

        val finishDate = userChallengeService.findById(certificationRequestDto.userChallengeId)
            .getUserChallengeHistories().last().date

        val isFinishedDay = certificationDate.isEqual(finishDate)

        return GetCertificatedImageUrlResponseDto(
            imageUrl = result,
            isFinished = isFinishedDay
        )
    }
}
