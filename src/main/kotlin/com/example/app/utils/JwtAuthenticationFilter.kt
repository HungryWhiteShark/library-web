package com.example.app.utils

import com.example.app.config.JwtUtil
import com.example.app.model.service.CustomUserDetailService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder



@Component
class JwtAuthenticationFilter(
    val userDetailService: CustomUserDetailService, val jwtUtil: JwtUtil): OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest,
        response: HttpServletResponse, filterChain: FilterChain) {
        val authHeader: String? = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith(" Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)
        val userEmail = jwtUtil.extractEmail(jwt)

        if (userEmail != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetail = userDetailService
        }

        filterChain.doFilter(request, response)
    }
}