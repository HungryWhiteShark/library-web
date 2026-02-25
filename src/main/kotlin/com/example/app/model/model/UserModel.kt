package com.example.app.model.model

import com.example.app.config.ApplicationConfig
import com.example.app.db.UserInfo
import com.example.app.model.dto.UserInfoDTO
import com.example.app.model.repo.UserInfoRepo
import com.example.app.model.service.Result
import com.example.app.utils.LogUtils



class UserModel(private val userRepo: UserInfoRepo) {

    fun registerUser(user: UserInfoDTO): Result {
        return try {
            userRepo.getUserInfo(user.email, user.citizenId).firstOrNull().let {
                if (it == null) {
                    val hashedPassword = ApplicationConfig().encoder().encode(user.password)
                    val new = UserInfo(
                        email = user.email,
                        phoneNumber = user.phoneNumber,
                        citizenId = user.citizenId,
                        password = hashedPassword!!,
                        fullName = user.fullName,
                        age = user.age,
                        gender = user.gender,
                        role = 2
                    )
                    return Result("", 100, userRepo.addUser(new))
                }
            }
            LogUtils.logInfo("user-already-exist")
            Result("user-already-exist", 101)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            Result(e.message.toString(), 101)
        }
    }


    fun getUserInfo(): Result {
        return try {
            Result("", 100)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            Result(e.message.toString(), 101)
        }
    }


    fun deleteUserInfo(id: Long): Result {
        return try {
            Result("", 100)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            Result(e.message.toString(), 101)
        }
    }


    fun updateUserInfo(userName: String, req: UserInfoDTO): Result {
        return try {
            Result("", 100)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            Result(e.message.toString(), 101)
        }
    }


    fun changeUserPassword(userName: String, oldPassword: String, newPassword: String): Result {
        return try {
            Result("", 100)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            Result(e.message.toString(), 101)
        }
    }

}
