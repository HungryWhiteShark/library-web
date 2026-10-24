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
        Index(name = "idx_${UserInfo.TABLE}_email", columnList = "email")
    ]
)
data class UserInfo (
    @Id
    @Column(columnDefinition = "text", nullable = false)
    var email: String = "",

    @Column(columnDefinition = "text", nullable = true)
    var citizenId: String? = null,

    @Column(columnDefinition = "text", nullable = false)
    var password: String = "",

    @Column(columnDefinition = "text", nullable = false)
    var fullName: String = "",

    @Column(updatable = true)
    var birthdate: LocalDateTime? = null,

    @Column(columnDefinition = "boolean", nullable = false)
    var gender: Boolean = true,

    @Column(columnDefinition = "text", nullable = true)
    var phoneNumber: String? = null,

    @Column(columnDefinition = "int")
    var role: Int = 2,

    @Column(columnDefinition = "text", nullable = true)
    var avatar: String? = null,

    @Column(name = "date_created", updatable = false)
    var dateCreated: LocalDateTime = LocalDateTime.now(),

    @Column(name = "date_updated", updatable = false)
    var dateUpdated: LocalDateTime = LocalDateTime.now(),

    @Column(columnDefinition = "timestamp", nullable = true)
    var deleted: LocalDateTime? = null

): Serializable {
    companion object {
        const val TABLE = "lib_user"
    }
}