package com.app.server.feed.ui.dto.request

import com.app.server.feed.ui.dto.CreateFeedCommand
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime

data class CreateFeedRequestDto(
    @JsonProperty("user_challenge_id")
    val userChallengeId: Long,
    @JsonProperty("image_url")
    val imageUrl: String,
    val content: String? = null,
    @JsonProperty("publish_date")
    val publishDate: LocalDate
){
    fun toCommand(userId: Long) = CreateFeedCommand(
        userChallengeId = userChallengeId,
        imageUrl = imageUrl,
        content = content,
        publishDate = publishDate.atTime(LocalDateTime.now().hour, LocalDateTime.now().minute),
        userId = userId
    )
}