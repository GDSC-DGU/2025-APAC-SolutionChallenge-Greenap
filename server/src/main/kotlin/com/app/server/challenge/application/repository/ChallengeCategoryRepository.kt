package com.app.server.challenge.application.repository

import com.app.server.challenge.domain.model.ChallengeCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ChallengeCategoryRepository : JpaRepository<ChallengeCategory, Long> {

    @Query("SELECT cc FROM ChallengeCategory cc JOIN FETCH cc.challenges")
    override fun findAll(): List<ChallengeCategory>
}