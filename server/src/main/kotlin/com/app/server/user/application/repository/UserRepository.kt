package com.app.server.user.application.repository

import com.app.server.user.domain.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.id = :userId " +
            "AND u.deletedAt IS NULL")
    override fun findById(userId: Long): Optional<User>

    @Query("SELECT u FROM User u WHERE u.email = :email " +
            "AND u.deletedAt IS NULL")
    fun findByEmail(email: String): User?

    @Query("SELECT u FROM User u " +
            "WHERE u.id = :userId " +
            "AND u.refreshToken IS NOT NULL")
    fun findByIdAndRefreshTokenNotNull(userId: Long): User?

    @Query("SELECT u.nowMaxConsecutiveParticipationDayCount " +
            "FROM User u " +
            "WHERE u.id = :userId " +
            "AND u.deletedAt IS NULL")
    fun findNowMaxConsecutiveParticipationDayCountById(userId: Long): Int?

    @Modifying
    @Query("UPDATE User u " +
            "SET u.nowMaxConsecutiveParticipationDayCount = :maxConsecutiveParticipationDayCount " +
            "WHERE u.id = :userId " +
            "AND u.deletedAt IS NULL")
    fun updateNowMaxConsecutiveParticipationDayCountById(userId: Long, maxConsecutiveParticipationDayCount: Long): Int?
}
