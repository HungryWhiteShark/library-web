package com.example.app.db

import jakarta.persistence.*
import java.io.Serializable



@Entity
@Table(
    name = RefreshToken.TABLE,
    indexes = [
        Index(name = "idx_${RefreshToken.TABLE}_id", columnList = "tokenId")
    ]
)
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var tokenId: Long = 0L,

    @Column(columnDefinition = "varchar(255)", nullable = false)
    var email: String = "",

    @Column(columnDefinition = "varchar(255)", nullable = false)
    var tokenValue: String = "",

    @Column(columnDefinition = "bigint", nullable = false)
    var expiryDate: Long = 0L,

    @Column(columnDefinition = "boolean")
    var revoked: Boolean = false,

    @Column(columnDefinition = "varchar(255)", nullable = true)
    var deviceInfo: String? = ""

): Serializable {
    companion object {
        const val TABLE = "user_refresh_token"
    }
}