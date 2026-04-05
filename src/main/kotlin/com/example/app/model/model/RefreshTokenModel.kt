package com.example.app.model.model

import com.example.app.config.JwtProperties
import com.example.app.db.RefreshToken
import com.example.app.model.dto.RefreshTokenDTO
import com.example.app.model.repo.RefreshTokenRepo
import com.example.app.model.service.DeviceInfoService
import com.example.app.model.service.Result
import com.example.app.utils.LogUtils
import org.springframework.stereotype.Component



@Component
class RefreshTokenModel(
        private val jwtProperties: JwtProperties,
        private val refreshTokenRepo: RefreshTokenRepo) {

    fun createRefreshToken(data: RefreshTokenDTO): Result {
        return try {
            val deviceInfo = DeviceInfoService().getDeviceInfo(data.userAgent).message
            val expiryDate = jwtProperties.refreshTokenExpiration.toMillis() + System.currentTimeMillis()
            refreshTokenRepo.getRefreshToken(email = data.email, deviceInfo = deviceInfo).firstOrNull().let {
                if (it == null) {
                    val new = RefreshToken(
                        tokenValue = data.tokenValue,
                        ipAddress = data.ipAddress,
                        expiryDate = expiryDate,
                        deviceInfo = deviceInfo,
                        email = data.email,
                        revoked = false
                    )
                    return Result("", 100, refreshTokenRepo.addRefreshToken(new))
                }

                else Result("", 100, updateRefreshToken(it.tokenValue, data.tokenValue))
            }
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            Result("error", 101)
        }
    }


    fun updateRefreshToken(requestToken: String?, tokenValue: String): Result {
        return try {
            val expiryDate = jwtProperties.refreshTokenExpiration.toMillis() + System.currentTimeMillis()
            refreshTokenRepo.getRefreshToken(requestToken = requestToken).firstOrNull().let {
                if (it != null) {
                    it.tokenValue = tokenValue
                    it.expiryDate = expiryDate
                    refreshTokenRepo.addRefreshToken(it)
                    return Result("", 100, it.email)
                }
                else {
                    LogUtils.logInfo("update-refresh-token-error")
                    Result("update-refresh-token-error", 101, null)
                }
            }
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            return Result("error", 101)
        }
    }


    fun deleteRefreshToken(email: String?, deviceInfo: String?, tokenValue: String?): Result {
        return try {
            refreshTokenRepo.getRefreshToken(email, deviceInfo, tokenValue).firstOrNull().let {
                if (it != null) {
                    return Result("", 100, refreshTokenRepo.deleteRefreshToken(it))
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