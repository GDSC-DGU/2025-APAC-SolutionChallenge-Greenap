package com.app.server.feed.ui.dto

data class FeedListResponseDto(
    val feedList: List<FeedDto>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)

data class FeedDto(
    val id: Long,
    val category: String,
    val challengeTitle: String,
    val imageUrl: String,
    val content: String,
    val createdAt: String, // 또는 LocalDateTime으로 바꿀 수 있음
    val user: FeedUserDto
)

data class FeedUserDto(
    val nickname: String,
    val profileImageUrl: String,
    val burningLevel: Int
)