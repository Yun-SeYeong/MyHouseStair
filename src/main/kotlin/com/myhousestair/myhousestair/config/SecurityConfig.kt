package com.myhousestair.myhousestair.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .cors {
                it.configurationSource(
                    UrlBasedCorsConfigurationSource().also { configureSource ->
                        configureSource.registerCorsConfiguration("/**",
                            CorsConfiguration().apply {
                                allowedOrigins = listOf(
                                    "ws://localhost:8080",
                                    "ws://140.238.15.22:9000",
                                    "http://localhost:8080",
                                    "http://localhost:8081",
                                    "http://140.238.15.22:9000",
                                    "http://10.0.2.2:8080"
                                )
                                allowedMethods = listOf("POST", "GET", "DELETE", "PUT")
                                allowedHeaders = listOf("*")
                            })
                    }
                )
            }
            .headers { configure -> configure.frameOptions { it.sameOrigin() } }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    HttpMethod.POST,
                    "/v1/auth/**"
                ).permitAll()
                it.requestMatchers(
                    "/swagger-ui/**"
                ).permitAll()
                it.requestMatchers(
                    "/api-docs/**"
                ).permitAll()
                it.requestMatchers(
                    "/h2-console/**"
                ).permitAll()
                it.anyRequest().authenticated()
            }
            .addFilterBefore(
                JwtFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}