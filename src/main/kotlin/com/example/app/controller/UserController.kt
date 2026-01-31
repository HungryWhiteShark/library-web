package com.example.app.controller

import com.example.app.base.BaseController
import com.example.app.model.dto.UserInfoDTO
import com.example.app.model.user.UserModel
import com.example.app.utils.LogUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam



@RestController
@RequestMapping("/users")
class UserController: BaseController() {

    @PostMapping(value = ["/add_user"])
    fun addUser(@RequestParam info: UserInfoDTO): ResponseEntity<Any> {
        return try {
            val result = UserModel(this).addUserInfo(info)
            return if (result.success)
                responseData(result.data)
            else response(result.code, result.message)
        }
        catch (e: Exception) {
            autoWired(LogUtils::class.java).logError(e)
            response(500, "system-error")
        }
    }


    @GetMapping(value = ["/get_user"])
    fun getUserInfo(@RequestParam search: String, @RequestParam searchField: String): ResponseEntity<Any> {
        return try {
            val result = UserModel(this).getListUser(search, searchField)
            return if (result.success)
                responseData(result.data)
            else response(result.code, result.message)
        }
        catch (e: Exception) {
            autoWired(LogUtils::class.java).logError(e)
            response(500, "system-error")
        }
    }


    @PostMapping(value = ["/update_user/{id}"])
    fun updateUserInfo(@PathVariable id: Long, @RequestParam req: UserInfoDTO): ResponseEntity<Any> {
        return try {
            val result = UserModel(this).updateUserInfo(id, req)
            return if (result.success)
                responseData(result.data)
            else response(result.code, result.message)

        }
        catch (e: Exception) {
            autoWired(LogUtils::class.java).logError(e)
            response(500, "system-error")
        }
    }


    @DeleteMapping(value = ["/delete_user"])
    fun deleteUserInfo(@RequestParam id: Long): ResponseEntity<Any> {
        return try {
            val result = UserModel(this).deleteUserInfo(id)
            return if (result.success)
                responseData(result.data)
            else response(result.code, result.message)
        }
        catch (e: Exception) {
            autoWired(LogUtils::class.java).logError(e)
            response(500, "system-error")
        }
    }


    @PostMapping(value = ["/change_password"])
    fun changeUserPassword(@PathVariable id: Long, @RequestParam oldPassword: String,
                           @RequestParam newPassword: String): ResponseEntity<Any> {
        return try {
            val result = UserModel(this).updatePassword(id, oldPassword, newPassword)
            return if (result.success)
                responseData(result.data)
            else response(result.code, result.message)
        }
        catch (e: Exception) {
            autoWired(LogUtils::class.java).logError(e)
            response(500, "system-error")
        }
    }

}
