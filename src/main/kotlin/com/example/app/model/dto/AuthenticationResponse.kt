package com.example.app.model.dto


data class AuthenticationResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val email: String?,
)
