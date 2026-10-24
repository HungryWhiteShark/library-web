package com.example.app.model.dto

import java.time.LocalDateTime


data class UserInfoRequest(
    var email: String = "",
    var password: String = "",
    var citizenId: String? = null,
    var fullName: String = "",
    var birthdate: LocalDateTime? = null,
    var gender: Boolean = true,
    var phoneNumber: String? = null,
    var avatar: String? = null,
    var role: Int = 2
)


data class UserInfoResponse(
    var citizenId: String? = null,
    var fullName: String = "",
    var email: String = "",
    var password: String = "",
    var birthdate: LocalDateTime? = null,
    var gender: Boolean = true,
    var phoneNumber: String? = null,
    var role: Int = 0,
    var avatar: String? = null
)
