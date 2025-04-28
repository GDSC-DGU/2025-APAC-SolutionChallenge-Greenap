package com.app.server.challenge_certification.application.dto

import java.time.LocalDate

data class CertificationFacadeToServiceDto(
    val imageUrl: String,
    val certificationDate: LocalDate
)
