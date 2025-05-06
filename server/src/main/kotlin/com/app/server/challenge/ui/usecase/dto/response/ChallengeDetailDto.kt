package com.app.server.challenge.ui.usecase.dto.response

import com.app.server.challenge.domain.enums.EChallengeStatus
import com.app.server.challenge.domain.model.Challenge

data class ChallengeDetailDto(
    val id: Long?,
    val title: String,
    val preDescription: String,
    val description: String,
    val certificationMethodDescription: String,
    val status: EChallengeStatus = EChallengeStatus.RUNNING,
    val mainImageUrl: String?,
    val certificationExampleImageUrl: String?,
    val categoryId: Long?,
    val categoryTitle: String,
){
    constructor(challenge: Challenge) : this(
        id = challenge.id,
        title = challenge.title,
        preDescription = challenge.preDescription,
        description = challenge.description,
        certificationMethodDescription = challenge.certificationMethodDescription,
        mainImageUrl = challenge.mainImageUrl,
        certificationExampleImageUrl = challenge.certificationExampleImageUrl,
        categoryId = challenge.challengeCategory.id,
        categoryTitle = challenge.challengeCategory.title,
    )
}
