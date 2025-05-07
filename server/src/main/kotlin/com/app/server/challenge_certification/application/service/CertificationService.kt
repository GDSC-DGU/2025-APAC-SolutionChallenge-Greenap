package com.app.server.challenge_certification.application.service

import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.challenge_certification.infra.CertificationInfraService
import com.app.server.challenge_certification.ui.dto.request.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.response.GetCertificatedImageUrlResponseDto
import com.app.server.challenge_certification.ui.usecase.CertificationUseCase
import com.app.server.common.exception.BadRequestException
import com.app.server.core.infra.cloud_storage.CloudStorageUtil
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.util.Base64

@Service
@Transactional
class CertificationService(
    private val certificationInfraService: CertificationInfraService,
    private val userChallengeService: UserChallengeService,
    private val eventPublisher: ApplicationEventPublisher,
    private val cloudStorageUtil: CloudStorageUtil
) : CertificationUseCase {

    override fun certificateChallengeWithDate(
        certificationRequestDto: CertificationRequestDto,
        certificationDate: LocalDate
    ): GetCertificatedImageUrlResponseDto {

        val userChallenge = userChallengeService.findById(certificationRequestDto.userChallengeId)

        val certificateResultAndAnswer = certificationInfraService.certificate(
            sendToCertificationServerRequestDto = certificationRequestDto.toSendToCertificationServerRequestDto(
                userChallenge.challenge,
                imageEncodingData = encodeImageToBase64(certificationRequestDto.image)
            )
        )

        val certificateResult : EUserCertificatedResultCode = certificateResultAndAnswer.keys.first()
        val message = certificateResultAndAnswer.values.first()

        when (certificateResult) {
            EUserCertificatedResultCode.CERTIFICATED_FAILED -> throw BadRequestException(
                UserChallengeException.FAILED_CERTIFICATION,message
                )
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED -> {

            }

            else -> throw BadRequestException(
                UserChallengeException.ERROR_IN_CERTIFICATED_SERVER,
                message
            )
        }

        val imageUrl =
            cloudStorageUtil.uploadImageToCloudStorage(
                image = certificationRequestDto.image,
                userChallengeId = userChallenge.id!!
            )

        eventPublisher.publishEvent(
            CertificationSucceededEvent(
                userChallengeId = userChallenge.id!!,
                imageUrl = imageUrl,
                certificatedDate = certificationDate,
            )
        )

        return GetCertificatedImageUrlResponseDto(imageUrl)
    }

    fun encodeImageToBase64(image: MultipartFile?): String {
        return try {
            val bytes = image!!.bytes
            Base64.getEncoder().encodeToString(bytes)
        } catch (e: Exception) {
            throw RuntimeException("Failed to encode image to Base64", e)
        }
    }

}