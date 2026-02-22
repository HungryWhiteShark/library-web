package com.example.app.model.dto

data class RefreshTokenDTO(
    val refreshToken: String,
    val email: String,
    val expiryDate: Long,
    val deviceInfo: String? = null,
    val ipAddress: String? = null
)
