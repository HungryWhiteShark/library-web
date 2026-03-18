package com.example.app.model.service

import com.example.app.utils.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource



@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtAuthenticationFilter: JwtAuthenticationFilter) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfig()) }
            .csrf { it.disable() }
            .sessionManagement { session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/auth/**", "/error").permitAll()
                auth.anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }


    @Bean
    fun corsConfig(): CorsConfigurationSource {
        val config = CorsConfiguration()

        config.allowedOrigins = listOf("http://localhost:5173")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        config.allowedHeaders = listOf("Authorization", "Content-Type", "Cache-Control")
        config.allowCredentials = true
        config.exposedHeaders = listOf("Authorization", "Set-Cookie")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

}
