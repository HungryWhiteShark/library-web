package com.example.app.model.service

import com.example.app.db.UserInfo
import com.example.app.model.repo.UserInfoRepo
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service



@Service
class CustomUserDetailService(private val jdbc: NamedParameterJdbcTemplate, private val db: DatabaseService): UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails =
        UserInfoRepo(jdbc, db).getUserInfo(email).firstOrNull().let {
            it?.mapToUserDetail() ?: throw UsernameNotFoundException("Not found")
        }


    private fun UserInfo.mapToUserDetail(): UserDetails =
        User.builder().username(this.email)
            .password(this.password)
            .roles(this.role.toString())
            .build()

}