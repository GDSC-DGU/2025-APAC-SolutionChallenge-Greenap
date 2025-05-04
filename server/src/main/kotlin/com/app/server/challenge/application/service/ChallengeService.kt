package com.app.server.challenge.application.service

import com.app.server.challenge.application.repository.ChallengeRepository
import com.app.server.challenge.domain.model.Challenge
import com.app.server.challenge.exception.ChallengeException
import com.app.server.common.enums.CommonResultCode
import com.app.server.common.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class ChallengeService(
    private val challengeRepository: ChallengeRepository
) {

    fun findById(challengeId: Long): Challenge {
        return challengeRepository.findById(challengeId).orElseThrow {
            throw NotFoundException(ChallengeException.NOT_FOUND)
        }
    }

    fun findAll() : List<Challenge> {
        return challengeRepository.findAll()
    }

    fun findByTitle(title: String): Challenge {
        return challengeRepository.findByTitle(title)
    }
}