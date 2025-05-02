package com.app.server.feed.application.repository

import com.app.server.feed.domain.model.query.FeedProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface FeedProjectionRepository : JpaRepository<FeedProjection, Long> {

    @Query("SELECT fp FROM FeedProjection fp WHERE fp.id = :id AND fp.deletedAt IS NULL")
    override fun findById(id: Long): Optional<FeedProjection>
}