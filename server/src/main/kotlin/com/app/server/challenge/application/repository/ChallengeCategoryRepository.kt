package com.app.server.challenge.application.repository

import com.app.server.challenge.domain.model.ChallengeCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChallengeCategoryRepository : JpaRepository<ChallengeCategory, Long> {

    @Query("SELECT cc.title FROM ChallengeCategory cc " +
            "WHERE cc.id = :challengeCategoryId " +
            "AND cc.deletedAt IS NULL")
    fun getTitleById(challengeCategoryId: Long): Optional<String>

    @Query("SELECT cc FROM ChallengeCategory cc " +
            "JOIN FETCH cc.challenges " +
            "WHERE cc.deletedAt IS NULL")
    override fun findAll(): List<ChallengeCategory>
}