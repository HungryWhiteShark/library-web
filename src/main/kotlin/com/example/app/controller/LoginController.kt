package com.example.app.controller

import com.example.app.base.BaseController
import com.example.app.model.dto.AuthenticationRequest
import com.example.app.model.service.AuthenticationService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



@RestController
@RequestMapping("/auth")
class LoginController(private val authService: AuthenticationService): BaseController() {

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
            cookie.path = "/api/auth/refresh"
            cookie.maxAge = 15 * 24 * 60 * 60 // 15 days

            response.addCookie(cookie)
            return response(100, "")

        }
        catch (e: Exception) {
            logUtils.logError(e.message.toString())
            response(102, "login-error")
        }
    }
}
