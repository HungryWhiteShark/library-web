package com.example.app.controller

import com.example.app.base.BaseController
import com.example.app.model.dto.AuthenticationRequest
import com.example.app.model.dto.LoginResponseDTO
import com.example.app.model.dto.UserInfoRequest
import com.example.app.model.model.UserModel
import com.example.app.model.service.AuthenticationService
import com.example.app.utils.LogUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



@RestController
@RequestMapping("/auth")
class LoginController(
    private val authService: AuthenticationService, private val userModel: UserModel): BaseController() {

    @PostMapping(value = ["/login"])
    fun login(@RequestBody authRequest: AuthenticationRequest,
              req: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Any> {
        return try {
            val ipAddress = req.remoteAddr
            val userAgent = req.getHeader("User-Agent") ?: "Unknown"
            val authResponse = authService.authentication(authRequest, userAgent, ipAddress)
            val cookie = authService.createCookie(authResponse.refreshToken!!)
            response.addCookie(cookie)
            val user = hashMapOf<String, Any>()

            userModel.getUserInfo(email = authRequest.email, null, false).first().let {
                user["id"] = it.userId
                user["fullName"] = it.fullName
                user["avatar"] = it.avatar
                user["role"] = it.role
            }
            user["email"] = authRequest.email

            return responseData(LoginResponseDTO(authResponse.accessToken, user), 100, "")
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString())
            response(500, "system-error")
        }
    }


    @PostMapping(value = ["/register"])
    fun register(@RequestBody user: UserInfoRequest, req: HttpServletRequest): ResponseEntity<Any> {
        return try {
            req.getHeader("User-Agent") ?: "Unknown"

            val result = userModel.registerUser(user)
            return if (result.success) responseData(result.data)
                else response(result.code, result.message)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString())
            response(500, "system-error")
        }
    }


    @PostMapping(value = ["/refresh"])
    fun refreshToken(
        @CookieValue(name = "refresh_token") requestToken: String?,
        response: HttpServletResponse): ResponseEntity<Any> {

        return try {
            val result = authService.refreshToken(requestToken)
            if (result.accessToken != null) {
                val cookie = authService.createCookie(result.refreshToken!!)
                response.addCookie(cookie)
                val user = hashMapOf<String, Any>()

                userModel.getUserInfo(result.email, null, false).first().let {
                    user["id"] = it.userId
                    user["fullName"] = it.fullName
                    user["avatar"] = it.avatar
                    user["role"] = it.role
                }
                user["email"] = result.email.toString()

                return responseData(LoginResponseDTO(result.accessToken, user), 100, "")
            }
            else response(401, "Unauthorized")
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString())
            response(500, "system-error")
        }
    }



    @PostMapping(value = ["/logout"])
    fun logout(@CookieValue(name = "refresh_token", required = true) token: String?,
        response: HttpServletResponse): ResponseEntity<Any> {
        return try {
            println(token)
            if (token != null) authService.deleteRefreshToken(token)

            val cookie = authService.createCookie(null, 0)
            response.addCookie(cookie)

            responseData(null, 200, "Logged out successfully")
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString())
            response(500, "system-error")
        }
    }

}

