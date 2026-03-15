package com.example.app.model.dto

data class RefreshTokenDTO(
    val refreshToken: String = "",
    val email: String = "",
    val expiryDate: Long = 0L,
    val deviceInfo: String? = null,
    val ipAddress: String? = null
)



data class RefreshTokenRequest(
    val email: String = "",
    val deviceInfo: String? = null,
    val ipAddress: String? = null
)
