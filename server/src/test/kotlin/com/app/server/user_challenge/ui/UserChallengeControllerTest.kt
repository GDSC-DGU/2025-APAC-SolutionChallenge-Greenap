package com.app.server.user_challenge.ui

import com.app.server.IntegrationTestContainer
import com.app.server.common.security.info.CustomUserDetails
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.post
import kotlin.test.Test

@SpringBootTest
class UserChallengeControllerTest : IntegrationTestContainer() {

    @Test
    @DisplayName("챌린지에 참여할 수 있다.")
    @Disabled
    fun participateChallenge() {
        // given
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        val userDetails = CustomUserDetails(userId, testEmail, authorities)
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication
        SecurityContextHolder.setContext(context)

        // when & then
        mockMvc.post("/api/v1/challenges") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
            content = """
                {
                    "challenge_id": $challengeId,
                    "participants_date": $participationDays
                }
            """.trimIndent()
        }
        .andExpect {
            status { isOk() }
        }
    }
}