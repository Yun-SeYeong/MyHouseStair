package com.myhousestair.myhousestair.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.security.SecurityScheme.In
import io.swagger.v3.oas.models.security.SecurityScheme.Type
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders

@Configuration
@OpenAPIDefinition(servers = [Server(url = "/")])
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("우리집계단 API")
                .description("우리집계단 API 명세서")
                .version("v1")
        )
        .components(
            Components()
                .securitySchemes(
                    mapOf(
                        HttpHeaders.AUTHORIZATION to SecurityScheme()
                            .name(HttpHeaders.AUTHORIZATION)
                            .type(Type.APIKEY)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .`in`(In.HEADER)
                    )
                )
        )
}