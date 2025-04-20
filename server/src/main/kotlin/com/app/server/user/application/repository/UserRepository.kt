package com.app.server.user.application.repository

import com.app.server.user.domain.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email: String): User?

    fun findByIdAndRefreshTokenNotNull(userId: Long): User?
}
