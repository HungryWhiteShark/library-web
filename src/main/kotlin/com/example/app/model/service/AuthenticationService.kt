package com.example.app.model.service

import com.example.app.config.JwtProperties
import com.example.app.config.JwtUtil
import com.example.app.model.dto.AuthenticationRequest
import com.example.app.model.dto.AuthenticationResponse
import com.example.app.model.model.RefreshTokenModel
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service



@Service
class AuthenticationService(
    private val authManager: AuthenticationManager
    , private val userDetailService: CustomUserDetailService
    , private val jwtUtil: JwtUtil
    , private val jwtProperties: JwtProperties) {

    fun generateAccessToken(userDetail: UserDetails): String {
        return jwtUtil.buildToken(HashMap(), userDetail, jwtProperties.accessTokenExpiration.toMillis())
    }


    fun generateRefreshToken(userDetail: UserDetails): String {
        return jwtUtil.buildToken(HashMap(), userDetail, jwtProperties.refreshTokenExpiration.toMillis())
    }


    fun authentication(authRequest: AuthenticationRequest,
                       userAgent: String, ipAddress: String): AuthenticationResponse {
        authManager.authenticate(UsernamePasswordAuthenticationToken(
            authRequest.email, authRequest.password
        ))

        val user = userDetailService.loadUserByUsername(authRequest.email)
        val accessToken = generateAccessToken(user)
        val refreshToken = generateRefreshToken(user)
        val deviceInfo = DeviceInfoService().getDeviceInfo(userAgent)

        RefreshTokenModel(this).addRefreshToken(authRequest.email, deviceInfo.message, ipAddress)

        return AuthenticationResponse(accessToken, refreshToken)
    }

}
