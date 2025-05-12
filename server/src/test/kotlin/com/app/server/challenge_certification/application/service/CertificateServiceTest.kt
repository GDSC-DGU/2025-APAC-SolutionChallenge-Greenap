package com.app.server.challenge_certification.application.service

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge_certification.application.dto.command.CertificationCommand
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.challenge_certification.ui.dto.request.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.request.SendToCertificationServerRequestDto
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.port.outbound.CertificationPort
import com.app.server.common.exception.BadRequestException
import com.app.server.infra.cloud_storage.CloudStorageUtil
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.isA
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import kotlin.test.Test

@SpringBootTest
@Transactional
@Rollback
@ExtendWith(SpringExtension::class)
class CertificateServiceTest : IntegrationTestContainer() {

    @MockitoSpyBean
    private lateinit var certificationService: CertificationServiceImpl

    @MockitoBean
    private lateinit var certificationPort: CertificationPort

    @Autowired
    private lateinit var userChallengeService: UserChallengeService

    @Autowired
    private lateinit var challengeService: ChallengeService

    @MockitoBean
    private lateinit var cloudStorageUtil: CloudStorageUtil

    @MockitoBean
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    var certificationRequestDto = CertificationRequestDto(
        userChallengeId = userChallengeId,
        image = MockMultipartFile(
            "test.png", "test.png",
            "image/png", ByteArray(1)
        )
    )
    var sendToCertificationServerRequestDto = SendToCertificationServerRequestDto(
        imageUrl,
        challengeId,
        challengeTitle,
        challengeDescription
    )
    var savedUserChallenge: UserChallenge? = null

    @BeforeEach
    fun setUp() {
        savedUserChallenge = makeUserChallengeAndHistory(participantsStartDate)

        certificationRequestDto = CertificationRequestDto(
            userChallengeId = savedUserChallenge!!.id!!,
            image = MockMultipartFile(
                "test.png", "test.png",
                "image/png", ByteArray(1)
            )
        )
        doReturn(imageUrl).`when`(certificationService).encodeImageToBase64(
            MockMultipartFile("test.png", "test.png", "image/png", ByteArray(1))
        )

        val challenge = challengeService.findById(challengeId)
        sendToCertificationServerRequestDto = SendToCertificationServerRequestDto(
            imageUrl,
            challenge.id!!,
            challenge.title,
            challenge.description
        )
    }

    @AfterEach
    fun tearDown() {
        userChallengeService.deleteAll()
        reset(applicationEventPublisher)
    }

    private fun makeUserChallengeAndHistory(startDate: LocalDate): UserChallenge {
        val mainTestChallenge = challengeService.findById(challengeId)

        val userChallenge: UserChallenge = UserChallenge.createEntity(
            CreateUserChallengeDto(
                userId = userId,
                challenge = mainTestChallenge,
                participantsDate = 7,
                status = EUserChallengeStatus.RUNNING
            )
        )
        userChallenge.addUserChallengeHistories(
            listOf(
                UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate,
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ),
                UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(1),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ),
                UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(2),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ),
                UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(3),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ),
                UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(4),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ),
                UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(5),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ),
                UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(6),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                )
            )
        )
        return userChallengeService.save(userChallenge)
    }

    @Test
    @DisplayName("챌린지 인증에 실패했다면, 인증에 실패하였음을 클라이언트에게 전달한다.")
    fun failChallengeCertificate() {
        // given
        val errorMessage = "testMessage"
        given(cloudStorageUtil.uploadImageToCloudStorage(any(), any()))
            .willReturn(imageUrl)
        given(certificationPort.verifyCertificate(any())).willReturn(
            mapOf(EUserCertificatedResultCode.CERTIFICATED_FAILED to errorMessage)
        )
        // when
        val exception = assertThrows<BadRequestException> {
            certificationService.certificateImage(
                CertificationCommand(
                    certificationRequestDto.userChallengeId,
                    certificationRequestDto.image,
                    participantsStartDate
                )
            )
        }
        // then
        assertThat(exception).isInstanceOf(BadRequestException::class.java)
        assertThat(exception.message).isEqualTo(errorMessage)
    }

    @Test
    @DisplayName("챌린지 인증에 성공했다면, 인증 성공 이벤트를 게시한다.")
    fun publishChallengeImage() {
        given(cloudStorageUtil.uploadImageToCloudStorage(any(), any()))
            .willReturn(imageUrl)
        given(certificationPort.verifyCertificate(any()))
            .willReturn(
                mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
            )
        // when
        certificationService.certificateImage(
            CertificationCommand(
                certificationRequestDto.userChallengeId,
                certificationRequestDto.image,
                participantsStartDate
            )
        )

        // then
        verify(applicationEventPublisher).publishEvent(isA(CertificationSucceededEvent::class.java))
    }

    @TestConfiguration
    class MockitoPublisherConfiguration {
        @Bean
        @Primary
        fun publisher(): ApplicationEventPublisher {
            return mock(ApplicationEventPublisher::class.java)
        }
    }
}