package com.app.server.challenge_certification.ui.dto

import com.app.server.challenge.domain.model.Challenge
import com.fasterxml.jackson.annotation.JsonProperty

data class CertificationRequestDto(
    @JsonProperty("user_challenge_id")
    val userChallengeId: Long,
    @JsonProperty("image_url")
    val imageUrl: String
){
    fun toSendToCertificationServerRequestDto(challenge: Challenge) = SendToCertificationServerRequestDto(
        imageUrl = imageUrl,
        challengeId = challenge.id!!,
        challengeName = challenge.title,
        challengeDescription = challenge.description
    )

}