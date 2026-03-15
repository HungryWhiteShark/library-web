package com.example.app.model.model

import com.example.app.config.JwtProperties
import com.example.app.config.JwtUtil
import com.example.app.db.RefreshToken
import com.example.app.model.dto.RefreshTokenDTO
import com.example.app.model.repo.RefreshTokenRepo
import com.example.app.model.service.CustomUserDetailService
import com.example.app.model.service.DatabaseService
import com.example.app.model.service.Result
import com.example.app.utils.LogUtils
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component


@Component
class RefreshTokenModel(private val jdbc: NamedParameterJdbcTemplate, private val db: DatabaseService
                            , private val jwtUtil: JwtUtil, private val userDetailService: CustomUserDetailService
                            , private val jwtProperties: JwtProperties) {

    fun createRefreshToken(data: RefreshTokenDTO): Result {
        return try {
            RefreshTokenRepo(jdbc, db).getRefreshToken(data.email, data.deviceInfo).firstOrNull().let {
                if (it == null) {
                    val new = RefreshToken(
                        tokenValue = data.refreshToken,
                        ipAddress = data.ipAddress,
                        expiryDate = data.expiryDate,
                        deviceInfo = data.deviceInfo,
                        email = data.email,
                        revoked = false
                    )
                    return Result("", 100, RefreshTokenRepo(jdbc, db).addRefreshToken(new))
                }
            }
            LogUtils.logInfo("error")
            Result("error", 101)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            Result("error", 101)
        }
    }



    fun updateRefreshToken(tokenValue: String?): Result {
        return try {
            RefreshTokenRepo(jdbc, db).getRefreshToken(tokenValue = tokenValue).firstOrNull().let {
                if (it != null) {
                    val user = userDetailService.loadUserByUsername(it.email)
                    it.tokenValue = jwtUtil.generateRefreshToken(user)
                    it.expiryDate = jwtProperties.refreshTokenExpiration.toMillis() + System.currentTimeMillis()
                    return Result("", 100, RefreshTokenRepo(jdbc, db).addRefreshToken(it))
                }
            }
            LogUtils.logInfo("error")
            Result("error", 101)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            return Result("error", 101)
        }
    }


    fun deleteRefreshToken(tokenValue: String?): Result {
        return try {
            RefreshTokenRepo(jdbc, db).getRefreshToken(tokenValue = tokenValue).firstOrNull().let {
                if (it != null) {
                    return Result("", 100, RefreshTokenRepo(jdbc, db).deleteRefreshToken(it))
                }
            }
            LogUtils.logInfo("refresh token not found")
            Result("refresh token not found", 101)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            Result("error", 101)
        }
    }

}