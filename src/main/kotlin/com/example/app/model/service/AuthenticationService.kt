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
import org.springframework.http.ResponseCookie



@Service
class AuthenticationService(
    private val authManager: AuthenticationManager
    , private val userDetailService: CustomUserDetailService
    , private val jwtUtil: JwtUtil
    , private val refreshTokenModel: RefreshTokenModel) {

    fun authentication(authRequest: AuthenticationRequest, userAgent: String,
                       ipAddress: String): AuthenticationResponse {

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



    fun deleteRefreshToken(email: String?, deviceInfo: String?, refreshToken: String?): String {
        try {
            val returnMessage = refreshTokenModel.deleteRefreshToken(email, deviceInfo, refreshToken).message
            returnMessage.let {
                if (it.isEmpty()) {
                    LogUtils.logInfo("refresh token deleted successfully")
                    return ""
                }
            }
            LogUtils.logError(returnMessage)
            return returnMessage

        }
        catch (e: Exception) {
            LogUtils.logError(e.localizedMessage)
            return e.localizedMessage
        }
    }



    fun createCookie(refreshToken: String?, maxAge: Long?): ResponseCookie {
        val cookie = ResponseCookie.from("refresh_token", refreshToken ?: "")
            .httpOnly(true) // prevent JS access
            .secure(true)
            .path("/")
            .maxAge(maxAge ?: (15 * 24 * 60 * 6)) // 15 days
            .sameSite("None")
            .build()
        return cookie
    }


    fun extractUserEmail(username: String): String? {
        return jwtUtil.extractEmail(username)
    }

}
