package com.app.server.user_challenge.application.service

import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge.application.usecase.dto.request.ChallengeParticipantDto
import com.app.server.common.exception.BadRequestException
import com.app.server.common.exception.BusinessException
import com.app.server.common.exception.InternalServerErrorException
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.usecase.ParticipantChallengeUseCase
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class UserChallengeCommandService (
    private val userChallengeService: UserChallengeService,
    private val challengeService: ChallengeService,
    private val userChallengeHistoryCommandService: UserChallengeHistoryCommandService
) : ParticipantChallengeUseCase {

    override fun execute(
        challengeParticipantDto: ChallengeParticipantDto,
    ) : UserChallenge {
        validateUserCanParticipateInChallenge(challengeParticipantDto)
        val userChallenge = createUserChallenge(challengeParticipantDto)
        saveUserChallengeWithHistory(userChallenge, challengeParticipantDto.participantsDate)
        return userChallenge
    }

    private fun validateUserCanParticipateInChallenge(challengeParticipantDto: ChallengeParticipantDto) {
        val existingUserChallenge = userChallengeService.findByUserIdAndChallengeId(
            userId = challengeParticipantDto.userId,
            challengeId = challengeParticipantDto.challengeId
        )

        existingUserChallenge?.validateCanParticipants()
    }

    private fun createUserChallenge(
        challengeParticipantDto: ChallengeParticipantDto
    ): UserChallenge {
        val challenge = challengeService.findById(challengeParticipantDto.challengeId)
        return UserChallenge.createEntity(
            CreateUserChallengeDto(
                userId = challengeParticipantDto.userId,
                challenge = challenge,
                participantsDate = challengeParticipantDto.participantsDate,
                status =challengeParticipantDto.status
            )
        )
    }

    private fun saveUserChallengeWithHistory(userChallenge: UserChallenge, participantsDate: Int) {
        // UserChallengeHistory를 생성하고 UserChallenge에 연결
        userChallengeHistoryCommandService.createUserChallengeHistory(
            userChallenge = userChallenge,
            participantsDate = participantsDate
        )

        // UserChallenge와 연결된 모든 히스토리가 함께 저장됨
        userChallengeService.save(userChallenge)
    }
}