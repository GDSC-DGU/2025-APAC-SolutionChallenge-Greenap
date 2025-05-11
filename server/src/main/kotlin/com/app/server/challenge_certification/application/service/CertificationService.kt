package com.app.server.challenge_certification.application.service

import com.app.server.challenge_certification.application.dto.command.CertificationCommand

interface CertificationService {

    fun certificateImage(certificationCommand: CertificationCommand): String
}