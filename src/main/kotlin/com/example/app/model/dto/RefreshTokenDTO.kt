package com.example.app.model.dto


data class RefreshTokenDTO(
    val email: String = "",
    val userAgent: String = "",
    val tokenValue: String = "",
    val ipAddress: String = ""
)
