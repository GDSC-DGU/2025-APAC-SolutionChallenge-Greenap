package com.app.server.challenge_certification.application.usecase

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.challenge_certification.infra.CertificationInfraService
import com.app.server.challenge_certification.ui.dto.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.SendToCertificationServerRequestDto
import com.app.server.challenge_certification.ui.usecase.CertificationUseCase
import com.app.server.common.exception.BadRequestException
import com.app.server.user.application.service.UserEventListener
import com.app.server.user.application.service.UserService
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.service.UserChallengeEventListener
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import com.app.server.user_challenge.domain.event.SavedTodayUserChallengeCertificationEvent
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.reset
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDate
import kotlin.test.Test

@SpringBootTest
@Transactional
@Rollback
class CertificationUseCaseTest : IntegrationTestContainer() {

    @Autowired
    private lateinit var userEventListener: UserEventListener

    @Autowired
    private lateinit var userService: UserService

    @MockitoBean
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var certificationUseCase: CertificationUseCase

    @Autowired
    private lateinit var userChallengeService: UserChallengeService

    @Autowired
    private lateinit var challengeService: ChallengeService

    @Autowired
    private lateinit var userChallengeEventListener: UserChallengeEventListener

    @MockitoBean
    private lateinit var certificationInfraService: CertificationInfraService


    var certificationRequestDto = CertificationRequestDto(
        userChallengeId = userChallengeId,
        imageUrl = imageUrl,
    )
    var secondCertificationRequestDto = CertificationRequestDto(
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
    var secondSavedUserChallenge: UserChallenge? = null

    @BeforeEach
    fun setUp() {

        savedUserChallenge = makeUserChallengeAndHistory(challengeId, participantsStartDate)
        secondSavedUserChallenge = makeUserChallengeAndHistory(challengeId+1, participantsStartDate)

        fun makeCertificationRequestDto(userChallengeId: Long): CertificationRequestDto {
            return CertificationRequestDto(
                userChallengeId = userChallengeId,
                imageUrl = imageUrl,
            )
        }

        certificationRequestDto = makeCertificationRequestDto(savedUserChallenge!!.id!!)
        secondCertificationRequestDto = makeCertificationRequestDto(secondSavedUserChallenge!!.id!!)

        val challenge = challengeService.findById(challengeId)

        sendToCertificationServerRequestDto = SendToCertificationServerRequestDto(
            imageUrl = imageUrl,
            challengeId = challenge.id!!,
            challengeName = challenge.title,
            challengeDescription = challenge.description
        )

        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
    }

    @AfterEach
    fun tearDown() {
        userChallengeService.deleteAll()
        reset(applicationEventPublisher)
    }

    private fun makeUserChallengeAndHistory(challengeId: Long, startDate: LocalDate): UserChallenge {
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
    @DisplayName("챌린지 인증에 성공하면 해당 날짜의 인증 상태를 변경할 수 있다.")
    fun completeChallenge() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )

        // when
        certificationUseCase.certificateChallengeWithDate(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        verify(applicationEventPublisher).publishEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.handleCertificationSucceededEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        // then
        assertThat(savedUserChallenge!!.getUserChallengeHistories().first().status).isEqualTo(
            EUserChallengeCertificationStatus.SUCCESS
        )
    }

    @Test
    @DisplayName("챌린지 인증에 성공하면 전체 참여 일수가 증가한다.")
    fun completeChallengeWithIncreasingParticipationDays() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )

        val pastUserChallengeTotalParticipationDayCount = savedUserChallenge!!.totalParticipationDayCount

        // when
        certificationUseCase.certificateChallengeWithDate(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        verify(applicationEventPublisher).publishEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.handleCertificationSucceededEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )

        // then
        assertThat(savedUserChallenge!!.totalParticipationDayCount).isGreaterThan(
            pastUserChallengeTotalParticipationDayCount
        )
        assertThat(savedUserChallenge!!.totalParticipationDayCount - pastUserChallengeTotalParticipationDayCount).isOne
    }

    @Test
    @DisplayName("챌린지를 처음 인증하여 성공했다면, 연속 참여 일수가 증가한다.")
    fun completeChallengeFirstTryWithIncreasingConsecutiveParticipationDays() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        val pastUserChallengeNowConsecutiveParticipationDayCount =
            savedUserChallenge!!.nowConsecutiveParticipationDayCount
        // when
        certificationUseCase.certificateChallengeWithDate(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        verify(applicationEventPublisher).publishEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.handleCertificationSucceededEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        // then
        assertThat(savedUserChallenge!!.nowConsecutiveParticipationDayCount).isGreaterThan(
            pastUserChallengeNowConsecutiveParticipationDayCount
        )
        assertThat(
            savedUserChallenge!!.nowConsecutiveParticipationDayCount
                    - pastUserChallengeNowConsecutiveParticipationDayCount
        ).isOne
        assertThat(savedUserChallenge!!.maxConsecutiveParticipationDayCount).isOne
    }

    @Test
    @DisplayName("챌린지 인증에 성공했을 때 연속 참여 조건을 만족한다면 연속 참여 일수가 증가한다.")
    fun completeChallengeWithIncreasingConsecutiveParticipationDays() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )

        certificationUseCase.certificateChallengeWithDate(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        verify(applicationEventPublisher).publishEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.handleCertificationSucceededEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )

        val todayUserChallengeNowConsecutiveParticipationDayCount =
            savedUserChallenge!!.nowConsecutiveParticipationDayCount

        // when
        certificationUseCase.certificateChallengeWithDate(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate.plusDays(1)
        )
        verify(applicationEventPublisher).publishEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.handleCertificationSucceededEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate.plusDays(1)
            )
        )

        val expectedNowConsecutiveParticipationDayCount = savedUserChallenge!!.nowConsecutiveParticipationDayCount

        // then
        assertThat(expectedNowConsecutiveParticipationDayCount).isGreaterThan(
            todayUserChallengeNowConsecutiveParticipationDayCount
        )
        assertThat(expectedNowConsecutiveParticipationDayCount - todayUserChallengeNowConsecutiveParticipationDayCount).isOne
    }

    @Test
    @DisplayName("챌린지 인증에 성공했을 때 연속 참여 조건을 만족하지 않는다면 연속 참여 일수가 초기화된다.")
    fun completeChallengeWithResettingConsecutiveParticipationDays() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        certificationUseCase.certificateChallengeWithDate(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        verify(applicationEventPublisher).publishEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.handleCertificationSucceededEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )

        val pastUserChallengeMaxConsecutiveParticipationDayCount =
            savedUserChallenge!!.maxConsecutiveParticipationDayCount
        // when
        certificationUseCase.certificateChallengeWithDate(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate.plusDays(2)
        )
        verify(applicationEventPublisher).publishEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate.plusDays(2)
            )
        )
        userChallengeEventListener.handleCertificationSucceededEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate.plusDays(2)
            )
        )
        val todayUserChallengeNowConsecutiveParticipationDayCount =
            savedUserChallenge!!.nowConsecutiveParticipationDayCount
        // then
        assertThat(todayUserChallengeNowConsecutiveParticipationDayCount)
            .isLessThan(pastUserChallengeMaxConsecutiveParticipationDayCount)
            .isZero()
    }

    @Test
    @DisplayName("챌린지 인증에 실패하면 인증 실패임을 알린다.")
    fun failCertificated() {
        //given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.CERTIFICATED_FAILED
        )
        // when & then
        val exception = assertThrows<BadRequestException> {
            certificationUseCase.certificateChallengeWithDate(
                certificationRequestDto = certificationRequestDto,
                certificationDate = participantsStartDate
            )
        }
        assertThat(exception.message).isEqualTo(UserChallengeException.FAILED_CERTIFICATION.message)
    }

    @Test
    @DisplayName("오늘 인증한 챌린지에 대해 인증을 시도하면 예외가 발생한다.")
    fun alreadyCertificatedToday() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        certificationUseCase.certificateChallengeWithDate(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        verify(applicationEventPublisher).publishEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.handleCertificationSucceededEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )

        // when & then
        val exception = assertThrows<BadRequestException> {
            certificationUseCase.certificateChallengeWithDate(
                certificationRequestDto = certificationRequestDto,
                certificationDate = participantsStartDate
            )
            verify(applicationEventPublisher, times(2))
                .publishEvent(
                    makeCertificationSucceededEvent(
                        savedUserChallenge!!.id!!,
                        participantsStartDate
                    )
                )
            userChallengeEventListener.handleCertificationSucceededEvent(
                makeCertificationSucceededEvent(
                    savedUserChallenge!!.id!!,
                    participantsStartDate
                )
            )
        }
        assertThat(exception.message).isEqualTo(UserChallengeException.ALREADY_CERTIFICATED.message)
    }

    @Test
    @DisplayName("챌린지 인증에 성공했다면, 인증 사진을 저장한다.")
    fun saveChallengeImage() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        // when
        certificationUseCase.certificateChallengeWithDate(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        verify(applicationEventPublisher).publishEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.handleCertificationSucceededEvent(
            makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        // then
        assertThat(savedUserChallenge!!.getUserChallengeHistories().first().certificatedImageUrl)
            .isEqualTo(imageUrl)
    }

    @Test
    @DisplayName("챌린지 인증에 성공하면 사용자의 현재 최대 연속 참여 일수를 갱신한다.")
    fun updateMaxConsecutiveParticipationDays() {
        // given
        given(certificationInfraService.certificate(any()))
            .willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )

        // when
        certificationUseCase.certificateChallengeWithDate(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        certificationUseCase.certificateChallengeWithDate(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate.plusDays(1)
        )
        certificationUseCase.certificateChallengeWithDate(
            certificationRequestDto = secondCertificationRequestDto,
            certificationDate = participantsStartDate
        )

        userChallengeEventListener.handleCertificationSucceededEvent(
            certificationSucceededEvent = makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.handleCertificationSucceededEvent(
            certificationSucceededEvent = makeCertificationSucceededEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate.plusDays(1)
            )
        )
        userChallengeEventListener.handleCertificationSucceededEvent(
            certificationSucceededEvent = makeCertificationSucceededEvent(
                secondSavedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        verify(applicationEventPublisher, times(3)).publishEvent(
            org.mockito.ArgumentMatchers.any(SavedTodayUserChallengeCertificationEvent::class.java)
        )
        userEventListener.handleUserCreatedEvent(
            makeSavedUserChallengeCertificationEvent(
                userId = userId,
                maxConsecutiveParticipationDayCount = savedUserChallenge!!.maxConsecutiveParticipationDayCount
            )
        )
        userEventListener.handleUserCreatedEvent(
            makeSavedUserChallengeCertificationEvent(
                userId = userId,
                maxConsecutiveParticipationDayCount = savedUserChallenge!!.maxConsecutiveParticipationDayCount
            )
        )
        userEventListener.handleUserCreatedEvent(
            makeSavedUserChallengeCertificationEvent(
                userId = userId,
                maxConsecutiveParticipationDayCount = secondSavedUserChallenge!!.maxConsecutiveParticipationDayCount
            )
        )
        // then
        assertThat(savedUserChallenge!!.maxConsecutiveParticipationDayCount)
            .isGreaterThan(
                secondSavedUserChallenge!!.maxConsecutiveParticipationDayCount
            )
            .isEqualTo(
                userService.findById(userId).nowMaxConsecutiveParticipationDayCount
            )
            .isEqualTo(2)
    }

    private fun makeCertificationSucceededEvent(userChallengeId: Long, certificateDate: LocalDate)
            : CertificationSucceededEvent {
        return CertificationSucceededEvent(
            userChallengeId = userChallengeId,
            imageUrl = imageUrl,
            certificatedDate = certificateDate
        )
    }

    private fun makeSavedUserChallengeCertificationEvent(
        userId: Long,
        maxConsecutiveParticipationDayCount: Long
    ): SavedTodayUserChallengeCertificationEvent {
        return SavedTodayUserChallengeCertificationEvent(
            userId = userId,
            maxConsecutiveParticipationDayCount = maxConsecutiveParticipationDayCount
        )
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
