package com.app.server.challenge_certification.application.service

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.event.CertificationSucceededEvent
import com.app.server.challenge_certification.infra.CertificationInfraService
import com.app.server.challenge_certification.ui.dto.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.SendToCertificationServerRequestDto
import com.app.server.challenge_certification.ui.usecase.CertificationUseCase
import com.app.server.common.exception.BadRequestException
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
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
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import kotlin.test.Test


@SpringBootTest
@Transactional
@Rollback
@ExtendWith(SpringExtension::class)
class CertificateServiceTest : IntegrationTestContainer() {

    @Autowired
    private lateinit var certificationUseCase: CertificationUseCase

    @Autowired
    private lateinit var userChallengeService: UserChallengeService

    @Autowired
    private lateinit var challengeService: ChallengeService

    @MockitoBean
    private lateinit var certificationInfraService: CertificationInfraService

    @MockitoBean
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    var certificationRequestDto = CertificationRequestDto(
        userChallengeId = userChallengeId,
        imageUrl = imageUrl,
    )
    var sendToCertificationServerRequestDto = SendToCertificationServerRequestDto(
        imageUrl = imageUrl,
        challengeId = challengeId,
        challengeName = challengeTitle,
        challengeDescription = challengeDescription
    )
    var savedUserChallenge: UserChallenge? = null

    @BeforeEach
    fun setUp() {
        savedUserChallenge = makeUserChallengeAndHistory(participantsStartDate)

        certificationRequestDto = CertificationRequestDto(
            userChallengeId = savedUserChallenge!!.id!!,
            imageUrl = imageUrl,
        )
        val challenge = challengeService.findById(challengeId)
        sendToCertificationServerRequestDto = SendToCertificationServerRequestDto(
            imageUrl = imageUrl,
            challengeId = challenge.id!!,
            challengeName = challenge.title,
            challengeDescription = challenge.description
        )
    }

    @AfterEach
    fun tearDown() {
        userChallengeService.deleteAll()
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
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.CERTIFICATED_FAILED
        )
        // when
        val exception = assertThrows<BadRequestException> {
            certificationUseCase.certificateChallengeWithDate(
                userId, certificationRequestDto, participantsStartDate
            )
        }
        // then
        assertThat(exception).isInstanceOf(BadRequestException::class.java)
        assertThat(exception.message).isEqualTo(UserChallengeException.FAILED_CERTIFICATION.message)
    }

    @Test
    @DisplayName("챌린지 인증에 성공했다면, 인증 성공 이벤트를 게시한다.")
    fun publishChallengeImage() {
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        // when
        certificationUseCase.certificateChallengeWithDate(
            userId, certificationRequestDto, participantsStartDate
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