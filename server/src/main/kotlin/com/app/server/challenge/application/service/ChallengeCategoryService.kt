package com.app.server.challenge.application.service

import com.app.server.challenge.application.repository.ChallengeCategoryRepository
import com.app.server.challenge.domain.model.ChallengeCategory
import com.app.server.challenge.exception.ChallengeException
import com.app.server.common.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class ChallengeCategoryService(
    private val challengeCategoryRepository: ChallengeCategoryRepository
) {

    fun getNameByCategoryId(challengeCategoryId: Long): String {
        return challengeCategoryRepository.getTitleById(challengeCategoryId)
            .orElseThrow { NotFoundException(ChallengeException.NOT_FOUND_CHALLENGE_CATEGORY) }
    }

    fun findAll(): List<ChallengeCategory> {
        return challengeCategoryRepository.findAll()
    }
}