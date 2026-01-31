package com.example.app.model.dto

data class UserInfoDTO(
    var citizenId: String = "",
    var password: String = "",
    var fullName: String = "",
    var email: String = "",
    var age: Int = 0,
    var gender: Boolean = true,
    var phoneNumber: String? = "",
    var role: Int = 0
)