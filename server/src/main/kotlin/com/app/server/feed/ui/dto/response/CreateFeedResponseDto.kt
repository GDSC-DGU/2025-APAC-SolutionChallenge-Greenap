package com.app.server.feed.ui.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateFeedResponseDto(
    @JsonProperty("feed_id")
    val feedId: Long,
    @JsonProperty("image_url")
    val imageUrl: String,
    @JsonProperty("content")
    val content: String?,
    @JsonProperty("user_challenge_id")
    val userChallengeId: Long,
)