package com.example.app.model.service

import com.example.app.config.JwtUtil
import com.example.app.model.dto.AuthenticationRequest
import com.example.app.model.dto.AuthenticationResponse
import com.example.app.model.dto.RefreshTokenDTO
import com.example.app.model.model.RefreshTokenModel
import com.example.app.utils.LogUtils
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import java.util.UUID
import jakarta.servlet.http.Cookie



@Service
class AuthenticationService(
    private val authManager: AuthenticationManager
    , private val userDetailService: CustomUserDetailService
    , private val jwtUtil: JwtUtil
    , private val refreshTokenModel: RefreshTokenModel) {

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
        refreshTokenModel.createRefreshToken(data)
        return AuthenticationResponse(accessToken, refreshToken, null)
    }


    fun refreshToken(requestToken: String?): AuthenticationResponse {
        val refreshToken = UUID.randomUUID().toString()
        refreshTokenModel.updateRefreshToken(requestToken, refreshToken)
            .data?.toString().let {
                if (it != null) {
                    val user = userDetailService.loadUserByUsername(it)
                    val accessToken = jwtUtil.generateAccessToken(user)
                    return AuthenticationResponse(accessToken, refreshToken, it)
                }
                else return AuthenticationResponse(null,null, null)
            }
    }


    fun deleteRefreshToken(refreshToken: String?) {
        refreshTokenModel.deleteRefreshToken(refreshToken).message.let {
            if (it.isNotEmpty()) {
                LogUtils.logInfo("refresh token deleted successfully")
            }
            else LogUtils.logError(it)
        }
    }


    fun createCookie(refreshToken: String?, maxAge: Int = 15 * 24 * 60 * 60): Cookie {
        val cookie = Cookie("refresh_token", refreshToken)

        cookie.isHttpOnly = true // prevent JS access
        cookie.secure = false
        cookie.path = "/auth/refresh"
        cookie.maxAge = maxAge  // 15 days

        return cookie
    }

}
