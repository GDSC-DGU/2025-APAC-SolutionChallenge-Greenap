package com.app.server.challenge.application.repository

import com.app.server.challenge.domain.model.Challenge
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ChallengeRepository : JpaRepository<Challenge, Long>
{
    @Query("SELECT c FROM Challenge c WHERE c.title = :title AND c.deletedAt IS NULL")
    fun findByTitle(title: String): Challenge
}