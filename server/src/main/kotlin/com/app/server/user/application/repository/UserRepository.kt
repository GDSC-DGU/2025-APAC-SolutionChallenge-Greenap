package com.app.server.user.application.repository

import com.app.server.user.domain.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.id = :userId " +
            "AND u.deletedAt IS NULL")
    override fun findById(userId: Long): Optional<User>

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
}
