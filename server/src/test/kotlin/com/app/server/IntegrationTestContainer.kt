package com.app.server

import com.app.server.core.security.util.JwtUtil
import org.jasypt.encryption.StringEncryptor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.GenericContainer
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
    protected val secondUserId = 2L
    protected val secondUserName = "testUser2"
    protected val userName: String = "testUser"
    protected val challengeId: Long = 1L
    protected var userChallengeId: Long = 1L
    protected val participationDays: Int = 7
    protected val participantsStartDate: LocalDate = LocalDate.now()
    protected val testEmail = "testEmail@email.com"
    protected val categoryId = 1L
    protected val imageUrl = "testImageUrl"
    protected val challengeTitle = "유휴 전원 / 대기 전력 OFF"
    protected val challengeDescription = "사용하지 않는 전자기기의 전원을 끄거나, 플러그를 뽑는 챌린지입니다. TV, 컴퓨터 등의 대기 전력을 차단함으로써 불필요한 전기 낭비를 줄이고 온실가스 배출을 저감할 수 있습니다."
    protected val accessTokenValidationTime = 360000L
    protected val token : String by lazy {
        jwtUtil.createToken(userId, testEmail, "USER", accessTokenValidationTime)
    }

    companion object {

        @Container
        var mySQLContainer = KMySQLContainer(DockerImageName.parse("mysql:8.0.40"))
            .withReuse(true)

        @Container
        var redisContainer = KRedisContainer(DockerImageName.parse("redis:7.0.11"))
            .withReuse(true)

        @DynamicPropertySource
        @JvmStatic
        fun setProperties(registry: DynamicPropertyRegistry) {
            println("MySQL Container URL: ${mySQLContainer.jdbcUrl}")
            println("MySQL Container isRunning: ${mySQLContainer.isRunning()}")
            registry.add("spring.datasource.url") { mySQLContainer.jdbcUrl }
            registry.add("spring.datasource.username") { mySQLContainer.username }
            registry.add("spring.datasource.password") { mySQLContainer.password }
            
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379) }
        }
    }

}

class KMySQLContainer(dockerImageName: DockerImageName) : MySQLContainer<KMySQLContainer>(dockerImageName)

class KRedisContainer(dockerImageName: DockerImageName) : GenericContainer<KRedisContainer>(dockerImageName) {
    init {
        withExposedPorts(6379)
    }
}