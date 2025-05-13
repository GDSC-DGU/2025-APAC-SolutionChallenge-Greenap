package com.app.server.challenge_certification.ui.usecase

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.challenge_certification.ui.dto.request.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.request.SendToCertificationServerRequestDto
import com.app.server.challenge_certification.ui.dto.request.UserChallengeIceRequestDto
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.port.outbound.CertificationPort
import com.app.server.common.exception.BadRequestException
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.service.UserChallengeEventListener
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import com.app.server.user_challenge.ui.usecase.UsingIceUseCase
import jakarta.transaction.Transactional
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.kotlin.reset
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import kotlin.test.Test

@SpringBootTest
@Transactional
@Rollback
class UsingIceUseCaseTest : IntegrationTestContainer() {

    @MockitoBean
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var challengeService: ChallengeService

    @Autowired
    private lateinit var usingIceUseCase: UsingIceUseCase

    @Autowired
    private lateinit var userChallengeEventListener: UserChallengeEventListener

    @Autowired
    private lateinit var userChallengeService: UserChallengeService

    @MockitoBean
    private lateinit var certificationClientPort: CertificationPort


    val requiredSuccessfulDaysForIce = (participationDays).floorDiv(2).plus(1)
    val notSufficientSuccessfulDaysForIce = (participationDays).floorDiv(2).minus(1)
    var userChallengeIceRequestDto = UserChallengeIceRequestDto(
        userChallengeId = 1L
    )
    var certificationRequestDto = CertificationRequestDto(
        userChallengeId = userChallengeId,
        image = mock(MultipartFile::class.java)
    )

    var savedUserChallenge: UserChallenge? = null
    var sendToCertificationServerRequestDto = SendToCertificationServerRequestDto(
        imageUrl,
        challengeId,
        challengeTitle,
        challengeDescription
    )

    @BeforeEach
    fun setUpChallengeFixture() {
        savedUserChallenge = makeUserChallengeAndHistory(participantsStartDate)


        val challenge = challengeService.findById(challengeId)
        sendToCertificationServerRequestDto = SendToCertificationServerRequestDto(
            imageUrl,
            challenge.id!!,
            challenge.title,
            challenge.description
        )
        given(certificationClientPort.verifyCertificate(sendToCertificationServerRequestDto)).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )
        certificationRequestDto = CertificationRequestDto(
            userChallengeId = savedUserChallenge!!.id!!,
            image = mock(MultipartFile::class.java)
        )

        userChallengeIceRequestDto = UserChallengeIceRequestDto(
            userChallengeId = savedUserChallenge!!.id!!
        )
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

    @AfterEach
    fun tearDown() {
        userChallengeService.deleteAll()
        reset(applicationEventPublisher)
    }

    @Test
    @DisplayName("얼리기는 전체 참여 기간의 50% 이상 성공했을 때, 인증 대신 얼리기를 사용하여 인증을 건너뛸 수 있다.")
    fun skipChallengeWithLimit() = runTest {
        // given
        given(certificationClientPort.verifyCertificate(sendToCertificationServerRequestDto)).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )
        for (i in 0 until requiredSuccessfulDaysForIce)
            userChallengeEventListener.processWhenReceive(
                event = makeCertificationSucceededEvent(
                    participantsStartDate.plusDays(i.toLong())
                )
            )

        // when
        val userChallenge: UserChallenge = usingIceUseCase.processAfterCertificateIce(
            iceRequestDto = userChallengeIceRequestDto,
            certificationDate = participantsStartDate.plusDays(requiredSuccessfulDaysForIce.toLong())
        )
        // then
        assertThat(userChallenge.getUserChallengeHistories().get(requiredSuccessfulDaysForIce).status).isEqualTo(
            EUserChallengeCertificationStatus.ICE
        )
    }

    @Test
    @DisplayName("전체 참여 기간의 50% 이상을 성공하지 못했다면 얼리기를 사용할 수 없다.")
    fun skipChallengeWithLimitFailed() = runTest {
        // given
        given(certificationClientPort.verifyCertificate(sendToCertificationServerRequestDto)).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )
        for (i in 0 until notSufficientSuccessfulDaysForIce)
            userChallengeEventListener.processWhenReceive(
                event = makeCertificationSucceededEvent(
                    participantsStartDate.plusDays(i.toLong())
                )
            )        // when
        val exception = assertThrows<BadRequestException> {
            usingIceUseCase.processAfterCertificateIce(
                iceRequestDto = userChallengeIceRequestDto,
                certificationDate = participantsStartDate.plusDays(requiredSuccessfulDaysForIce.toLong())
            )
        }
        assertThat(exception.message).isEqualTo(UserChallengeException.CANNOT_USE_ICE.message)

    }


    // Legacy
    @Test
    @Disabled
    @DisplayName("얼리기를 사용하면 전체 참여일은 올라가지 않는다. (Legacy) ")
    fun skipChallengeWithoutIncreasingParticipationDays() = runTest {
        // given
        given(certificationClientPort.verifyCertificate(sendToCertificationServerRequestDto)).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )
        for (i in 0 until requiredSuccessfulDaysForIce)
            userChallengeEventListener.processWhenReceive(
                event = makeCertificationSucceededEvent(
                    participantsStartDate.plusDays(i.toLong())
                )
            )
        val existTotalParticipantDayCount = savedUserChallenge!!.totalParticipationDayCount
        // when
        val userChallenge: UserChallenge = usingIceUseCase.processAfterCertificateIce(
            iceRequestDto = userChallengeIceRequestDto,
            certificationDate = participantsStartDate.plusDays(requiredSuccessfulDaysForIce.toLong())
        )
        // then
        assertThat(userChallenge.getUserChallengeHistories().get(requiredSuccessfulDaysForIce).status).isEqualTo(
            EUserChallengeCertificationStatus.ICE
        )
        assertThat(userChallenge.totalParticipationDayCount).isEqualTo(existTotalParticipantDayCount)
    }

    @Test
    @DisplayName("얼리기를 사용했을 때 연속 참여 조건을 만족한다면 연속 참여 일수가 증가한다.")
    fun skipChallengeWithIncreasingConsecutiveParticipationDays() = runTest {
        // given
        given(certificationClientPort.verifyCertificate(sendToCertificationServerRequestDto)).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )
        for (i in 0 until requiredSuccessfulDaysForIce)
            userChallengeEventListener.processWhenReceive(
                event = makeCertificationSucceededEvent(
                    participantsStartDate.plusDays(i.toLong())
                )
            )

        val existNowConsecutiveParticipantDayCount = savedUserChallenge!!.nowConsecutiveParticipationDayCount
        // when
        val userChallenge: UserChallenge = usingIceUseCase.processAfterCertificateIce(
            iceRequestDto = userChallengeIceRequestDto,
            certificationDate = participantsStartDate.plusDays(requiredSuccessfulDaysForIce.toLong())
        )
        // then
        assertThat(userChallenge.getUserChallengeHistories().get(requiredSuccessfulDaysForIce).status).isEqualTo(
            EUserChallengeCertificationStatus.ICE
        )
        assertThat(userChallenge.nowConsecutiveParticipationDayCount).isGreaterThan(
            existNowConsecutiveParticipantDayCount
        )
        assertThat(userChallenge.nowConsecutiveParticipationDayCount - existNowConsecutiveParticipantDayCount).isOne
    }

    @Test
    @DisplayName("얼리기를 사용했을 때 연속 참여 조건을 만족하지 않는다면 연속 참여 일수는 증가하지 않는다.")
    fun skipChallengeWithoutIncreasingConsecutiveParticipationDays() = runTest {
        // given
        given(certificationClientPort.verifyCertificate(sendToCertificationServerRequestDto)).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )
        for (i in 0 until requiredSuccessfulDaysForIce)
            userChallengeEventListener.processWhenReceive(
                event = makeCertificationSucceededEvent(
                    participantsStartDate.plusDays(i.toLong())
                )
            )
        val existNowConsecutiveParticipantDayCount = savedUserChallenge!!.nowConsecutiveParticipationDayCount
        // when
        val userChallenge: UserChallenge = usingIceUseCase.processAfterCertificateIce(
            iceRequestDto = userChallengeIceRequestDto,
            certificationDate = participantsStartDate.plusDays(requiredSuccessfulDaysForIce.plus(1).toLong())
        )
        // then
        assertThat(userChallenge.getUserChallengeHistories().get(requiredSuccessfulDaysForIce - 1).status).isEqualTo(
            EUserChallengeCertificationStatus.SUCCESS
        )
        assertThat(userChallenge.getUserChallengeHistories().get(requiredSuccessfulDaysForIce).status).isEqualTo(
            EUserChallengeCertificationStatus.FAILED
        )
        assertThat(userChallenge.getUserChallengeHistories().get(requiredSuccessfulDaysForIce + 1).status).isEqualTo(
            EUserChallengeCertificationStatus.ICE
        )
        assertThat(userChallenge.nowConsecutiveParticipationDayCount).isLessThan(existNowConsecutiveParticipantDayCount)
        assertThat(userChallenge.nowConsecutiveParticipationDayCount).isOne
    }


    @Test
    @DisplayName("얼리기를 한번 사용하면 해당 챌린지에서는 다시 얼리기를 사용할 수 없다.")
    fun skipChallengeWithIceCount() = runTest {
        // given
        given(certificationClientPort.verifyCertificate(sendToCertificationServerRequestDto)).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )

        for (i in 0 until requiredSuccessfulDaysForIce)
            userChallengeEventListener.processWhenReceive(
                event = makeCertificationSucceededEvent(
                    participantsStartDate.plusDays(i.toLong())
                )
            )
        val userChallenge: UserChallenge = usingIceUseCase.processAfterCertificateIce(
            iceRequestDto = userChallengeIceRequestDto,
            certificationDate = participantsStartDate.plusDays(requiredSuccessfulDaysForIce.toLong())
        )

        // when
        assertThat(userChallenge.getUserChallengeHistories().get(requiredSuccessfulDaysForIce).status).isEqualTo(
            EUserChallengeCertificationStatus.ICE
        )

        val exception = assertThrows<BadRequestException> {
            usingIceUseCase.processAfterCertificateIce(
                iceRequestDto = userChallengeIceRequestDto,
                certificationDate = participantsStartDate.plusDays(
                    requiredSuccessfulDaysForIce.plus(1).toLong()
                )
            )
        }
        assertThat(exception.message).isEqualTo(UserChallengeException.CANNOT_USE_ICE.message)

    }

    private fun makeCertificationSucceededEvent(certificateDate: LocalDate): CertificationSucceededEvent {
        return CertificationSucceededEvent(
            userChallengeId = savedUserChallenge!!.id!!,
            imageUrl = imageUrl,
            certificatedDate = certificateDate
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