package com.app.server.feed.application.repository

import com.app.server.feed.domain.model.query.FeedProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FeedProjectionRepository : JpaRepository<FeedProjection, Long> {

    @Query("SELECT fp FROM FeedProjection fp" +
            " WHERE fp.id = :id " +
            "AND fp.deletedAt IS NULL")
    override fun findById(id: Long): Optional<FeedProjection>

    @Query("SELECT fp FROM FeedProjection fp " +
            "WHERE fp.id IN :feedIds " +
            "AND fp.deletedAt IS NULL " +
            "ORDER BY fp.createdAt DESC ")
    fun findByIdIn(feedIds: List<Long>, pageable: Pageable) : Page<FeedProjection>

    @Query("SELECT fp FROM FeedProjection fp " +
            "WHERE fp.deletedAt IS NULL " +
            "ORDER BY fp.createdAt DESC ")
    override fun findAll(pageable: Pageable) : Page<FeedProjection>

    @Query("SELECT fp FROM FeedProjection fp " +
            "WHERE fp.challengeCategoryTitle = :categoryName " +
            "AND fp.deletedAt IS NULL " +
            "ORDER BY fp.createdAt DESC ")
    fun findAllByCategoryName(categoryName: String, pageable: Pageable): Page<FeedProjection>

    @Query("SELECT fp FROM FeedProjection fp " +
            "WHERE fp.userId = :userId " +
            "AND fp.challengeCategoryTitle = :categoryName " +
            "AND fp.deletedAt IS NULL " +
            "ORDER BY fp.createdAt DESC ")
    fun findByUserIdAndCategoryName(userId: Long, categoryName: String, pageable: Pageable): Page<FeedProjection>
}