package com.app.server.user.application.service

import com.app.server.common.exception.NotFoundException
import com.app.server.user.application.repository.UserRepository
import com.app.server.user.domain.model.User
import com.app.server.user.exception.UserExceptionCode
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun updateRefreshToken(userId: Long?, refreshToken: String?) {
    }

    fun findById(userId: Long): User =
        userRepository.findById(userId)
            .orElseThrow { NotFoundException(UserExceptionCode.NOT_FOUND_USER) }
}
