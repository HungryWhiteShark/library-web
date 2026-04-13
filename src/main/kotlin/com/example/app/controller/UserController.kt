package com.example.app.controller

import com.example.app.base.BaseController
import com.example.app.model.dto.UserInfoRequest
import com.example.app.model.model.UserModel
import com.example.app.utils.LogUtils
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*



@RestController
@RequestMapping("/user")
class UserController(private val userModel: UserModel): BaseController() {

    @GetMapping(value = ["/user"])
    fun getUserInfo(@RequestParam email: String?, @RequestParam citizenId: String?, @RequestParam deleted: Boolean): ResponseEntity<Any> {
        return try {
            responseData(userModel.getUserInfo(email, citizenId, deleted))
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString())
            response(500, "system-error")
        }
    }


    @PostMapping(value = ["/profile"])
    fun updateUserInfo(@RequestParam id: Long, @RequestParam req: UserInfoRequest): ResponseEntity<Any> {
        return try {
            val result = userModel.updateUserInfo(id, req)
            return if (result.success) responseData(result.data)
            else response(result.code, result.message)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString())
            response(500, "system-error")
        }
    }


    @DeleteMapping(value = ["/delete"])
    fun deleteUserInfo(@RequestParam id: Long): ResponseEntity<Any> {
        return try {
            val result = userModel.deleteUserInfo(id)
            return if (result.success) responseData(result.data)
            else response(result.code, result.message)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString())
            response(500, "system-error")
        }
    }


    @PostMapping(value = ["/change-password"])
    fun changeUserPassword(@AuthenticationPrincipal user: UserDetails, @RequestParam oldPassword: String, @RequestParam newPassword: String): ResponseEntity<Any> {
        return try {
            val result = userModel.changeUserPassword(user.username, oldPassword, newPassword)
            return if (result.success) responseData(result.data)
            else response(result.code, result.message)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString())
            response(500, "system-error")
        }
    }

}
