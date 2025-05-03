package com.app.server.challenge_certification.domain.event

import java.time.LocalDate

data class CertificationSucceededEvent(
    val userChallengeId: Long,
    val imageUrl: String,
    val certificatedDate: LocalDate
)

