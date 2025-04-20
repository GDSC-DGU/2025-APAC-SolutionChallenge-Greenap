package com.app.server.rank.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "challenge_rank_snapshots")
data class ChallengeRankSnapshot @PersistenceCreator constructor(
    @Id
    val id: String? = null,

    val challengeId: Long,
    val timestamp: LocalDateTime,

    val rankings: List<ChallengeUserRank> = listOf()
)

data class ChallengeUserRank(
    val userId: Long,
    val score: Long,
    val rank: Int
)