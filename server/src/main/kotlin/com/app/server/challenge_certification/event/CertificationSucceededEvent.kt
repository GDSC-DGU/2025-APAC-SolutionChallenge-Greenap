package com.app.server.challenge_certification.event

import java.time.LocalDate

data class CertificationSucceededEvent(
    val userChallengeId: Long,
    val imageUrl: String,
    val certificatedDate: LocalDate
)

