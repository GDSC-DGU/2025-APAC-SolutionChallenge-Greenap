package com.app.server.user_challenge.ui

import com.app.server.IntegrationTestContainer
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import kotlin.test.Test

@SpringBootTest
class UserChallengeControllerTest : IntegrationTestContainer() {

    @Test
    @Disabled
    @DisplayName("챌린지에 참여할 수 있다.")
    fun participateChallenge() {
        // when
        mockMvc.post("/api/v1/challenge") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                    "challenge_id": $challengeId,
                    "participants_date": $participationDays
                }
            """.trimIndent()

        }
        // then
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
            }

    }
}