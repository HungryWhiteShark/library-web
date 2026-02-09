package com.example.app.model.model

import com.example.app.base.BaseModel
import com.example.app.model.repo.RefreshTokenRepo
import com.example.app.model.service.Result



class RefreshTokenModel(base: Any): BaseModel(base) {
    private val refreshTokenRepo = RefreshTokenRepo(this)


    fun addRefreshToken(email: String, deviceInfo: String, ipAddress: String): Result {
        return try {
            refreshTokenRepo.getRefreshToken(email, deviceInfo).let {
                if (it != null) {
                    return Result("", 100)
                }
            }
            Result("", 100)
        }
        catch (e: Exception) {
            logError(e)
            Result(e.message.toString(), 101)
        }
    }
}