package com.app.server.user_challenge.application.service

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.event.CertificationSucceededEvent
import com.app.server.challenge_certification.infra.CertificationInfraService
import com.app.server.challenge_certification.ui.dto.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.SendToCertificationServerRequestDto
import com.app.server.challenge_certification.ui.usecase.CertificationUseCase
import com.app.server.common.exception.InternalServerErrorException
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.dto.ReceiveReportResponseDto
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import com.app.server.user_challenge.enums.EUserReportResultCode
import com.app.server.user_challenge.event.ReportCreatedEvent
import com.app.server.user_challenge.infra.ReportInfraService
import com.app.server.user_challenge.ui.dto.SendToReportServerRequestDto
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.isA
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
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
class UserChallengeCommandServiceTest : IntegrationTestContainer() {

    @Autowired
    private lateinit var challengeService: ChallengeService

    @Autowired
    private lateinit var userChallengeService: UserChallengeService

    @MockitoBean
    private lateinit var certificationInfraService: CertificationInfraService

    @Autowired
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var userChallengeEventListener: UserChallengeEventListener

    @MockitoBean
    private lateinit var reportInfraService: ReportInfraService

    var savedUserChallenge: UserChallenge? = null
    val expectedReportMessage = "탄소 절감 AI 생성 메시지"

    @BeforeEach
    fun setUp() {
        savedUserChallenge = makeUserChallengeAndHistory(participantsStartDate)
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
    @DisplayName("챌린지 인증에 성공한 날짜가 챌린지 종료일자와 같다면, 리포트 메시지를 AI 서버로부터 받아온다.")
    fun getReportMessage() {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        // when
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            CertificationSucceededEvent(
                userId,
                savedUserChallenge!!.id!!,
                imageUrl,
                participantsStartDate.plusDays(participationDays - 1L)
            )
        )
        // then
        verify(applicationEventPublisher).publishEvent(isA(ReportCreatedEvent::class.java))
    }

    @Test
    @DisplayName("챌린지 리포트 메시지를 받아왔다면 저장하고, 챌린지의 상태를 Pending으로 변경한다.")
    fun saveReportMessage() {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        // when
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            CertificationSucceededEvent(
                userId,
                savedUserChallenge!!.id!!,
                imageUrl,
                participantsStartDate.plusDays(participationDays - 1L)
            )
        )
        // then
        assertThat(savedUserChallenge!!.reportMessage).isEqualTo(expectedReportMessage)
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.PENDING)
    }

    @Test
    @DisplayName("마지막 날짜의 챌린지 인증에는 성공하였으나, 리포트 메시지를 생성에 실패했다면, 챌린지의 상태를 Dead으로 변경한다.")
    fun getReportMessageWithFail() {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_FAILED)
        // when
        val exception = assertThrows<InternalServerErrorException> {
            userChallengeEventListener.handleCertificationSucceededEventForTest(
                CertificationSucceededEvent(
                    userId,
                    savedUserChallenge!!.id!!,
                    imageUrl,
                    participantsStartDate.plusDays(participationDays - 1L)
                )
            )
        }
        // then
        assertThat(exception.message).isEqualTo(UserChallengeException.CANNOT_MAKE_REPORT.message)
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.DEAD)
    }

    @Test
    @DisplayName("마지막 날짜의 챌린지 인증에는 성공하였으나, 리포트 서버의 응답을 받지 못했다면, 챌린지의 상태를 Dead으로 변경한다.")
    fun getReportMessageWithReportServerDeadAndChangeStatusToDead() {
        // given
        settingReceiveReport(status = EUserReportResultCode.ERROR_IN_RECEIVE_REPORT_SERVER)
        // when
        val exception = assertThrows<InternalServerErrorException> {
            userChallengeEventListener.handleCertificationSucceededEventForTest(
                CertificationSucceededEvent(
                    userId,
                    savedUserChallenge!!.id!!,
                    imageUrl,
                    participantsStartDate.plusDays(participationDays - 1L)
                )
            )
        }
        // then
        assertThat(exception.message).isEqualTo(UserChallengeException.ERROR_IN_REPORT_SERVER.message)
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.DEAD)
    }

    @Test
    @DisplayName("챌린지 종료 일자와 오늘 일자가 같고 오늘 인증에 성공했다면, 챌린지 상태를 Pending으로 변경한다.")
    fun finishChallenge() {
        // given
        val startDate = "2025-04-30"
        val endDate = "2025-05-06"
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        // when
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            CertificationSucceededEvent(
                userId,
                savedUserChallenge!!.id!!,
                imageUrl,
                LocalDate.parse(endDate)
            )
        )
        // then
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.PENDING)
    }

    @Test
    @Disabled
    @DisplayName("챌린지 종료 일자보다 오늘 일자가 더 크다면, 챌린지 상태를 Pending으로 변경한다.")
    fun finishChallengeWithOverdue() {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        // when
        // TODO : 배치 작업 테스트
        // then
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.PENDING)
    }

    @Test
    @DisplayName("Pending 상태의 챌린지는 리포트를 발급받은 상태이다.")
    fun issueReport() {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        // when
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            CertificationSucceededEvent(
                userId,
                savedUserChallenge!!.id!!,
                imageUrl,
                participantsStartDate.plusDays(participationDays - 1L)
            )
        )
        // then
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.PENDING)
        assertThat(savedUserChallenge!!.reportMessage).isEqualTo(expectedReportMessage)
    }

    @Test
    @Disabled
    @DisplayName("Pending 상태의 챌린지는 사용자가 챌린지 종료 다음날 안으로 리포트를 확인한다면 WAIT 상태로 변경된다.")
    fun checkReport() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("Pending 상태의 챌린지는 사용자가 챌린지 종료 다음날이후로 리포트를 확인했다면 COMPLETED 상태로 변경된다.")
    fun completeReport() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("COMPLETED 상태의 챌린지는 챌린지 이어하기 기능을 사용할 수 없다. 즉, 다시 Running 상태로 변경할 수 없다.")
    fun continueChallenge() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태의 챌린지는 챌린지 종료 다음 날 안으로 이어할 수 있다.")
    fun continueChallengeWithWait() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태의 챌린지를 사용자가 이어하고 싶다면, 총 참가 일수가 증가한다.")
    fun continueChallengeWithWaitAndIncreaseTotalParticipationDays() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태의 챌린지를 사용자가 이어하고 싶다면, 참여 중인 챌린지의 참여 히스토리가 늘어난다.")
    fun continueChallengeWithWaitAndIncreaseParticipationDays() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태의 챌린지를 사용자가 이어하여 참여 히스토리가 늘어났을 때, 새로 추가된 히스토리들의 상태는 모두 fail이다.")
    fun continueChallengeWithWaitAndChangeStatusToFail() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태의 챌린지를 사용자가 이어하여 참여 히스토리가 늘어났을 때, 기존 히스토리들은 그대로 유지된다.")
    fun continueChallengeWithWaitAndKeepStatus() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태의 챌린지를 사용자가 이어하고 싶다면, 기존 리포트 메시지는 삭제된다.")
    fun continueChallengeWithWaitAndDeleteReportMessage() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태의 챌린지를 사용자가 이어했을 때, 연속 참여 횟수도 이어서 증가할 수 있다.")
    fun continueChallengeWithWaitAndIncreaseConsecutiveParticipationDays() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지 이어하기를 성공했다면, 해당 챌린지의 상태는 다시 Running으로 변경된다.")
    fun continueChallengeWithSuccess() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태인 챌린지가 챌린지 종료일자로부터 이틀이 지났다면, 챌린지 상태를 COMPLETED로 변경한다.")
    fun completeChallengeWithWait() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지의 상태가 Dead인 챌린지들을 모아 다시 리포트 메시지를 요청할 수 있다. 이 요청은 주기적으로 이루어지거나, Dead 상태의 챌린지들의 개수가 10개 이상 일 때 이루어진다.")
    fun getReportMessageWithDead() {
        // given
        // when
        // then
    }

    @TestConfiguration
    class MockitoPublisherConfiguration {
        @Bean
        @Primary
        fun publisher(): ApplicationEventPublisher {
            return mock(ApplicationEventPublisher::class.java)
        }
    }


    private fun settingReceiveReport(status: EUserReportResultCode) {
        given(certificationInfraService.certificate(any()))
            .willReturn(
                EUserCertificatedResultCode.SUCCESS_CERTIFICATED
            )
        given(
            reportInfraService.receiveReportMessage(any())
        ).willReturn(
            ReceiveReportResponseDto(
                status = status,
                message = expectedReportMessage
            )
        )
        for (i in 0 until participationDays - 1)
        // TODO : 이벤트 리스너단계부터 검증이 필요. 지금은 리스너 메서드를 호출해서 테스트하지만, 제대로 이벤트를 수신받고 있는 지도 확인 필요
            userChallengeEventListener.handleCertificationSucceededEventForTest(
                CertificationSucceededEvent(
                    userId,
                    savedUserChallenge!!.id!!,
                    imageUrl,
                    participantsStartDate.plusDays(i.toLong())
                )
            )
    }
    // TODO: 랭킹 작업 관련 테스트 작성
    // TODO: 알림 작업 관련 테스트 작성

}