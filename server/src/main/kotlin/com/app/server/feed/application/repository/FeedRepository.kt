package com.app.server.feed.application.repository

import com.app.server.feed.domain.model.command.Feed
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FeedRepository : JpaRepository<Feed, Long> {

    @Query("SELECT f FROM Feed f WHERE f.id = :id AND f.deletedAt IS NULL")
    override fun findById(id: Long): Optional<Feed>
}
