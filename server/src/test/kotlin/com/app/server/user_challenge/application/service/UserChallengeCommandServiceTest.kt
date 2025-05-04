package com.app.server.user_challenge.application.service

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge.ui.usecase.dto.request.ChallengeParticipantDto
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.challenge_certification.infra.CertificationInfraService
import com.app.server.common.exception.BadRequestException
import com.app.server.common.exception.InternalServerErrorException
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.dto.ReceiveReportResponseDto
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import com.app.server.user_challenge.enums.EUserReportResultCode
import com.app.server.user_challenge.domain.event.ReportCreatedEvent
import com.app.server.user_challenge.infra.ReportInfraService
import com.app.server.user_challenge.ui.usecase.GetUserChallengeReportUseCase
import com.app.server.user_challenge.ui.usecase.ParticipantChallengeUseCase
import jakarta.transaction.Transactional
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.isA
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.reset
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
    private lateinit var participantChallengeUseCase: ParticipantChallengeUseCase

    @Autowired
    private lateinit var getUserChallengeReportUseCase: GetUserChallengeReportUseCase

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
    val endDate = participantsStartDate.plusDays(participationDays - 1L)

    @BeforeEach
    fun setUp() {
        savedUserChallenge = makeUserChallengeAndHistory(participantsStartDate)
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
                ), UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(1),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ), UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(2),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ), UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(3),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ), UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(4),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ), UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(5),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ), UserChallengeHistory(
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
    fun getReportMessage() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        // when
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate
            )
        )
        // then
        verify(applicationEventPublisher).publishEvent(isA(ReportCreatedEvent::class.java))
    }

    @Test
    @DisplayName("챌린지 리포트 메시지를 받아왔다면 저장하고, 챌린지의 상태를 Pending으로 변경한다.")
    fun saveReportMessage() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        // when
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate

            )
        )
        // then
        assertThat(savedUserChallenge!!.reportMessage).isEqualTo(expectedReportMessage)
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.PENDING)
    }

    @Test
    @DisplayName("마지막 날짜의 챌린지 인증에는 성공하였으나, 리포트 메시지를 생성에 실패했다면, 챌린지의 상태를 Dead으로 변경하여 추후 처리가 가능하게 한다.")
    fun getReportMessageWithFail() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_FAILED)
        // when
        val exception = assertThrows<InternalServerErrorException> {
            userChallengeEventListener.processWhenReceive(
                CertificationSucceededEvent(
                    savedUserChallenge!!.id!!, imageUrl, participantsStartDate.plusDays(participationDays - 1L)
                )
            )
        }
        // then
        assertThat(exception.message).isEqualTo(UserChallengeException.CANNOT_MAKE_REPORT.message)
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.DEAD)
    }

    @Test
    @DisplayName("마지막 날짜의 챌린지 인증에는 성공하였으나, 리포트 서버의 응답을 받지 못했다면, 챌린지의 상태를 Dead으로 변경하여 추후 처리가 가능하게 한다.")
    fun getReportMessageWithReportServerDeadAndChangeStatusToDead() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.ERROR_IN_RECEIVE_REPORT_SERVER)
        // when
        val exception = assertThrows<InternalServerErrorException> {
            userChallengeEventListener.processWhenReceive(
                CertificationSucceededEvent(
                    savedUserChallenge!!.id!!, imageUrl, endDate
                )
            )
        }
        // then
        assertThat(exception.message).isEqualTo(UserChallengeException.ERROR_IN_REPORT_SERVER.message)
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.DEAD)
    }

    @Test
    @DisplayName("챌린지 종료 일자와 오늘 일자가 같고 오늘 인증에 성공했다면, 챌린지 상태를 Pending으로 변경한다.")
    fun finishChallenge() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        // when
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate
            )
        )
        // then
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.PENDING)
    }

    @Test
    @Disabled
    @DisplayName("챌린지 종료 일자보다 오늘 일자가 더 크다면, 챌린지 상태를 Pending으로 변경한다.")
    fun finishChallengeWithOverdue() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        // when
        // then
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.PENDING)
    }

    @Test
    @DisplayName("Pending 상태의 챌린지는 리포트를 발급받은 상태이다.")
    fun issueReport() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        // when
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate
            )
        )
        // then
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.PENDING)
        assertThat(savedUserChallenge!!.reportMessage).isEqualTo(expectedReportMessage)
    }

    @Test
    @DisplayName("리포트를 아직 확인하지 않은 상태의 챌린지는 사용자가 챌린지 종료 다음날 안으로 리포트를 확인한다면 해당 챌린지는 이어하기가 가능한 상태로 변경된다.")
    fun checkReport() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate
            )
        )
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.PENDING)
        // when
        getUserChallengeReportUseCase.getReport(savedUserChallenge!!.id!!, endDate.plusDays(1))
        // then
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.WAITING)
    }

    @Test
    @DisplayName("리포트를 아직 확인하지 않은 상태의 챌린지는 사용자가 챌린지 종료 다음 날 이후로 리포트를 확인했다면 더 이상 이어할 수 없고 완전히 종료된다.")
    fun completeReport() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate
            )
        )
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.PENDING)
        // when
        getUserChallengeReportUseCase.getReport(savedUserChallenge!!.id!!, endDate.plusDays(2))
        // then
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.COMPLETED)
    }

    @Test
    @DisplayName("챌린지 이어하기를 성공했다면, 해당 챌린지의 상태는 다시 Running으로 변경되어 계속 이어 진행된다.")
    fun continueChallengeWithSuccess() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate
            )
        )
        getUserChallengeReportUseCase.getReport(savedUserChallenge!!.id!!, endDate.plusDays(1))
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.WAITING)
        // when
        participantChallengeUseCase.execute(
            ChallengeParticipantDto(
                userId = userId,
                challengeId = challengeId,
                participantsTotalDays = 7,
                status = EUserChallengeStatus.PENDING,
                participantsStartDate = endDate.plusDays(1)
            )
        )
        // then
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.RUNNING)
    }

    @Test
    @DisplayName("리포트를 아직 확인하지 않은 상태의 챌린지에 대해 사용자가 참여 요청을 보냈을 때, 리포트를 아직 확인하지 않았다면 리포트를 확인해야 한다.")
    fun continueChallengeWithPending() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate
            )
        )
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.PENDING)
        // when
        val exception = assertThrows<BadRequestException> {
            participantChallengeUseCase.execute(
                ChallengeParticipantDto(
                    userId = userId,
                    challengeId = challengeId,
                    participantsTotalDays = 7,
                    status = EUserChallengeStatus.RUNNING,
                    participantsStartDate = endDate.plusDays(1)
                )
            )
        }
        // then
        assertThat(exception.message).isEqualTo(UserChallengeException.CHALLENGE_WAITED_AND_STATUS_IS_PENDING.message)
    }

    @Test
    @DisplayName("완전 종료된 상태의 챌린지는 챌린지 이어하기 기능을 사용할 수 없다. 이 챌린지에 다시 참여 요청을 보내면 이어하기가 아니라 새로운 챌린지에 참여하게 된다.")
    fun continueChallenge() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate
            )
        )
        getUserChallengeReportUseCase.getReport(savedUserChallenge!!.id!!, endDate.plusDays(2))
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.COMPLETED)
        // when
        val newUserChallenge: UserChallenge = participantChallengeUseCase.execute(
            ChallengeParticipantDto(
                userId = userId,
                challengeId = challengeId,
                participantsTotalDays = 7,
                status = EUserChallengeStatus.RUNNING,
                participantsStartDate = endDate.plusDays(2)
            )
        )
        // then
        assertThat(savedUserChallenge!!.id!!).isNotEqualTo(newUserChallenge.id!!)
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.COMPLETED)
        assertThat(newUserChallenge.status).isEqualTo(EUserChallengeStatus.RUNNING)
        assertThat(savedUserChallenge!!.userId).isEqualTo(newUserChallenge.userId)
    }

    @Test
    @DisplayName("리포트를 확인한 챌린지는 챌린지 종료 다음 날 안으로 이어할 수 있다.")
    fun continueChallengeWithWait() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate
            )
        )
        getUserChallengeReportUseCase.getReport(savedUserChallenge!!.id!!, endDate.plusDays(1))
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.WAITING)
        // when
        participantChallengeUseCase.execute(
            ChallengeParticipantDto(
                userId = userId,
                challengeId = challengeId,
                participantsTotalDays = 7,
                status = EUserChallengeStatus.PENDING,
                participantsStartDate = endDate.plusDays(1)
            )
        )
        // then
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.RUNNING)
        assertThat(savedUserChallenge!!.participantDays).isEqualTo(participationDays + participationDays)
        assertThat(savedUserChallenge!!.getUserChallengeHistories().size).isEqualTo(
            participationDays + participationDays
        )
    }

    @Test
    @DisplayName("이어하기가 가능한 상태의 챌린지를 사용자가 이어한다면, 새로 선택한 참가일수만큼 총 참가 일수가 증가한다.")
    fun continueChallengeWithWaitAndIncreaseTotalParticipationDays() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate
            )
        )
        getUserChallengeReportUseCase.getReport(savedUserChallenge!!.id!!, endDate.plusDays(1))
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.WAITING)
        // when
        participantChallengeUseCase.execute(
            ChallengeParticipantDto(
                userId = userId,
                challengeId = challengeId,
                participantsTotalDays = 7,
                status = EUserChallengeStatus.PENDING,
                participantsStartDate = endDate.plusDays(1)
            )
        )
        // then
        assertThat(savedUserChallenge!!.participantDays).isEqualTo(participationDays + participationDays)
    }

    @Test
    @DisplayName("이어하기가 가능한 상태의 챌린지를 사용자가 이어하고 싶다면, 참여 중인 챌린지의 참여 히스토리가 늘어난다.")
    fun continueChallengeWithWaitAndIncreaseParticipationDays() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate
            )
        )
        getUserChallengeReportUseCase.getReport(savedUserChallenge!!.id!!, endDate.plusDays(1))
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.WAITING)
        // when
        participantChallengeUseCase.execute(
            ChallengeParticipantDto(
                userId = userId,
                challengeId = challengeId,
                participantsTotalDays = 7,
                status = EUserChallengeStatus.PENDING,
                participantsStartDate = endDate.plusDays(1)
            )
        )
        // then
        assertThat(savedUserChallenge!!.getUserChallengeHistories().size).isEqualTo(
            participationDays + participationDays
        )
    }

    @Test
    @DisplayName("이어하기가 가능한 상태의 챌린지를 사용자가 이어하여 참여 히스토리가 늘어났을 때, 새로 추가된 히스토리들의 상태는 모두 실패된 상태라고 간주한다.")
    fun continueChallengeWithWaitAndChangeStatusToFail() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate
            )
        )
        getUserChallengeReportUseCase.getReport(savedUserChallenge!!.id!!, endDate.plusDays(1))
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.WAITING)
        // when
        participantChallengeUseCase.execute(
            ChallengeParticipantDto(
                userId = userId,
                challengeId = challengeId,
                participantsTotalDays = 7,
                status = EUserChallengeStatus.PENDING,
                participantsStartDate = endDate.plusDays(1)
            )
        )
        // then
        assertThat(savedUserChallenge!!.getUserChallengeHistories().get(participationDays).status).isEqualTo(
            EUserChallengeCertificationStatus.FAILED
        )
        assertThat(savedUserChallenge!!.getUserChallengeHistories().last().status).isEqualTo(
            EUserChallengeCertificationStatus.FAILED
        )
    }

    @Test
    @DisplayName("이어하기가 가능한 상태의 챌린지를 사용자가 이어하여 참여 히스토리가 늘어났을 때, 기존 히스토리들은 그대로 유지된다.")
    fun continueChallengeWithWaitAndKeepStatus() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate
            )
        )
        getUserChallengeReportUseCase.getReport(savedUserChallenge!!.id!!, endDate.plusDays(1))
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.WAITING)
        // when
        participantChallengeUseCase.execute(
            ChallengeParticipantDto(
                userId = userId,
                challengeId = challengeId,
                participantsTotalDays = 7,
                status = EUserChallengeStatus.PENDING,
                participantsStartDate = endDate.plusDays(1)
            )
        )
        // then
        assertThat(savedUserChallenge!!.getUserChallengeHistories().get(participationDays-1).status).isEqualTo(
            EUserChallengeCertificationStatus.SUCCESS
        )
        assertThat(savedUserChallenge!!.getUserChallengeHistories().get(participationDays).status).isEqualTo(
            EUserChallengeCertificationStatus.FAILED
        )
    }

    @Test
    @DisplayName("이어하기가 가능한 상태의 챌린지를 사용자가 이어하고 싶다면, 기존 리포트 메시지는 삭제된다.")
    fun continueChallengeWithWaitAndDeleteReportMessage() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate
            )
        )
        getUserChallengeReportUseCase.getReport(savedUserChallenge!!.id!!, endDate.plusDays(1))
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.WAITING)
        assertThat(savedUserChallenge!!.reportMessage).isEqualTo(expectedReportMessage)
        // when
        participantChallengeUseCase.execute(
            ChallengeParticipantDto(
                userId = userId,
                challengeId = challengeId,
                participantsTotalDays = 7,
                status = EUserChallengeStatus.PENDING,
                participantsStartDate = endDate.plusDays(1)
            )
        )
        // then
        assertThat(savedUserChallenge!!.reportMessage).isNull()
    }

    @Test
    @DisplayName("이어하기가 가능한 상태의 챌린지를 사용자가 이어했을 때, 연속 참여 횟수도 이어서 증가할 수 있다.")
    fun continueChallengeWithWaitAndIncreaseConsecutiveParticipationDays() = runTest {
        // given
        settingReceiveReport(status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS)

        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate
            )
        )

        val expectedConsecutiveParticipationDays = savedUserChallenge!!.nowConsecutiveParticipationDayCount + 1

        getUserChallengeReportUseCase.getReport(savedUserChallenge!!.id!!, endDate.plusDays(1))
        assertThat(savedUserChallenge!!.status).isEqualTo(EUserChallengeStatus.WAITING)
        assertThat(savedUserChallenge!!.reportMessage).isEqualTo(expectedReportMessage)

        participantChallengeUseCase.execute(
            ChallengeParticipantDto(
                userId = userId,
                challengeId = challengeId,
                participantsTotalDays = 7,
                status = EUserChallengeStatus.PENDING,
                participantsStartDate = endDate.plusDays(1)
            )
        )
        // when
        userChallengeEventListener.processWhenReceive(
            CertificationSucceededEvent(
                savedUserChallenge!!.id!!, imageUrl, endDate.plusDays(1)
            )
        )
        // then
        assertThat(savedUserChallenge!!.nowConsecutiveParticipationDayCount).isEqualTo(expectedConsecutiveParticipationDays)
        assertThat(savedUserChallenge!!.maxConsecutiveParticipationDayCount).isEqualTo(expectedConsecutiveParticipationDays)
    }

    @Test
    @Disabled
    @DisplayName("이어하기가 가능한 상태의 챌린지(WAIT 상태인 챌린지)가 챌린지 종료일자로부터 이틀이 지났다면, 해당 챌린지는 더이상 이어하기를 진행할 수 없다.(COMPLETED 상태로 변경된다.)")
    fun completeChallengeWithWait() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("리포트 발급에 실패한 챌린지들(챌린지의 상태가 Dead인 챌린지들)을 모아 다시 리포트 메시지를 요청할 수 있다. 이 요청은 주기적으로 이루어지거나, Dead 상태의 챌린지들의 개수가 10개 이상 일 때 이루어진다.")
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


    private suspend fun settingReceiveReport(status: EUserReportResultCode) {
        given(certificationInfraService.certificate(any())).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        given(
            reportInfraService.receiveReportMessage(any())
        ).willReturn(
            ReceiveReportResponseDto(
                status = status, message = expectedReportMessage
            )
        )
        for (i in 0 until participationDays - 1)
            userChallengeEventListener.processWhenReceive(
                CertificationSucceededEvent(
                    savedUserChallenge!!.id!!, imageUrl, participantsStartDate.plusDays(i.toLong())
                )
            )
    }

    // TODO: 랭킹 작업 관련 테스트 작성
    // TODO: 알림 작업 관련 테스트 작성

}