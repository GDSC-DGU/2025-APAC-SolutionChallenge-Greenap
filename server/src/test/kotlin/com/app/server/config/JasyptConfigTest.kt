package com.app.server.config

import com.app.server.IntegrationTestContainer
import org.assertj.core.api.Assertions.assertThat
import org.jasypt.encryption.StringEncryptor
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JasyptConfigTest : IntegrationTestContainer() {

    @Test
    @DisplayName("JASYPT로 값을 암호화, 복호화 할 수 있다.")
    fun testEncrypt(){
        //given
        val exampleData = "test_JASYPT"

        //when
        val encryptedData: String = stringEncryptor.encrypt(exampleData)
        val decryptedData: String = stringEncryptor.decrypt(encryptedData)

        //then
        assertThat(decryptedData).isEqualTo(exampleData)
    }

}