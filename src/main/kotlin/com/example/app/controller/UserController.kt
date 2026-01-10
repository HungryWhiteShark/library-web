package com.example.app.controller

import com.example.app.base.BaseController
import com.example.app.db.user.UserInfo
import com.example.app.model.user.UserModel
import com.example.app.utils.LogUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping


@RestController
@RequestMapping("/users")
class UserController: BaseController() {

    @PostMapping("/add")
    fun addUser(info: UserInfo): ResponseEntity<*> {
        return try {
            val result = UserModel(this).addUserInfo(info)
                    responseData(data = null, message = result)
        }
        catch (e: Exception) {
            autoWired(LogUtils::class.java).logError(e)
            response(500, "system-error")
        }

    }


    @GetMapping("/get")
    fun getUserInfo(search: String? = null, searchField: String? = null): ResponseEntity<*> {
        return try {
            responseData(UserModel(this).getListUser(search, searchField))
        }
        catch (e: Exception) {
            autoWired(LogUtils::class.java).logError(e)
            response(500, "system-error")
        }

    }


    @PostMapping("/update")
    fun updateUserInfo(oldUserInfo: UserInfo, newUserInfo: UserInfo): ResponseEntity<*> {
        return try {
            responseData(UserModel(this).updateUserInfo(oldUserInfo, newUserInfo))
        }
        catch (e: Exception) {
            autoWired(LogUtils::class.java).logError(e)
            response(500, "system-error")
        }
    }


    @DeleteMapping("/delete")
    fun deleteUserInfo(info: UserInfo): ResponseEntity<*> {
        return try {
            responseData(UserModel(this).deleteUserInfo(info))
        }
        catch (e: Exception) {
            autoWired(LogUtils::class.java).logError(e)
            response(500, "system-error")
        }

    }

}
