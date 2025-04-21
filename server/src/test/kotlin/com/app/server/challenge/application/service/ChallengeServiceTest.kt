package com.app.server.challenge.application.service

import com.app.server.IntegrationTestContainer
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback

@Transactional
@Rollback
@SpringBootTest
class ChallengeServiceTest : IntegrationTestContainer() {

    @Autowired
    private lateinit var challengeService: ChallengeService
    @Autowired
    private lateinit var challengeCategoryService: ChallengeCategoryService

    @Test
    @DisplayName("서비스 중인 챌린지 목록을 조회할 수 있다.")
    fun getListChallenges() {
        // given

        // when
        val categories = challengeCategoryService.findAll()
        val challenges = challengeService.findAll()

        // then
        assertThat(categories.first().title).isEqualTo("자원 절약")
        assertThat(categories.last().title).isEqualTo("교통 절감")
        assertThat(categories.first().challenges.first().title).isEqualTo("유휴 전원 / 대기 전력 OFF")

        assertThat(challenges).size().isEqualTo(19)
        assertThat(challenges.first().title).isEqualTo("유휴 전원 / 대기 전력 OFF")
        assertThat(challenges.last().title).isEqualTo("플로깅 실천하기 챌린지")
    }

    @Test
    @DisplayName("특정 챌린지를 챌린지명으로 검색해서 가져올 수 있다.")
    fun getChallengeByTitle() {
        // given
        val categoryTitle: String = "자원 절약"
        val challengeTitle: String = "유휴 전원 / 대기 전력 OFF"
        // when
        val foundChallenge = challengeService.findByTitle(challengeTitle)

        // then
        assertThat(foundChallenge.title).isEqualTo(challengeTitle)
        assertThat(foundChallenge.challengeCategory.title).isEqualTo(categoryTitle)
    }

}