package com.app.server.challenge_certification.application.service

import com.app.server.challenge_certification.application.dto.command.CertificationCommand
import com.app.server.challenge_certification.port.outbound.CertificationPort
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.common.exception.BadRequestException
import com.app.server.infra.cloud_storage.CloudStorageUtil
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.exception.UserChallengeException
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
@Transactional
class CertificationServiceImpl(
    private val certificationPort: CertificationPort,
    private val userChallengeService: UserChallengeService,
    private val eventPublisher: ApplicationEventPublisher,
    private val cloudStorageUtil: CloudStorageUtil
) : CertificationService {

    override fun certificateImage(
        commnad: CertificationCommand
    ): String {

        val userChallengeId = commnad.userChallengeId
        val image = commnad.image
        val certificationDate = commnad.certificationDate

        val userChallenge = userChallengeService.findById(userChallengeId)

        val certificateResultAndAnswer = certificationPort.verifyCertificate(
            commnad.toSendToCertificationServerRequestDto(
                userChallenge.challenge,
                imageEncodingData = encodeImageToBase64(image)
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
                image = image,
                userChallengeId = userChallenge.id!!
            )

        eventPublisher.publishEvent(
            CertificationSucceededEvent(
                userChallengeId = userChallenge.id!!,
                imageUrl = imageUrl,
                certificatedDate = certificationDate,
            )
        )

        return imageUrl
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