package com.example.app.model.model

import com.example.app.base.BaseModel
import com.example.app.db.UserInfo
import com.example.app.model.dto.UserInfoDTO
import com.example.app.model.repo.UserInfoRepo
import com.example.app.model.service.Result
import com.example.app.model.service.SecurityConfig



class UserModel(base: Any): BaseModel(base) {
    private val userInfoRepo = UserInfoRepo(this)

    fun getListUser(search: String, searchField: String): Result {
        return try {
            val res = userInfoRepo.getUserInfo(search, searchField)
            if (res.isNotEmpty()) {
                return Result("", 100, res)
            }
            logInfo("list-user-not-found")
            Result("list-user-not-found", 101)
        }
        catch (e: Exception) {
            logError(e)
            Result(e.message.toString(), 101)
        }
    }


    fun addUserInfo(req: UserInfoDTO): Result {
        try {
            return if (userInfoRepo.getUserInfo(req.citizenId, "citizenId").isNotEmpty())
                Result("citizenId-already-existed", 101)
            else if (userInfoRepo.getUserInfo(req.email, "email").isNotEmpty())
                Result("email-already-existed", 101)
            else {
                val hashedPassword = autoWired(SecurityConfig::class.java).encoder().encode(req.password)
                val new = UserInfo(
                    email = req.email,
                    password = hashedPassword!!,
                    citizenId = req.citizenId,
                    age = req.age,
                    fullName = req.fullName,
                    phoneNumber = req.phoneNumber,
                    gender = req.gender
                )
                Result("", 100, userInfoRepo.addUserInfo(new))
            }
        }
        catch (e: Exception) {
            logError(e)
            return Result(e.message.toString(), 101)
        }
    }


    fun deleteUserInfo(id: Long): Result {
        return try {
            userInfoRepo.getUserInfo(id, "userId").firstOrNull().let {
                if (it != null)
                    return Result("", 100, userInfoRepo.deleteUserInfo(it))
            }
            logInfo("user-info-not-found")
            Result("user-info-not-found", 101)
        }
        catch (e: Exception) {
            logError(e)
            Result(e.message.toString(), 101)
        }
    }


    fun updateUserInfo(id: Long, req: UserInfoDTO): Result {
        return try {
            userInfoRepo.getUserInfo(id, "userId").firstOrNull().let {
                if (it != null) {
                    it.email = req.email
                    it.citizenId = req.citizenId
                    it.age = req.age
                    it.fullName = req.fullName
                    it.phoneNumber = req.phoneNumber
                    it.gender = req.gender
                    return Result("", 100, userInfoRepo.addUserInfo(it))
                }
            }
            logInfo("user-info-not-found")
            Result("user-info-not-found", 101)
        }
        catch (e: Exception) {
            logError(e)
            Result(e.message.toString(), 101)
        }
    }


    fun updatePassword(id: Long, oldPassword: String, newPassword: String): Result {
        return try {
            userInfoRepo.getUserInfo(id, "userId").firstOrNull().let {
                if (it != null && it.password == oldPassword) {
                    it.password = newPassword
                    return Result("", 100, userInfoRepo.addUserInfo(it))
                }
            }
            logInfo("user-info-not-found")
            Result("user-info-not-found", 101)
        }
        catch (e: Exception) {
            logError(e)
            Result(e.message.toString(), 101)
        }
    }

}
