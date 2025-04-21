package com.app.server.challenge.ui

import com.app.server.IntegrationTestContainer
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@SpringBootTest
class ChallengeControllerTest : IntegrationTestContainer(){

    @Test
    @DisplayName("모든 챌린지 리스트를 성공적으로 가져온다")
    fun getAllChallengeList() {
        mockMvc.get("/api/v1/challenges")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.data").exists()
            }
    }

    @Test
    @DisplayName("특정 챌린지를 ID로 조회할 수 있다.")
    fun getChallengeById() {

        mockMvc.get("/api/v1/challenges/$challengeId")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.data").exists()
            }
    }
}