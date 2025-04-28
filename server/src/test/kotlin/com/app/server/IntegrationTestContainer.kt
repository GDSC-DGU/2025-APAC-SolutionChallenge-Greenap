package com.app.server

import com.app.server.challenge.domain.model.Challenge
import com.app.server.challenge.domain.model.ChallengeCategory
import com.app.server.common.security.util.JwtUtil
import org.jasypt.encryption.StringEncryptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@DirtiesContext
@AutoConfigureMockMvc
abstract class IntegrationTestContainer {

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    protected lateinit var stringEncryptor: StringEncryptor

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtUtil: JwtUtil

    protected val userId: Long = 1L
    protected val challengeId: Long = 1L
    protected val userChallengeId: Long = 1L
    protected val participationDays: Int = 7
    protected val participantsStartDate: LocalDate = LocalDate.now()
    protected val testEmail = "testEmail@email.com"
    protected val accessTokenValidationTime = 360000L
    protected val token : String by lazy {
        jwtUtil.createToken(userId, testEmail, "USER", accessTokenValidationTime)
    }

    companion object {
        @Container
        var mySQLContainer = KMySQLContainer(DockerImageName.parse("mysql:8.0.40"))
            .withReuse(true)

        @DynamicPropertySource
        @JvmStatic
        fun setProperties(registry: DynamicPropertyRegistry) {
            println("MySQL Container URL: ${mySQLContainer.jdbcUrl}")
            println("MySQL Container isRunning: ${mySQLContainer.isRunning()}")
            registry.add("spring.datasource.url") { mySQLContainer.jdbcUrl }
            registry.add("spring.datasource.username") { mySQLContainer.username }
            registry.add("spring.datasource.password") { mySQLContainer.password }
        }
    }

}

class KMySQLContainer(dockerImageName: DockerImageName) : MySQLContainer<KMySQLContainer>(dockerImageName)