package com.example.app.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration


@ConfigurationProperties("jwt")
data class JwtProperties(
    val key: String = "",
    val accessTokenExpiration: Duration,
    val refreshTokenExpiration: Duration
)
