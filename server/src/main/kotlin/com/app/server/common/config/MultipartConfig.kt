package com.app.server.common.config

import jakarta.servlet.MultipartConfigElement
import org.springframework.boot.web.servlet.MultipartConfigFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.unit.DataSize

@Configuration
class MultipartConfig {

    @Bean
    fun multipartConfigElement(): MultipartConfigElement {
        val factory = MultipartConfigFactory()
        // 최대 파일 크기
        factory.setMaxFileSize(DataSize.ofMegabytes(50))
        // 최대 요청 크기
        factory.setMaxRequestSize(DataSize.ofMegabytes(50))
        return factory.createMultipartConfig()
    }
}