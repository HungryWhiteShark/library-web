package com.example.app.model.model

import com.example.app.config.JwtProperties
import com.example.app.db.RefreshToken
import com.example.app.model.dto.RefreshTokenDTO
import com.example.app.model.repo.RefreshTokenRepo
import com.example.app.model.service.DatabaseService
import com.example.app.model.service.DeviceInfoService
import com.example.app.model.service.Result
import com.example.app.utils.LogUtils
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component



@Component
class RefreshTokenModel(private val jdbc: NamedParameterJdbcTemplate, private val db: DatabaseService
                            , private val jwtProperties: JwtProperties) {

    fun createRefreshToken(data: RefreshTokenDTO): Result {
        return try {
            val deviceInfo = DeviceInfoService().getDeviceInfo(data.userAgent).message
            val expiryDate = jwtProperties.refreshTokenExpiration.toMillis() + System.currentTimeMillis()
            RefreshTokenRepo(jdbc, db).getRefreshToken(data.email, deviceInfo).firstOrNull().let {
                if (it == null) {
                    val new = RefreshToken(
                        tokenValue = data.tokenValue,
                        ipAddress = data.ipAddress,
                        expiryDate = expiryDate,
                        deviceInfo = deviceInfo,
                        email = data.email,
                        revoked = false
                    )
                    return Result("", 100, RefreshTokenRepo(jdbc, db).addRefreshToken(new))
                }
            }
            LogUtils.logInfo("create-refresh-token-error")
            Result("create-refresh-token-error", 101)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            Result("error", 101)
        }
    }


    fun updateRefreshToken(requestToken: String?, tokenValue: String): Result {
        return try {
            val expiryDate = jwtProperties.refreshTokenExpiration.toMillis() + System.currentTimeMillis()
            RefreshTokenRepo(jdbc, db).getRefreshToken(requestToken = requestToken).firstOrNull().let {
                if (it != null) {
                    it.tokenValue = tokenValue
                    it.expiryDate = expiryDate
                    RefreshTokenRepo(jdbc, db).addRefreshToken(it)
                    return Result("", 100, it.email)
                }
            }
            LogUtils.logInfo("update-refresh-token-error")
            Result("update-refresh-token-error", 101)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            return Result("error", 101)
        }
    }


//    fun deleteRefreshToken(tokenValue: String?): Result {
//        return try {
//            RefreshTokenRepo(jdbc, db).getRefreshToken(tokenValue = tokenValue).firstOrNull().let {
//                if (it != null) {
//                    return Result("", 100, RefreshTokenRepo(jdbc, db).deleteRefreshToken(it))
//                }
//            }
//            LogUtils.logInfo("refresh token not found")
//            Result("refresh token not found", 101)
//        }
//        catch (e: Exception) {
//            LogUtils.logError(e.message.toString(), e)
//            Result("error", 101)
//        }
//    }

}