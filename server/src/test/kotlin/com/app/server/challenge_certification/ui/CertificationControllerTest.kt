package com.app.server.challenge_certification.ui

import com.app.server.IntegrationTestContainer
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import kotlin.test.Test

@SpringBootTest
class CertificationControllerTest : IntegrationTestContainer() {

    @Test
    @DisplayName("오늘의 챌린지를 인증할 수 있다.")
    @Disabled
    fun participateChallenge() {
        // when
        mockMvc.post("/api/v1/challenge/user/$userChallengeId/certification") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
            content = """
                {
                    "image_url" : "testImageUrl",
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