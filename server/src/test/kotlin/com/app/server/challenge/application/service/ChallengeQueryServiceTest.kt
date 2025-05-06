package com.app.server.challenge.application.service

import com.app.server.IntegrationTestContainer
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback

@Transactional
@Rollback
@SpringBootTest
class ChallengeQueryServiceTest : IntegrationTestContainer() {

    @Autowired
    private lateinit var challengeQueryService: ChallengeQueryService

    @Test
    @DisplayName("카테고리 리스트와 챌린지 리스트를 정상적으로 조회한다")
    fun getTotalChallenges() {
        // when
        val categories = challengeQueryService.execute()

        // then
        assertTrue(categories.isNotEmpty(), "카테고리 목록은 비어있지 않아야 한다")
        categories.forEach { category ->
            assertNotNull(category.id)
            assertNotNull(category.title)
            assertNotNull(category.description)
            category.challenges.forEach { challenge ->
                assertNotNull(challenge.id)
                assertNotNull(challenge.title)
            }
        }
    }

    @Test
    @DisplayName("챌린지 상세 정보를 정상적으로 조회한다")
    fun getChallengeDetailList() {
        // given
        val allCategories = challengeQueryService.execute()
        val anyChallengeId = allCategories
            .flatMap { it.challenges }
            .firstOrNull()?.id ?: fail("테스트를 위해 챌린지가 하나 이상 필요합니다.")

        // when
        val challengeDetail = challengeQueryService.getChallengeDetail(anyChallengeId)

        // then
        assertEquals(anyChallengeId, challengeDetail.id)
        assertNotNull(challengeDetail.title)
        assertNotNull(challengeDetail.description)
    }
}