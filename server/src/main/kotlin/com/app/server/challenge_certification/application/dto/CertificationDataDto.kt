package com.app.server.challenge_certification.application.dto

import java.time.LocalDate

data class CertificationDataDto(
    val imageUrl: String,
    val certificationDate: LocalDate
)
