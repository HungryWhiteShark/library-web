package com.example.app.model.model

import com.example.app.db.RefreshToken
import com.example.app.model.dto.RefreshTokenDTO
import com.example.app.model.repo.RefreshTokenRepo
import com.example.app.model.service.DatabaseService
import com.example.app.model.service.Result
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.time.Instant



class RefreshTokenModel(private val jdbc: NamedParameterJdbcTemplate, private val db: DatabaseService) {

    fun updateRefreshToken(data: RefreshTokenDTO): Result {
        try {
            val token = RefreshTokenRepo(jdbc, db).getRefreshToken(data.email, data.deviceInfo).firstOrNull()

            if (token != null) {
                token.tokenValue = data.refreshToken
                token.ipAddress = data.ipAddress
                token.expiryDate = data.expiryDate
                token.revoked = false

                return Result("", 100, RefreshTokenRepo(jdbc, db).addRefreshToken(token))
            }
            else {
                val new = RefreshToken(
                    tokenValue = data.refreshToken,
                    ipAddress = data.ipAddress,
                    expiryDate = Instant.now().toEpochMilli(),
                    deviceInfo = data.deviceInfo,
                    email = data.email,
                    revoked = false
                )
                return Result("", 100, RefreshTokenRepo(jdbc, db).addRefreshToken(new))
            }
        }
        catch (e: Exception) {
            return Result("error", 101)
        }
    }

}