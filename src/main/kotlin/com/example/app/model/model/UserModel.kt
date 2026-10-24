package com.example.app.model.model

import com.example.app.config.ApplicationConfig
import com.example.app.db.UserInfo
import com.example.app.model.dto.UserInfoRequest
import com.example.app.model.dto.UserInfoResponse
import com.example.app.model.repo.UserInfoRepo
import com.example.app.model.service.Result
import com.example.app.utils.LogUtils
import org.springframework.stereotype.Service



@Service
class UserModel(private val userRepo: UserInfoRepo) {

    fun UserInfoResponse.toUserInfo(): UserInfo {
        return UserInfo(
            fullName = this.fullName,
            email = this.email,
            gender = this.gender,
            citizenId = this.citizenId,
            birthdate = this.birthdate,
            phoneNumber = this.phoneNumber,
            role = this.role,
            password = this.password
        )
    }


    fun registerUser(user: UserInfoRequest): Result {
        return try {
            userRepo.getUserInfo(email = user.email, citizenId = user.citizenId).firstOrNull().let {
                if (it == null) {
                    val hashedPassword = ApplicationConfig().encoder().encode(user.password)
                    val new = UserInfo(
                        email = user.email,
                        phoneNumber = user.phoneNumber,
                        citizenId = user.citizenId,
                        password = hashedPassword!!,
                        fullName = user.fullName,
                        birthdate = user.birthdate,
                        gender = user.gender,
                        avatar = user.avatar,
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


    fun getUserInfo(email: String?, citizenId: String?, deleted: Boolean): List<UserInfoResponse> {
        return try {
            val res = userRepo.getUserInfo(email = email, citizenId = citizenId, deleted = deleted)
            return res.ifEmpty { emptyList() }
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            emptyList()
        }
    }


    fun deleteUserInfo(email: String): Result {
        return try {
            userRepo.getUserInfo(email = email).firstOrNull().let {
                if (it != null) {
                    return Result("", 100, userRepo.deleteUser(it.toUserInfo()))
                }
            }

            LogUtils.logInfo("user-info-not-found")
            Result("user-info-not-found", 101)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            Result(e.message.toString(), 101)
        }
    }


    fun updateUserInfo(req: UserInfoRequest): Result {
        return try {
            userRepo.getUserInfo(email = req.email).firstOrNull().let {
                if (it != null) {
                    it.citizenId = req.citizenId
                    it.gender = req.gender
                    it.birthdate = req.birthdate
                    it.fullName = req.fullName
                    it.phoneNumber = req.phoneNumber
                    it.avatar = req.avatar
                    it.role = req.role
                    it.password = req.password
                    return Result("", 100, userRepo.addUser(it.toUserInfo()))
                }
            }

            LogUtils.logInfo("user-info-not-found")
            Result("user-info-not-found", 101)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            Result(e.message.toString(), 101)
        }
    }


    fun changeUserPassword(email: String, oldPassword: String, newPassword: String): Result {
        return try {
            val hashedOldPassword = ApplicationConfig().encoder().encode(oldPassword)!!
            userRepo.getUserInfo(email = email, password = hashedOldPassword).firstOrNull().let {
                if (it != null) {
                    it.password = ApplicationConfig().encoder().encode(newPassword)!!
                    return Result("", 100, userRepo.addUser(it.toUserInfo()))
                }
            }

            LogUtils.logInfo("old-password-not-correct")
            Result("old-password-not-correct", 100)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            Result(e.message.toString(), 101)
        }
    }

}
