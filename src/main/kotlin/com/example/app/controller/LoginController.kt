package com.example.app.controller

import com.example.app.base.BaseController
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.PostMapping


class LoginController : BaseController() {

    @PostMapping(value = ["/login"])
    fun login(response: HttpServletResponse) {
        val cookie = Cookie("AUTH-TOKEN", "")

        cookie.isHttpOnly = true
        cookie.secure = true
        cookie.path = "/"
        cookie.maxAge = 86400

        response.addCookie(cookie)
    }
}