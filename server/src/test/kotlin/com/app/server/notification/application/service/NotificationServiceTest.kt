package com.app.server.notification.application.service

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.ui.usecase.dto.request.ChallengeParticipantDto
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.notification.application.dto.command.GetEncourageMessageCommand
import com.app.server.notification.port.outbound.NotificationPort
import com.app.server.notification.ui.dto.request.GetEncourageMessageRequestDto
import com.app.server.user_challenge.application.dto.response.GetReportResponseDto
import com.app.server.user_challenge.application.service.UserChallengeEventListener
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.application.service.command.UserChallengeCommandService
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.enums.EUserReportResultCode
import com.app.server.user_challenge.port.outbound.ReportPort
import jakarta.transaction.Transactional
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.mockito.BDDMockito
import org.mockito.Mockito
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
class NotificationServiceTest : IntegrationTestContainer() {

    @MockitoBean
    private lateinit var reportPort: ReportPort

    @Autowired
    private lateinit var userChallengeService: UserChallengeService

    @Autowired
    private lateinit var notificationService: NotificationService

    @Autowired
    private lateinit var userChallengeCommandService: UserChallengeCommandService

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
        participantsStartDate = participantsStartDate.plusDays(1L)
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
        BDDMockito.given(notificationPort.getEncourageMessage(any()))
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
        val response = notificationService.getEncourageMessageForMain(
            GetEncourageMessageCommand(
                basicRequestDto.userId,
                basicRequestDto.todayDate
            )
        )
        // then
        Assertions.assertThat(response.message).isNotBlank
        Assertions.assertThat(response.userChallengeId).isNull()
        Assertions.assertThat(response.challengeTitle).isNotBlank
        Assertions.assertThat(response.progress).isZero
    }

    @Test
    @DisplayName("특정 챌린지에 참여 중이라면 해당 챌린지의 진행률에 맞는 격려 메시지를 받아온다.")
    fun getEncourageMessageWithChallengeTest() = runTest {
        // given
        val userChallenge = userChallengeCommandService.execute(firstChallengeParticipantDto)
        // when
        val response = notificationService.getEncourageMessageForMain(
            GetEncourageMessageCommand(
                basicRequestDto.userId,
                basicRequestDto.todayDate,
            )
        )
        // then
        Assertions.assertThat(response.message).isNotBlank
        Assertions.assertThat(response.userChallengeId).isEqualTo(userChallenge.userChallengeId)
        Assertions.assertThat(response.challengeTitle).isEqualTo(userChallenge.challengeTitle)
        Assertions.assertThat(response.progress).isEqualTo(userChallenge.progressFromElapsed)

        userChallengeService.deleteAll()

        // given
        val afterUserChallenge = userChallengeCommandService.execute(secondChallengeParticipantDto)

        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                userChallengeId = afterUserChallenge.userChallengeId,
                imageUrl = imageUrl,
                certificatedDate = participantsStartDate.plusDays(afterDaysCount),
            )
        )
        // when
        val responseAfterCertification = notificationService.getEncourageMessageForMain(
            GetEncourageMessageCommand(
                afterRequestDto.userId,
                afterRequestDto.todayDate.plusDays(afterDaysCount),
            )
        )
        // then
        Assertions.assertThat(responseAfterCertification.message).isNotBlank
        Assertions.assertThat(responseAfterCertification.userChallengeId).isEqualTo(afterUserChallenge.userChallengeId)
        Assertions.assertThat(responseAfterCertification.challengeTitle).isEqualTo(afterUserChallenge.challengeTitle)
        Assertions.assertThat(responseAfterCertification.progress).isEqualTo(14) // 1/7만큼 참여한 참여율
    }

    @Test
    @DisplayName("여러 개의 챌린지에 참여 중이라면 가장 진행률이 낮은 챌린지의 진행률에 맞는 격려 메시지를 받아온다.")
    fun getEncourageMessageWithMultipleChallengesTest() = runTest {
        // given
        val firstUserChallenge = userChallengeCommandService.execute(firstChallengeParticipantDto)
        val secondUserChallenge = userChallengeCommandService.execute(secondChallengeParticipantDto)
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                userChallengeId = secondUserChallenge.userChallengeId,
                imageUrl = imageUrl,
                certificatedDate = participantsStartDate.plusDays(afterDaysCount),
            )
        )
        // when
        val firstResponse = notificationService.getEncourageMessageForMain(
            GetEncourageMessageCommand(
                basicRequestDto.userId,
                basicRequestDto.todayDate.plusDays(afterDaysCount),
            )
        )
        // then
        Assertions.assertThat(firstResponse.message).isNotBlank
        Assertions.assertThat(firstResponse.userChallengeId)
            .isEqualTo(firstUserChallenge.userChallengeId)
            .isNotEqualTo(secondUserChallenge.userChallengeId)
        Assertions.assertThat(firstResponse.challengeTitle)
            .isEqualTo(firstUserChallenge.challengeTitle)
            .isNotEqualTo(secondUserChallenge.challengeTitle)
        Assertions.assertThat(firstResponse.progress)
            .isZero
    }

    @Test
    @DisplayName("이미 완료했던 챌린지들에 대해서는 독려 메시지를 받아와서는 안된다.")
    fun getEncourageMessageWithCompletedChallengesTest() = runTest {
        // given
        val userChallengeDto = userChallengeCommandService.execute(firstChallengeParticipantDto)
        BDDMockito.given(
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
                    userChallengeId = userChallengeDto.userChallengeId,
                    imageUrl = imageUrl,
                    certificatedDate = participantsStartDate.plusDays(i.toLong()),
                ),
            )
        }

        // when
        val response = notificationService.getEncourageMessageForMain(
            GetEncourageMessageCommand(
                userChallengeDto.userId,
                participantsStartDate.plusDays(participationDays.toLong())
            )
        )

        // then
        assertThat(response.userChallengeStatus).isNotEqualTo(
            EUserChallengeStatus.RUNNING
        )
        Assertions.assertThat(response.message).isNotBlank
        Assertions.assertThat(response.userChallengeId).isNull()
        Assertions.assertThat(response.challengeTitle).isNotEqualTo(userChallengeDto.challengeTitle)
        Assertions.assertThat(response.progress).isZero
    }

    @TestConfiguration
    class MockitoPublisherConfiguration {
        @Bean
        @Primary
        fun publisher(): ApplicationEventPublisher {
            return Mockito.mock(ApplicationEventPublisher::class.java)
        }
    }
}