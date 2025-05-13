package com.app.server.user_challenge.ui.usecase

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.ui.usecase.dto.request.ChallengeParticipantDto
import com.app.server.common.exception.BadRequestException
import com.app.server.common.exception.InternalServerErrorException
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.application.service.command.UserChallengeCommandService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import kotlin.test.Test

@SpringBootTest
@Transactional
@Rollback
class ParticipantChallengeUseCaseTest : IntegrationTestContainer() {
    @Autowired
    private lateinit var userChallengeCommandService: UserChallengeCommandService

    @Autowired
    private lateinit var usrChallengeService: UserChallengeService

    private lateinit var challengeParticipantDto: ChallengeParticipantDto

    @BeforeEach
    fun setUpChallengeFixture() {

        challengeParticipantDto = ChallengeParticipantDto(
            userId = userId,
            challengeId = challengeId,
            participantsTotalDays = participationDays,
            status = EUserChallengeStatus.RUNNING,
            participantsStartDate = participantsStartDate
        )
    }

    @AfterEach
    fun tearDown() {
        usrChallengeService.deleteAll()
    }

    @Test
    @DisplayName("특정 챌린지에 원하는 기간으로 참여할 수 있다.")
    fun participateChallenge() {

        // when
        val userChallenge = userChallengeCommandService.execute(challengeParticipantDto)

        // then
        assertThat(userChallenge.userId).isEqualTo(userId)
        assertThat(userChallenge.challengeTitle).isEqualTo(challengeTitle)
        assertThat(userChallenge.status).isEqualTo(EUserChallengeStatus.RUNNING)
        assertThat(userChallenge.participantsDate).isEqualTo(participationDays)
        assertThat(userChallenge.iceCount).isZero()
        assertThat(userChallenge.maxConsecutiveDays).isEqualTo(userChallenge.nowConsecutiveDays).isZero()
        assertThat(userChallenge.reportMessage).isNull()
        assertThat(userChallenge.challengeHistories)
            .hasSize(participationDays)
            .allMatch { it.isCertificated == EUserChallengeCertificationStatus.FAILED }
    }

    @Test
    @DisplayName("해당 챌린지를 눌렀을 때, 이 챌린지에 이미 참여하고 있다면, 새로 참여할 수 없다.")
    fun participateChallengeWithAlreadyParticipated() {
        // given
        userChallengeCommandService.execute(challengeParticipantDto)
        // when
        val exception = assertThrows<BadRequestException> {
            userChallengeCommandService.execute(challengeParticipantDto)
        }
        // then
        assertThat(exception).isInstanceOf(BadRequestException::class.java)
        assertThat(exception.message).isEqualTo(UserChallengeException.ALREADY_PARTICIPATED_AND_STATUS_IS_RUNNING.message)
    }

    @Test
    @DisplayName("해당 챌린지를 눌렀을 때, 챌린지를 마쳤지만 아직 챌린지 리포트를 확인하지 못한 경우에는 챌린지 리포트를 확인해야 다시 참여할 수 있다.")
    fun participateChallengeWithNotParticipated() {
        // given
        userChallengeCommandService.execute(challengeParticipantDto.copy(
            status = EUserChallengeStatus.PENDING
        ))
        // when
        val exception = assertThrows<BadRequestException> {
            userChallengeCommandService.execute(challengeParticipantDto)
        }
        // then
        assertThat(exception).isInstanceOf(BadRequestException::class.java)
        assertThat(exception.message).isEqualTo(UserChallengeException.CHALLENGE_WAITED_AND_STATUS_IS_PENDING.message)
    }

    @Test
    @DisplayName("해당 챌린지를 눌렀을 때, 이 챌린지를 마쳤지만 리포트를 확인하지 못했다면, 챌린지 리포트를 다시 발급받아야 한다.")
    fun participateChallengeWithDead() {
        // given
        userChallengeCommandService.execute(challengeParticipantDto.copy(
            status = EUserChallengeStatus.DEAD
        ))
        // when
        val exception = assertThrows<InternalServerErrorException> {
            userChallengeCommandService.execute(challengeParticipantDto)
        }
        // then
        assertThat(exception).isInstanceOf(InternalServerErrorException::class.java)
        assertThat(exception.message).isEqualTo(UserChallengeException.REPORT_NOT_FOUND_AND_STATUS_IS_DEAD.message)
    }

    @Test
    @DisplayName("해당 챌린지를 눌렀을 때, 해당 챌린지를 이전에 참여한 이력이 있어도, 2일 내로 완료한 챌린지가 아니라면 챌린지에 새로 참가할 수 있다.")
    fun participateChallengeWithCompleted() {
        // given
        val completedUserChallenge = userChallengeCommandService.execute(
            challengeParticipantDto.copy(
                status = EUserChallengeStatus.COMPLETED
            )
        )

        // when
        val userChallenge = userChallengeCommandService.execute(challengeParticipantDto)

        // then
        assertThat(userChallenge.userId).isEqualTo(userId).isEqualTo(completedUserChallenge.userId)
        assertThat(userChallenge.challengeTitle).isEqualTo(challengeTitle).isEqualTo(completedUserChallenge.challengeTitle)
        assertThat(userChallenge.status).isEqualTo(EUserChallengeStatus.RUNNING)
        assertThat(userChallenge.participantsDate).isEqualTo(participationDays)
        assertThat(userChallenge.iceCount).isZero()
        assertThat(userChallenge.maxConsecutiveDays).isEqualTo(userChallenge.nowConsecutiveDays).isZero()
        assertThat(userChallenge.reportMessage).isNull()
        assertThat(userChallenge.challengeHistories)
            .hasSize(participationDays)
            .allMatch { it.isCertificated == EUserChallengeCertificationStatus.FAILED }
        assertThat(completedUserChallenge.userChallengeId).isNotEqualTo(userChallenge.userChallengeId)
    }
}