package com.app.server.feed.ui.dto

import java.time.LocalDate

data class CreateFeedCommand(
    val userChallengeId: Long,
    val imageUrl: String,
    val content: String? = null,
    val publishDate: LocalDate,
    val userId : Long
)
