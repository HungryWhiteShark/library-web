package com.example.app.model.dto

data class UserInfoRequest(
    var citizenId: String = "",
    var password: String = "",
    var fullName: String = "",
    var email: String = "",
    var age: Int = 0,
    var gender: Boolean = true,
    var phoneNumber: String = ""
)


data class UserInfoResponse(
    var userId: Long = 0L,
    var citizenId: String = "",
    var fullName: String = "",
    var email: String = "",
    var password: String = "",
    var age: Int = 0,
    var gender: Boolean = true,
    var phoneNumber: String = "",
    var role: Int = 0,
    var avatar: String = ""
)
