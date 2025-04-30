package com.app.server.challenge_certification.event

import java.time.LocalDate

data class CertificationSucceededEvent(
    val userId : Long,
    val userChallengeId: Long,
    val imageUrl: String,
    val certificatedDate: LocalDate
)

