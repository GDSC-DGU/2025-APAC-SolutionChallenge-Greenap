package com.app.server.challenge_certification.application.service

import com.app.server.IntegrationTestContainer
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.Test

@SpringBootTest
@Transactional
@Rollback
@ExtendWith(SpringExtension::class)
class CertificateServiceTest : IntegrationTestContainer() {


    @Test
    @Disabled
    @DisplayName("챌린지 인증에 실패했다면, 인증에 실패하였음을 클라이언트에게 전달한다.")
    fun failChallenge() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지 인증에 성공했다면, 인증 사진을 저장한다.")
    fun saveChallengeImage() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지 인증에 성공하여 사진까지 저장했다면, 인증 성공 이벤트를 게시한다.")
    fun publishChallengeImage() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지 인증에 성공한 날짜가 챌린지 종료일자와 같다면, 리포트 메시지를 AI 서버로부터 받아온다.")
    fun getReportMessage() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지 리포트 메시지를 받아왔다면 저장하고, 챌린지의 상태를 Pending으로 변경한다.")
    fun saveReportMessage() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("마지막 날짜의 챌린지 인증에는 성공하였으나, 리포트 메시지를 받아오지 못했다면, 챌린지의 상태를 Dead으로 변경한다.")
    fun getReportMessageWithFail() {
        // given
        // when
        // then
    }
}