package com.app.server.challenge_certification.port.outbound

import com.app.server.challenge_certification.dto.ui.request.SendToCertificationServerRequestDto
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode

interface CertificationPort {

    fun verifyCertificate(sendToCertificationServerRequestDto: SendToCertificationServerRequestDto)
            : Map<EUserCertificatedResultCode, String>
}