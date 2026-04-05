package com.example.app.controller

import com.example.app.base.BaseController
import com.example.app.model.dto.AuthenticationRequest
import com.example.app.model.dto.LoginResponseDTO
import com.example.app.model.dto.UserInfoRequest
import com.example.app.model.model.UserModel
import com.example.app.model.service.AuthenticationService
import com.example.app.model.service.DeviceInfoService
import com.example.app.utils.LogUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*



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
            val cookie = authService.createCookie(authResponse.refreshToken!!, null)
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())

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
    fun refreshToken(@CookieValue(name = "refresh_token") requestToken: String?,
                     response: HttpServletResponse): ResponseEntity<Any> {

        return try {
            val result = authService.refreshToken(requestToken)
            if (result.accessToken != null) {
                val cookie = authService.createCookie(result.refreshToken!!, null)
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())

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
    fun logout(
        @RequestHeader("Authorization") authHeader: String?,
        @CookieValue(name = "refresh_token", required = true) token: String?,
        req: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Any> {

        return try {
            if (authHeader == null && token == null)
                return responseData(null, 200, "Logged out successfully")

            var email: String? = null

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val accessToken = authHeader.substring(7)
                println(accessToken)
                email = authService.extractUserEmail(accessToken)
            }
            if (token != null) {
                val userAgent = req.getHeader("User-Agent") ?: "Unknown"
                val deviceInfo = DeviceInfoService().getDeviceInfo(userAgent).message

                val message = authService.deleteRefreshToken(email,deviceInfo, token)
                if (message.isNotEmpty()) response(101, message)
            }

            val cookie = authService.createCookie(null, 0)
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
            responseData(null, 200, "Logged out successfully")
        }
        catch (e: Exception) {
            e.printStackTrace()
            LogUtils.logError(e.message.toString())
            response(500, "system-error")
        }
    }

}

