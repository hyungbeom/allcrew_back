package com.nodex.allcrew.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {

    @Value("\${app.cors.allowed-origin-patterns:http://localhost:*,http://127.0.0.1:*,http://211.37.179.144:*}")
    private lateinit var corsAllowedOriginPatterns: String

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun corsFilter(): CorsFilter {
        val config = CorsConfiguration().apply {
            allowedOriginPatterns =
                corsAllowedOriginPatterns.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("Authorization", "Content-Type")
            allowCredentials = true
            maxAge = 3600L
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/api/**", config)
        }

        return CorsFilter(source)
    }
}
