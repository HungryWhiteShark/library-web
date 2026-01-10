package com.example.app.db.user

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "user_info")
class UserInfo {
    @Id
    var citizenId = ""

    var userName = ""
    var password = ""
    var fullName = ""
    var email = ""
    var age = 0
    var gender = true

    var dateCreated = ""
    var dateUpdated = ""
    var role = 0
    var phoneNumber = ""
    var address = ""
}
