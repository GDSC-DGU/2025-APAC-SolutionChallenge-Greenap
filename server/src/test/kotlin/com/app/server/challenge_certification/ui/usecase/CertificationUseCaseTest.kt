package com.app.server.challenge_certification.ui.usecase

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge_certification.application.service.CertificationServiceImpl
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.challenge_certification.ui.dto.request.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.request.SendToCertificationServerRequestDto
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.port.outbound.CertificationPort
import com.app.server.common.exception.BadRequestException
import com.app.server.infra.cloud_storage.CloudStorageUtil
import com.app.server.user.application.service.UserEventListener
import com.app.server.user.application.service.UserService
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.service.UserChallengeEventListener
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.event.SavedTodayUserChallengeCertificationEvent
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import jakarta.transaction.Transactional
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.kotlin.*
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
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import kotlin.test.Test

@SpringBootTest
@Transactional
@Rollback
class CertificationUseCaseTest : IntegrationTestContainer() {

    @MockitoBean
    private lateinit var cloudStorageUtil: CloudStorageUtil

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
    private lateinit var certificationPort: CertificationPort

    @MockitoSpyBean
    private lateinit var certificationService: CertificationServiceImpl


    var certificationRequestDto = CertificationRequestDto(
        userChallengeId = userChallengeId,
        image = mock(MultipartFile::class.java)
    )
    var secondCertificationRequestDto = CertificationRequestDto(
        userChallengeId = userChallengeId,
        image = mock(MultipartFile::class.java)
    )

    var sendToCertificationServerRequestDto = SendToCertificationServerRequestDto(
        imageUrl,
        challengeId,
        challengeTitle,
        challengeDescription
    )

    var savedUserChallenge: UserChallenge? = null
    var secondSavedUserChallenge: UserChallenge? = null

    @BeforeEach
    fun setUp() {

        savedUserChallenge = makeUserChallengeAndHistory(challengeId, participantsStartDate)
        secondSavedUserChallenge = makeUserChallengeAndHistory(challengeId + 1, participantsStartDate)

        fun makeCertificationRequestDto(userChallengeId: Long): CertificationRequestDto {
            return CertificationRequestDto(
                userChallengeId = userChallengeId,
                image = MockMultipartFile(
                    "test.png", "test.png",
                    "image/png", ByteArray(1)
                )
            )
        }

        certificationRequestDto = makeCertificationRequestDto(savedUserChallenge!!.id!!)
        secondCertificationRequestDto = makeCertificationRequestDto(secondSavedUserChallenge!!.id!!)

        val challenge = challengeService.findById(challengeId)

        sendToCertificationServerRequestDto = SendToCertificationServerRequestDto(
            imageUrl,
            challenge.id!!,
            challenge.title,
            challenge.description
        )

        given(
            certificationPort.verifyCertificate(
                org.mockito.kotlin.any()
            )
        ).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )
        given(
            cloudStorageUtil.uploadImageToCloudStorage(
                any(), any()
            )
        )
            .willReturn(imageUrl)
        doReturn(imageUrl).`when`(certificationService).encodeImageToBase64(
            MockMultipartFile(
                "test.png", "test.png",
                "image/png", ByteArray(1)
            )
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
    fun completeChallenge() = runTest {
        // given
        given(certificationPort.verifyCertificate(sendToCertificationServerRequestDto)).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )

        // when
        certificationUseCase.execute(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        verify(applicationEventPublisher).publishEvent(
            makeEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.processWhenReceive(
            makeEvent(
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
    fun completeChallengeWithIncreasingParticipationDays() = runTest {
        // given
        given(certificationPort.verifyCertificate(sendToCertificationServerRequestDto)).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )

        val pastUserChallengeTotalParticipationDayCount = savedUserChallenge!!.totalParticipationDayCount

        // when
        certificationUseCase.execute(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        verify(applicationEventPublisher).publishEvent(
            makeEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.processWhenReceive(
            makeEvent(
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
    fun completeChallengeFirstTryWithIncreasingConsecutiveParticipationDays() = runTest {
        // given
        given(certificationPort.verifyCertificate(sendToCertificationServerRequestDto)).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )
        val pastUserChallengeNowConsecutiveParticipationDayCount =
            savedUserChallenge!!.nowConsecutiveParticipationDayCount
        // when
        certificationUseCase.execute(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        verify(applicationEventPublisher).publishEvent(
            makeEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.processWhenReceive(
            makeEvent(
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
    fun completeChallengeWithIncreasingConsecutiveParticipationDays() = runTest {
        // given
        given(certificationPort.verifyCertificate(sendToCertificationServerRequestDto)).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )

        certificationUseCase.execute(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        verify(applicationEventPublisher).publishEvent(
            makeEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.processWhenReceive(
            makeEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )

        val todayUserChallengeNowConsecutiveParticipationDayCount =
            savedUserChallenge!!.nowConsecutiveParticipationDayCount

        // when
        certificationUseCase.execute(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate.plusDays(1)
        )
        verify(applicationEventPublisher).publishEvent(
            makeEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.processWhenReceive(
            makeEvent(
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
    fun completeChallengeWithResettingConsecutiveParticipationDays() = runTest {
        // given
        given(certificationPort.verifyCertificate(sendToCertificationServerRequestDto)).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )
        certificationUseCase.execute(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        verify(applicationEventPublisher).publishEvent(
            makeEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.processWhenReceive(
            makeEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )

        val pastUserChallengeMaxConsecutiveParticipationDayCount =
            savedUserChallenge!!.maxConsecutiveParticipationDayCount
        // when
        certificationUseCase.execute(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate.plusDays(2)
        )
        verify(applicationEventPublisher).publishEvent(
            makeEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate.plusDays(2)
            )
        )
        userChallengeEventListener.processWhenReceive(
            makeEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate.plusDays(2)
            )
        )
        val todayUserChallengeNowConsecutiveParticipationDayCount =
            savedUserChallenge!!.nowConsecutiveParticipationDayCount
        // then
        assertThat(todayUserChallengeNowConsecutiveParticipationDayCount)
            .isOne
    }

    @Test
    @DisplayName("챌린지 인증에 실패하면 인증 실패임을 알린다.")
    fun failCertificated() {
        //given
        val errorMessageFromExternalAPI = "testMessage"
        given(certificationPort.verifyCertificate(any())).willReturn(
            mapOf(EUserCertificatedResultCode.CERTIFICATED_FAILED to errorMessageFromExternalAPI)
        )
        // when & then
        val exception = assertThrows<BadRequestException> {
            certificationUseCase.execute(
                certificationRequestDto = certificationRequestDto,
                certificationDate = participantsStartDate
            )
        }
        assertThat(exception.message).isEqualTo(errorMessageFromExternalAPI)
    }

    @Test
    @DisplayName("오늘 인증한 챌린지에 대해 인증을 시도하면 예외가 발생한다.")
    fun alreadyCertificatedToday() = runTest {
        // given
        given(certificationPort.verifyCertificate(sendToCertificationServerRequestDto)).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )
        certificationUseCase.execute(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        verify(applicationEventPublisher).publishEvent(
            makeEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.processWhenReceive(
            makeEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )

        // when & then
        val exception = assertThrows<BadRequestException> {
            certificationUseCase.execute(
                certificationRequestDto = certificationRequestDto,
                certificationDate = participantsStartDate
            )
            verify(applicationEventPublisher, times(2))
                .publishEvent(
                    makeEvent(
                        savedUserChallenge!!.id!!,
                        participantsStartDate
                    )
                )
            userChallengeEventListener.processWhenReceive(
                makeEvent(
                    savedUserChallenge!!.id!!,
                    participantsStartDate
                )
            )
        }
        assertThat(exception.message).isEqualTo(UserChallengeException.ALREADY_CERTIFICATED.message)
    }

    @Test
    @DisplayName("챌린지 인증에 성공했다면, 인증 사진을 저장한다.")
    fun saveChallengeImage() = runTest {
        // given
        given(certificationPort.verifyCertificate(sendToCertificationServerRequestDto)).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )
        // when
        certificationUseCase.execute(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        verify(applicationEventPublisher).publishEvent(
            makeEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.processWhenReceive(
            makeEvent(
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
    fun updateMaxConsecutiveParticipationDays() = runTest {
        // given
        given(certificationPort.verifyCertificate(org.mockito.kotlin.any()))
            .willReturn(
                mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
            )

        // when
        certificationUseCase.execute(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )
        certificationUseCase.execute(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate.plusDays(1)
        )
        certificationUseCase.execute(
            certificationRequestDto = secondCertificationRequestDto,
            certificationDate = participantsStartDate
        )

        userChallengeEventListener.processWhenReceive(
            event = makeEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        userChallengeEventListener.processWhenReceive(
            event = makeEvent(
                savedUserChallenge!!.id!!,
                participantsStartDate.plusDays(1)
            )
        )
        userChallengeEventListener.processWhenReceive(
            event = makeEvent(
                secondSavedUserChallenge!!.id!!,
                participantsStartDate
            )
        )
        verify(applicationEventPublisher, times(3)).publishEvent(
            ArgumentMatchers.any(SavedTodayUserChallengeCertificationEvent::class.java)
        )
        userEventListener.handleUserCreatedEvent(
            makeSavedUserChallengeCertificationEvent(
                userChallengeId = savedUserChallenge!!.id!!,
                maxConsecutiveParticipationDayCount = savedUserChallenge!!.maxConsecutiveParticipationDayCount,
                totalParticipationDayCount = savedUserChallenge!!.totalParticipationDayCount
            )
        )
        userEventListener.handleUserCreatedEvent(
            makeSavedUserChallengeCertificationEvent(
                userChallengeId = savedUserChallenge!!.id!!,
                maxConsecutiveParticipationDayCount = savedUserChallenge!!.maxConsecutiveParticipationDayCount,
                totalParticipationDayCount = savedUserChallenge!!.totalParticipationDayCount
            )
        )
        userEventListener.handleUserCreatedEvent(
            makeSavedUserChallengeCertificationEvent(
                userChallengeId = savedUserChallenge!!.id!!,
                maxConsecutiveParticipationDayCount = secondSavedUserChallenge!!.maxConsecutiveParticipationDayCount,
                totalParticipationDayCount = secondSavedUserChallenge!!.totalParticipationDayCount
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

    private fun makeEvent(userChallengeId: Long, certificateDate: LocalDate)
            : CertificationSucceededEvent {
        return CertificationSucceededEvent(
            userChallengeId = userChallengeId,
            imageUrl = imageUrl,
            certificatedDate = certificateDate
        )
    }

    private fun makeSavedUserChallengeCertificationEvent(
        userChallengeId: Long,
        maxConsecutiveParticipationDayCount: Long,
        totalParticipationDayCount: Long
    ): SavedTodayUserChallengeCertificationEvent {
        return SavedTodayUserChallengeCertificationEvent(
            userChallengeId = userChallengeId,
            maxConsecutiveParticipationDayCount = maxConsecutiveParticipationDayCount,
            totalParticipationDayCount = totalParticipationDayCount
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
