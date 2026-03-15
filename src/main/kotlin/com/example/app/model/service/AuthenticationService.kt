package com.example.app.model.service

import com.example.app.config.JwtProperties
import com.example.app.config.JwtUtil
import com.example.app.model.dto.AuthenticationRequest
import com.example.app.model.dto.AuthenticationResponse
import com.example.app.model.dto.RefreshTokenDTO
import com.example.app.model.model.RefreshTokenModel
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service



@Service
class AuthenticationService(
    private val authManager: AuthenticationManager
    , private val userDetailService: CustomUserDetailService
    , private val jwtUtil: JwtUtil
    , private val jwtProperties: JwtProperties
    , private val jdbc: NamedParameterJdbcTemplate
    , private val db: DatabaseService) {

    fun authentication(authRequest: AuthenticationRequest,
                       userAgent: String, ipAddress: String): AuthenticationResponse {
        authManager.authenticate(UsernamePasswordAuthenticationToken(
            authRequest.email, authRequest.password
        ))

        val user = userDetailService.loadUserByUsername(authRequest.email)

        val accessToken = jwtUtil.generateAccessToken(user)
        val refreshToken = jwtUtil.generateRefreshToken(user)
        val deviceInfo = DeviceInfoService().getDeviceInfo(userAgent)
        val data = RefreshTokenDTO(
            refreshToken = refreshToken,
            deviceInfo = deviceInfo.message,
            email = authRequest.email,
            expiryDate = jwtProperties.refreshTokenExpiration.toMillis() + System.currentTimeMillis(),
            ipAddress = ipAddress
        )
        RefreshTokenModel(jdbc, db, jwtUtil, userDetailService, jwtProperties).createRefreshToken(data)

        return AuthenticationResponse(accessToken, refreshToken)
    }

}
