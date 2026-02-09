package com.example.app.config

import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service



@Service
class ApplicationConfig {
    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder(12)


    @Bean
    @Throws(Exception::class)
    fun authManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

}
