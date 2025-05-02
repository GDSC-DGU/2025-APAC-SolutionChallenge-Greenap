package com.app.server.challenge_certification.application.service

import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.event.CertificationSucceededEvent
import com.app.server.challenge_certification.infra.CertificationInfraService
import com.app.server.challenge_certification.ui.dto.CertificationRequestDto
import com.app.server.challenge_certification.ui.usecase.CertificationUseCase
import com.app.server.common.exception.BadRequestException
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Transactional
class CertificationService(
    private val certificationInfraService: CertificationInfraService,
    private val userChallengeService: UserChallengeService,
    private val eventPublisher : ApplicationEventPublisher,
) : CertificationUseCase {

    override fun certificateChallengeWithDate(
        certificationRequestDto: CertificationRequestDto,
        certificationDate: LocalDate
    ): UserChallenge {

        val userChallenge = userChallengeService.findById(certificationRequestDto.userChallengeId)

        val certificateResult : EUserCertificatedResultCode = certificationInfraService.certificate(
            sendToCertificationServerRequestDto = certificationRequestDto.toSendToCertificationServerRequestDto(
                userChallenge.challenge
            )
        )

        when (certificateResult)
         {
            EUserCertificatedResultCode.CERTIFICATED_FAILED -> throw BadRequestException(UserChallengeException.FAILED_CERTIFICATION)
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED -> {}
            else -> throw BadRequestException(UserChallengeException.ERROR_IN_CERTIFICATED_SERVER)
        }

        eventPublisher.publishEvent(
            CertificationSucceededEvent(
                userChallengeId = userChallenge.id!!,
                imageUrl = certificationRequestDto.imageUrl,
                certificatedDate = certificationDate,
            )
        )

        return userChallenge
    }

}