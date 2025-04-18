package com.app.server.user.application.repository

import com.app.server.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email: String): User?

    fun findByIdAndRefreshTokenNotNull(userId: Long): User?
}
