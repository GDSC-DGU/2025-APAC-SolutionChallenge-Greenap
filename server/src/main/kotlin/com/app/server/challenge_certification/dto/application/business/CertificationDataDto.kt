package com.app.server.challenge_certification.dto.application.business

import java.time.LocalDate

data class CertificationDataDto(
    val imageUrl: String,
    val certificationDate: LocalDate
)