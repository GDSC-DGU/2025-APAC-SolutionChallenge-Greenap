package com.app.server.user_challenge.application.usecase

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.ui.usecase.dto.request.ChallengeParticipantDto
import com.app.server.common.exception.BadRequestException
import com.app.server.common.exception.BusinessException
import com.app.server.common.exception.InternalServerErrorException
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.ui.usecase.ParticipantChallengeUseCase
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
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
    private lateinit var participantChallengeUseCase: ParticipantChallengeUseCase

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


    @Test
    @DisplayName("특정 챌린지에 원하는 기간으로 참여할 수 있다.")
    fun participateChallenge() {

        // when
        val userChallenge : UserChallenge = participantChallengeUseCase.execute(challengeParticipantDto)

        // then
        assertThat(userChallenge.userId).isEqualTo(userId)
        assertThat(userChallenge.challenge.id).isEqualTo(challengeId)
        assertThat(userChallenge.status).isEqualTo(EUserChallengeStatus.RUNNING)
        assertThat(userChallenge.participantDays).isEqualTo(participationDays)
        assertThat(userChallenge.iceCount).isZero()
        assertThat(userChallenge.maxConsecutiveParticipationDayCount).isEqualTo(userChallenge.nowConsecutiveParticipationDayCount).isZero()
        assertThat(userChallenge.reportMessage).isNull()
        assertThat(userChallenge.getUserChallengeHistories())
            .hasSize(participationDays)
            .allMatch { it.status == EUserChallengeCertificationStatus.FAILED }
    }

    @Test
    @DisplayName("해당 챌린지를 눌렀을 때, 이 챌린지에 이미 참여하고 있다면, 새로 참여할 수 없다.")
    fun participateChallengeWithAlreadyParticipated() {
        // given
        participantChallengeUseCase.execute(challengeParticipantDto)
        // when
        val exception = assertThrows<BadRequestException> {
            participantChallengeUseCase.execute(challengeParticipantDto)
        }
        // then
        assertThat(exception).isInstanceOf(BadRequestException::class.java)
        assertThat(exception.message).isEqualTo(UserChallengeException.ALREADY_PARTICIPATED_AND_STATUS_IS_RUNNING.message)
    }

    @Test
    @DisplayName("해당 챌린지를 눌렀을 때, 챌린지를 마쳤지만 아직 챌린지 리포트를 확인하지 못한 경우 리포트를 먼저 확인해야 한다.")
    fun participateChallengeWithNotParticipated() {
        // given
        participantChallengeUseCase.execute(challengeParticipantDto.copy(
            status = EUserChallengeStatus.PENDING
        ))
        // when
        val exception = assertThrows<BadRequestException> {
            participantChallengeUseCase.execute(challengeParticipantDto)
        }
        // then
        assertThat(exception).isInstanceOf(BadRequestException::class.java)
        assertThat(exception.message).isEqualTo(UserChallengeException.CHALLENGE_WAITED_AND_STATUS_IS_PENDING.message)
    }

    @Test
    @DisplayName("해당 챌린지를 눌렀을 때, 챌린지를 마치고 리포트까지 확인한 챌린지가 이미 있다면 해당 챌린지를 이어할 수 있다. ")
    fun participateChallengeWithWait() {
        // given
        participantChallengeUseCase.execute(challengeParticipantDto.copy(
            status = EUserChallengeStatus.WAITING
        ))
        // when
        val exception = assertThrows<BusinessException> {
            participantChallengeUseCase.execute(challengeParticipantDto)
        }
        // then
        assertThat(exception).isInstanceOf(BusinessException::class.java)
        assertThat(exception.message).isEqualTo(UserChallengeException.CONTINUE_CHALLENGE_AND_STATUS_IS_WAITING.message)
    }

    @Test
    @DisplayName("해당 챌린지를 눌렀을 때, 이 챌린지를 마쳤지만 리포트를 확인하지 못했다면, 챌린지 리포트를 다시 발급받아야 한다.")
    fun participateChallengeWithDead() {
        // given
        participantChallengeUseCase.execute(challengeParticipantDto.copy(
            status = EUserChallengeStatus.DEAD
        ))
        // when
        val exception = assertThrows<InternalServerErrorException> {
            participantChallengeUseCase.execute(challengeParticipantDto)
        }
        // then
        assertThat(exception).isInstanceOf(InternalServerErrorException::class.java)
        assertThat(exception.message).isEqualTo(UserChallengeException.REPORT_NOT_FOUND_AND_STATUS_IS_DEAD.message)
    }

    @Test
    @DisplayName("해당 챌린지를 눌렀을 때, 해당 챌린지를 이전에 참여한 이력이 있어도, 2일 내로 완료한 챌린지가 아니라면 챌린지에 새로 참가할 수 있다.")
    fun participateChallengeWithCompleted() {
        // given
        val completedUserChallenge = participantChallengeUseCase.execute(
            challengeParticipantDto.copy(
                status = EUserChallengeStatus.COMPLETED
            )
        )

        // when
        val userChallenge = participantChallengeUseCase.execute(challengeParticipantDto)

        // then
        assertThat(userChallenge.userId).isEqualTo(userId).isEqualTo(completedUserChallenge.userId)
        assertThat(userChallenge.challenge.id).isEqualTo(challengeId).isEqualTo(completedUserChallenge.challenge.id)
        assertThat(userChallenge.status).isEqualTo(EUserChallengeStatus.RUNNING)
        assertThat(userChallenge.participantDays).isEqualTo(participationDays)
        assertThat(userChallenge.iceCount).isZero()
        assertThat(userChallenge.maxConsecutiveParticipationDayCount).isEqualTo(userChallenge.nowConsecutiveParticipationDayCount).isZero()
        assertThat(userChallenge.reportMessage).isNull()
        assertThat(userChallenge.getUserChallengeHistories())
            .hasSize(participationDays)
            .allMatch { it.status == EUserChallengeCertificationStatus.FAILED }
        assertThat(completedUserChallenge.id).isNotEqualTo(userChallenge.id)
    }
}