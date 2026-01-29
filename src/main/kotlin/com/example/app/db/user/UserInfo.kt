package com.example.app.db.user

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime



@Entity
@Table(name = "")
data class UserInfo (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var citizenId: String = "",

    @Column(columnDefinition = "varchar(255)", nullable = false)
    var password: String = "",

    @Column(columnDefinition = "nvarchar(64)", nullable = false)
    var fullName: String = "",

    @Column(columnDefinition = "varchar(255)", nullable = false)
    var email: String = "",

    @Column(columnDefinition = "int", nullable = true)
    var age: Int? = 0,

    @Column(columnDefinition = "boolean", nullable = false)
    var gender: Boolean = true,

    @Column(columnDefinition = "varchar(20)", nullable = true)
    var phoneNumber: String? = "",

    @Column(columnDefinition = "bit", nullable = false)
    var role: Int = 0,

    @Column(columnDefinition = "timestamp", nullable = true)
    var dateCreated: LocalDateTime = LocalDateTime.now(),

    @Column(columnDefinition = "timestamp")
    var dateUpdated: LocalDateTime = LocalDateTime.now()

): Serializable {
    companion object {
        val TABLE = "user_info"

    }
}
