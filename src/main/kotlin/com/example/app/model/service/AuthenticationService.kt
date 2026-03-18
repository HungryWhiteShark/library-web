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
import java.util.UUID


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
        val refreshToken = UUID.randomUUID().toString()
        val data = RefreshTokenDTO(
            email = authRequest.email,
            userAgent = userAgent,
            ipAddress = ipAddress,
            tokenValue = refreshToken
        )
        RefreshTokenModel(jdbc, db, jwtProperties).createRefreshToken(data)
        return AuthenticationResponse(accessToken, refreshToken)
    }


    fun refreshToken(requestToken: String?): AuthenticationResponse {
        val refreshToken = UUID.randomUUID().toString()
        val email = RefreshTokenModel(jdbc, db, jwtProperties)
            .updateRefreshToken(requestToken, refreshToken)
            .data.toString()

        val user = userDetailService.loadUserByUsername(email)
        val accessToken = jwtUtil.generateAccessToken(user)
        return AuthenticationResponse(accessToken, refreshToken)
    }

}
