package com.app.server.user_challenge.application.service

import com.app.server.common.enums.CommonResultCode
import com.app.server.common.exception.NotFoundException
import com.app.server.user_challenge.application.repository.UserChallengeRepository
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserChallengeService (
    private val userChallengeRepository: UserChallengeRepository
) {

    fun save(userChallenge: UserChallenge) : UserChallenge {
        return userChallengeRepository.save(userChallenge)
    }

    fun findById(userChallengeId: Long) : UserChallenge =
        userChallengeRepository.findById(userChallengeId)
            .orElseThrow { NotFoundException(CommonResultCode.NOT_FOUND, "해당 챌린지를 참여하고 있지 않습니다.")
            }
    fun findAllByUserId(userId: Long): List<UserChallenge> =
        userChallengeRepository.findAllByUserId(userId)

    fun countCompletedUserBy(challengeId: Long): Long =
        userChallengeRepository.countByChallengeIdAndStatusIsCompleted(
        challengeId, EUserChallengeStatus.COMPLETED
    )

    fun countAllParticipantUserBy(challengeId: Long): Long =
        userChallengeRepository.countByChallengeId(
        challengeId
    )

    fun findByUserIdAndChallengeId(userId: Long, challengeId: Long): UserChallenge? =
        userChallengeRepository.findByUserIdAndChallengeId(
            userId = userId,
            challengeId = challengeId
        )

    fun findHistoryByUserChallenge(userChallengeId: Long): List<UserChallengeHistory> {
        return findById(userChallengeId).getUserChallengeHistories()
    }

    fun deleteAll() {
        userChallengeRepository.deleteAll()
    }

    fun flush(){
        userChallengeRepository.flush()
    }

}