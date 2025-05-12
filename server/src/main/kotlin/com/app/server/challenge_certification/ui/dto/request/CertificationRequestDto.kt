package com.app.server.challenge_certification.ui.dto.request

import com.app.server.challenge.domain.model.Challenge
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.web.multipart.MultipartFile

data class CertificationRequestDto(
    @JsonProperty("user_challenge_id")
    val userChallengeId: Long,
    @JsonProperty("image")
    val image: MultipartFile,
)