package com.app.server.challenge_certification.infra

import com.app.server.challenge_certification.ui.dto.SendToCertificationServerRequestDto
import org.springframework.stereotype.Service

@Service
class CertificationInfraService {

    fun certificate(sendToCertificationServerRequestDto: SendToCertificationServerRequestDto?) : Boolean {
        // TODO: AI 서버와의 통신 로직 구현
        return true
    }
}