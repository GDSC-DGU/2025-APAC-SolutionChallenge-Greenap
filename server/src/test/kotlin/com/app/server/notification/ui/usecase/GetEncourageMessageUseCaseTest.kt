package com.app.server.notification.ui.usecase

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.ui.usecase.dto.request.ChallengeParticipantDto
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.infra.api.report.ReportAdaptor
import com.app.server.notification.dto.request.GetEncourageMessageRequestDto
import com.app.server.notification.port.outbound.NotificationPort
import com.app.server.user_challenge.application.dto.response.GetReportResponseDto
import com.app.server.user_challenge.application.service.UserChallengeEventListener
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.enums.EUserReportResultCode
import com.app.server.user_challenge.port.outbound.ReportPort
import com.app.server.user_challenge.ui.usecase.ParticipantChallengeUseCase
import jakarta.transaction.Transactional
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.Test

@SpringBootTest
@Transactional
@Rollback
class GetEncourageMessageUseCaseTest : IntegrationTestContainer() {

    @MockitoBean
    private lateinit var reportPort: ReportPort

    @Autowired
    private lateinit var userChallengeService: UserChallengeService

    @Autowired
    private lateinit var getEncourageMessageUseCase: GetEncourageMessageUseCase

    @Autowired
    private lateinit var participantChallengeUseCase: ParticipantChallengeUseCase

    @Autowired
    private lateinit var userChallengeEventListener: UserChallengeEventListener

    @MockitoBean
    private lateinit var notificationPort: NotificationPort

    var firstChallengeParticipantDto: ChallengeParticipantDto = ChallengeParticipantDto(
        userId = userId,
        challengeId = challengeId,
        participantsTotalDays = participationDays,
        status = EUserChallengeStatus.RUNNING,
        participantsStartDate = participantsStartDate
    )
    var secondChallengeParticipantDto: ChallengeParticipantDto = ChallengeParticipantDto(
        userId = userId,
        challengeId = challengeId + 1,
        participantsTotalDays = participationDays,
        status = EUserChallengeStatus.RUNNING,
        participantsStartDate = participantsStartDate
    )
    val afterDaysCount = 5L

    val basicRequestDto: GetEncourageMessageRequestDto = GetEncourageMessageRequestDto(
        userId = userId,
        todayDate = participantsStartDate,
    )
    val afterRequestDto: GetEncourageMessageRequestDto = GetEncourageMessageRequestDto(
        userId = userId,
        todayDate = participantsStartDate.plusDays(afterDaysCount),
    )

    @BeforeEach
    fun setUp() {
        given(notificationPort.getEncourageMessage(any()))
            .willReturn("${userName}님, ${challengeTitle} 챌린지에 참여하고 계신가요? 파이팅!!!")
    }

    @AfterEach
    fun tearDown() {
        userChallengeService.deleteAll()
    }

    @Test
    @DisplayName("아무 챌린지에도 참여하고 있지 않은 상황에서는 무작위의 챌린지를 선정해 진행률 0%로 격려 메시지를 받아온다.")
    fun getEncourageMessageTest() {
        // given
        // when
        val response = getEncourageMessageUseCase.execute(basicRequestDto)
        // then
        assertThat(response.message).isNotBlank
        assertThat(response.userChallengeId).isNull()
        assertThat(response.challengeTitle).isNotBlank
        assertThat(response.progress).isZero
    }

    @Test
    @DisplayName("특정 챌린지에 참여 중이라면 해당 챌린지의 진행률에 맞는 격려 메시지를 받아온다.")
    fun getEncourageMessageWithChallengeTest() = runTest {
        // given
        val userChallenge = participantChallengeUseCase.execute(firstChallengeParticipantDto)
        // when
        val response = getEncourageMessageUseCase.execute(basicRequestDto)
        // then
        assertThat(response.message).isNotBlank
        assertThat(response.userChallengeId).isEqualTo(userChallenge.id!!)
        assertThat(response.challengeTitle).isEqualTo(userChallenge.challenge.title)
        assertThat(response.progress).isEqualTo(userChallenge.calculateProgressFromElapsedDays(participantsStartDate))

        userChallengeService.deleteAll()

        // given
        val afterUserChallenge = participantChallengeUseCase.execute(secondChallengeParticipantDto)
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                userChallengeId = afterUserChallenge.id!!,
                imageUrl = imageUrl,
                certificatedDate = participantsStartDate.plusDays(afterDaysCount),
            )
        )
        // when
        val responseAfterCertification = getEncourageMessageUseCase.execute(afterRequestDto)
        // then
        assertThat(responseAfterCertification.message).isNotBlank
        assertThat(responseAfterCertification.userChallengeId).isEqualTo(afterUserChallenge.id!!)
        assertThat(responseAfterCertification.challengeTitle).isEqualTo(afterUserChallenge.challenge.title)
        assertThat(responseAfterCertification.progress).isEqualTo(
            afterUserChallenge.calculateProgressFromTotalParticipationDays(
                participantsStartDate.plusDays(afterDaysCount)
            )
        )
    }

    @Test
    @DisplayName("여러 개의 챌린지에 참여 중이라면 가장 진행률이 낮은 챌린지의 진행률에 맞는 격려 메시지를 받아온다.")
    fun getEncourageMessageWithMultipleChallengesTest() = runTest {
        // given
        val firstUserChallenge = participantChallengeUseCase.execute(firstChallengeParticipantDto)
        val secondUserChallenge = participantChallengeUseCase.execute(secondChallengeParticipantDto)
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                userChallengeId = secondUserChallenge.id!!,
                imageUrl = imageUrl,
                certificatedDate = participantsStartDate.plusDays(afterDaysCount),
            )
        )
        // when
        val response = getEncourageMessageUseCase.execute(afterRequestDto)
        // then
        assertThat(response.message).isNotBlank
        assertThat(firstUserChallenge.calculateProgressFromElapsedDays(participantsStartDate))
            .isLessThan(secondUserChallenge.calculateProgressFromElapsedDays(participantsStartDate.plusDays(afterDaysCount)))
        assertThat(response.userChallengeId)
            .isEqualTo(firstUserChallenge.id!!)
            .isNotEqualTo(secondUserChallenge.id!!)
        assertThat(response.challengeTitle)
            .isEqualTo(firstUserChallenge.challenge.title)
            .isNotEqualTo(secondUserChallenge.challenge.title)
        assertThat(response.progress)
            .isEqualTo(firstUserChallenge.calculateProgressFromElapsedDays(participantsStartDate))
            .isNotEqualTo(secondUserChallenge.calculateProgressFromElapsedDays(participantsStartDate.plusDays(afterDaysCount)))
    }

    @Test
    @DisplayName("이미 완료했던 챌린지들에 대해서는 독려 메시지를 받아와서는 안된다.")
    fun getEncourageMessageWithCompletedChallengesTest() = runTest {
        // given
        val userChallenge = participantChallengeUseCase.execute(firstChallengeParticipantDto)
        given(
            reportPort.getReportMessage(
                any()
            )
        )
            .willReturn(
                GetReportResponseDto(
                    status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS,
                    message = "인증 성공",
                )
            )
        for (i in 0 until participationDays) {
            userChallengeEventListener.processWhenReceive(
                CertificationSucceededEvent(
                    userChallengeId = userChallenge.id!!,
                    imageUrl = imageUrl,
                    certificatedDate = participantsStartDate.plusDays(i.toLong()),
                )
            )
        }
        assertThat(userChallenge.status).isEqualTo(EUserChallengeStatus.PENDING)

        // when
        val response = getEncourageMessageUseCase.execute(basicRequestDto)

        // then
        assertThat(response.message).isNotBlank
        assertThat(response.userChallengeId).isNull()
        assertThat(response.challengeTitle).isNotEqualTo(userChallenge.challenge.title)
        assertThat(response.progress).isZero.isNotEqualTo(
            userChallenge.calculateProgressFromTotalParticipationDays(participantsStartDate)
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