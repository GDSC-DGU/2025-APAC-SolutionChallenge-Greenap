package com.app.server.user_challenge.application.repository

import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.model.UserChallenge
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserChallengeRepository : JpaRepository<UserChallenge, Long> {

    @Query("SELECT COUNT(DISTINCT uc.userId) " +
            "FROM UserChallenge uc " +
            "WHERE uc.challenge.id = :challengeId " +
            "AND uc.status = :status " +
            "AND uc.deletedAt IS NULL")
    fun countByChallengeIdAndStatusIsCompleted(challengeId: Long, status: EUserChallengeStatus): Long

    @Query("SELECT COUNT(DISTINCT uc.userId) " +
            "FROM UserChallenge uc " +
            "WHERE uc.challenge.id = :challengeId " +
            "AND uc.deletedAt IS NULL")
    fun countByChallengeId(challengeId: Long): Long

    @Query("SELECT uc FROM UserChallenge uc " +
            "WHERE uc.userId = :userId " +
            "AND uc.challenge.id = :challengeId " +
            "AND uc.deletedAt IS NULL")
    fun findByUserIdAndChallengeId(userId: Long, challengeId: Long) : UserChallenge?

    @Query("SELECT uc FROM UserChallenge uc " +
            "WHERE uc.id = :userChallengeId " +
            "AND uc.deletedAt IS NULL")
    override fun findById(userChallengeId: Long): Optional<UserChallenge>

}
