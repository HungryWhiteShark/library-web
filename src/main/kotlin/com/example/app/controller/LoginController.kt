package com.example.app.controller

import com.example.app.base.BaseController
import com.example.app.model.dto.AuthenticationRequest
import com.example.app.model.dto.UserInfoDTO
import com.example.app.model.model.UserModel
import com.example.app.model.service.AuthenticationService
import com.example.app.model.service.DatabaseService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



@RestController
@RequestMapping("/auth")
class LoginController(
    private val authService: AuthenticationService,
    private val jdbc: NamedParameterJdbcTemplate,
    private val db: DatabaseService): BaseController() {

    @PostMapping(value = ["/login"])
    fun login(@RequestBody authRequest: AuthenticationRequest,
              req: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Any> {
        return try {
            val ipAddress = req.remoteAddr
            val userAgent = req.getHeader("User-Agent") ?: "Unknown"
            val authResponse = authService.authentication(authRequest, userAgent, ipAddress)

            val cookie = Cookie("refresh_token", authResponse.refreshToken)

            cookie.isHttpOnly = true // prevent JS access
            cookie.secure = true     // only sends over https
            cookie.path = "/auth/refresh"
            cookie.maxAge = 15 * 24 * 60 * 60 // 15 days

            response.addCookie(cookie)
            return response(100, "")

        }
        catch (e: Exception) {
            logUtil.logError(e.message.toString())
            response(102, "login-error")
        }
    }


    @PostMapping(value = ["/register"])
    fun register(@RequestBody user: UserInfoDTO, req: HttpServletRequest): ResponseEntity<Any> {
        return try {
            req.getHeader("User-Agent") ?: "Unknown"

            val result = UserModel(jdbc, db).registerUser(user, authService)
            return if (result.success) responseData(result.data)
                else response(result.code, result.message)
        }
        catch (e: Exception) {
            logUtil.logError(e.message.toString())
            response(500, "system-error")
        }
    }
}
