package com.example.app.db

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(
    name = UserInfo.TABLE,
    indexes = [
        Index(name = "idx_${UserInfo.TABLE}_userId", columnList = "userId")
    ]
)
data class UserInfo (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var userId: Long = 0L,

    @Column(columnDefinition = "varchar(255)", nullable = false)
    var citizenId: String = "",

    @Column(columnDefinition = "varchar(255)", nullable = false)
    var password: String = "",

    @Column(columnDefinition = "nvarchar(64)", nullable = false)
    var fullName: String = "",

    @Column(columnDefinition = "varchar(255)", nullable = false)
    var email: String = "",

    @Column(columnDefinition = "int")
    var age: Int = 0,

    @Column(columnDefinition = "boolean", nullable = false)
    var gender: Boolean = true,

    @Column(columnDefinition = "varchar(20)", nullable = true)
    var phoneNumber: String? = "",

    @Column(columnDefinition = "int")
    var role: Int = 0,

    @Column(columnDefinition = "timestamp", nullable = true)
    var dateCreated: LocalDateTime = LocalDateTime.now(),

    @Column(columnDefinition = "timestamp")
    var dateUpdated: LocalDateTime = LocalDateTime.now()

): Serializable {
    companion object {
        const val TABLE = "user_info"
    }
}