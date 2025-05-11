package com.app.server.challenge_certification.port.outbound

import com.app.server.challenge_certification.ui.dto.request.SendToCertificationServerRequestDto
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode

interface CertificationPort {

    fun verifyCertificate(sendToCertificationServerRequestDto: SendToCertificationServerRequestDto)
            : Map<EUserCertificatedResultCode, String>
}