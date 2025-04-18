package com.app.server

import org.jasypt.encryption.StringEncryptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@ActiveProfiles("test")
abstract class IntegrationTestContainer {

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    protected lateinit var stringEncryptor: StringEncryptor

    companion object {
        private const val USERNAME = "test"
        private const val PASSWORD = "password"
        private const val DATABASE = "test_db"

        @Container
        @JvmStatic
        var mySQLContainer: MySQLContainer<*> = KMySQLContainer(DockerImageName.parse("mysql:8.0.40"))
            .withDatabaseName(DATABASE)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)
            .withReuse(true)
    }
}

class KMySQLContainer(dockerImageName: DockerImageName) : MySQLContainer<KMySQLContainer>(dockerImageName)