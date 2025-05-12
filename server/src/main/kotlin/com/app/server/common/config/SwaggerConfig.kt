package com.app.server.common.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI{
        return OpenAPI()
            .info(Info()
                .title("Greenap API")
                .version("v1.0.0")
                .description("API documentation for the Greenap Client.")
            )
    }
}