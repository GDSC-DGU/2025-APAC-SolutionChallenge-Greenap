package com.app.server.feed.ui.dto

import java.time.LocalDateTime

data class CreateFeedCommand(
    val userChallengeId: Long,
    val imageUrl: String,
    val content: String? = null,
    val publishDate: LocalDateTime,
    val userId : Long
)
