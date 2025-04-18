package com.app.solutionchallenge.common.config

import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JasyptConfig(
    @Value("\${jasypt.encryptor.password}")
    private val JASYPT_ENCRYPTOR_PASSWORD: String
) {
    /**
     * Jasypt StringEncryptor Bean
     * @return StringEncryptor
     */
    @Bean("jasyptStringEncryptor")
    open fun stringEncryptor(): StringEncryptor {
        val encryptor = PooledPBEStringEncryptor()
        val config = SimpleStringPBEConfig()

        config.password = JASYPT_ENCRYPTOR_PASSWORD
        config.algorithm = "PBEWithMD5AndDES"
        config.setKeyObtentionIterations("1000")
        config.setPoolSize("1")
        config.providerName = "SunJCE"
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator")
        config.stringOutputType = "base64"

        encryptor.setConfig(config)
        return encryptor
    }
}
