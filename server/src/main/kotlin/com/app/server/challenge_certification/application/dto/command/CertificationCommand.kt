package com.app.server.challenge_certification.application.dto.command

import com.app.server.challenge.domain.model.Challenge
import com.app.server.challenge_certification.ui.dto.request.SendToCertificationServerRequestDto
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

data class CertificationCommand(
    val userChallengeId: Long,
    val image: MultipartFile,
    val certificationDate: LocalDate
){
    fun toSendToCertificationServerRequestDto(
        challenge: Challenge,
        imageEncodingData: String
    ) = SendToCertificationServerRequestDto(
        "data:image;base64,$imageEncodingData",
        challenge.id!!,
        challenge.title,
        challenge.description
    )
}