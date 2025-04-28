package com.app.server.challenge_certification.infra

import com.app.server.challenge_certification.ui.dto.SendToCertificationServerRequestDto
import org.springframework.stereotype.Service

@Service
class CertificationInfraService {

    fun certificate(sendToCertificationServerRequestDto: SendToCertificationServerRequestDto?) : Boolean {
        return true
    }
}