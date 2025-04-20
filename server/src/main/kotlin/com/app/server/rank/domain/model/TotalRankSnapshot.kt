package com.app.server.rank.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "total_rank_snapshots")
data class TotalRankSnapshot @PersistenceCreator constructor(
    @Id
    val id: String? = null,

    val timestamp: LocalDateTime,

    val rankings: List<UserRank> = listOf()
)

data class UserRank(
    val userId: Long,
    val score: Long,
    val rank: Int
)
