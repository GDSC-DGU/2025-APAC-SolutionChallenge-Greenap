package com.app.server.challenge_certification.application.service

import com.app.server.challenge_certification.application.dto.CertificationFacadeToServiceDto
import com.app.server.challenge_certification.infra.CertificationInfraService
import com.app.server.challenge_certification.ui.dto.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.UserChallengeIceRequestDto
import com.app.server.challenge_certification.usecase.CertificationUseCase
import com.app.server.challenge_certification.usecase.UsingIceUseCase
import com.app.server.common.exception.BadRequestException
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Transactional
class CertificationFacadeService(
    private val certificationService: CertificationService,
    private val certificationInfraService: CertificationInfraService,
    private val userChallengeService: UserChallengeService
) : CertificationUseCase, UsingIceUseCase {

    override fun certificateChallengeWithDate(
        certificationRequestDto: CertificationRequestDto,
        certificationDate: LocalDate
    ): UserChallenge {

        val userChallenge = userChallengeService.findById(certificationRequestDto.userChallengeId)

        if (!certificationInfraService.certificate(
                sendToCertificationServerRequestDto = certificationRequestDto.toSendToCertificationServerRequestDto(
                    userChallenge.challenge
                )
            )
        ) {
            throw BadRequestException(UserChallengeException.FAILED_CERTIFICATION)
        }

        val certificationFacadeToServiceDto =
            CertificationFacadeToServiceDto(
                imageUrl = certificationRequestDto.imageUrl,
                certificationDate = certificationDate
            )
        return certificationService.processAfterCertificateSuccess(userChallenge, certificationFacadeToServiceDto)
    }

    override fun execute(
        userChallengeIceRequestDto: UserChallengeIceRequestDto,
        certificationDate: LocalDate
    ): UserChallenge {

       return certificationService.processAfterUserChallengeIce(userChallengeIceRequestDto, certificationDate)
    }

}