package com.example.app.db

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
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

    @Column(columnDefinition = "text", nullable = false)
    var citizenId: String = "",

    @Column(columnDefinition = "text", nullable = false)
    var password: String = "",

    @Column(columnDefinition = "text", nullable = false)
    var fullName: String = "",

    @Column(columnDefinition = "text", nullable = false)
    var email: String = "",

    @Column(columnDefinition = "int")
    var age: Int = 0,

    @Column(columnDefinition = "boolean", nullable = false)
    var gender: Boolean = true,

    @Column(columnDefinition = "text", nullable = true)
    var phoneNumber: String? = "",

    @Column(columnDefinition = "int")
    var role: Int = 2,

    @CreationTimestamp
    var dateCreated: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    var dateUpdated: LocalDateTime = LocalDateTime.now(),

    var deleted: Boolean = false

): Serializable {
    companion object {
        const val TABLE = "user_info"
    }
}