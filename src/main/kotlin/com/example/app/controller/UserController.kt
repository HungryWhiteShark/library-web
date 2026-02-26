package com.example.app.controller

import com.example.app.base.BaseController
import com.example.app.model.dto.AuthenticationRequest
import com.example.app.model.dto.UserInfoDTO
import com.example.app.model.model.UserModel
import com.example.app.utils.LogUtils
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam



@RestController
@RequestMapping("/user")
class UserController(private val userModel: UserModel): BaseController() {

    @GetMapping(value = ["/user"])
    fun getUserInfo(@RequestBody authRequest: AuthenticationRequest,
                    @RequestParam citizenId: String?, @RequestParam deleted: Boolean?): ResponseEntity<Any> {
        return try {
            val result = userModel.getUserInfo(authRequest.email, citizenId, deleted)
            return if (result.success) responseData(result.data)
            else response(result.code, result.message)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString())
            response(500, "system-error")
        }
    }


    @PostMapping(value = ["/user/profile"])
    fun updateUserInfo(@AuthenticationPrincipal user: UserDetails, @RequestParam req: UserInfoDTO): ResponseEntity<Any> {
        return try {
            val result = userModel.updateUserInfo(user.username, req)
            return if (result.success) responseData(result.data)
            else response(result.code, result.message)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString())
            response(500, "system-error")
        }
    }


    @DeleteMapping(value = ["/user/{id}"])
    fun deleteUserInfo(@PathVariable id: Long): ResponseEntity<Any> {
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
