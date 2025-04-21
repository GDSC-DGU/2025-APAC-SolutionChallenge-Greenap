package com.app.server.challenge.application.service

import com.app.server.challenge.application.repository.ChallengeCategoryRepository
import com.app.server.challenge.domain.model.ChallengeCategory
import org.springframework.stereotype.Service

@Service
class ChallengeCategoryService (
    private val challengeCategoryRepository: ChallengeCategoryRepository
){

    fun findAll() : List<ChallengeCategory> {
        return challengeCategoryRepository.findAll()
    }
}