package com.example.app.config

import com.example.app.base.BaseService
import com.example.app.utils.LogUtils
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.Date



@ConfigurationProperties("jwt")
data class JwtProperties(
    val key: String,
    val accessTokenExpiration: Long = 1000 * 60 * 15,
    val refreshTokenExpiration: Long = 1000 * 60 * 60 * 24 * 15
)


@Service
class JwtUtil(): BaseService() {
    val jwtProperties = autoWired(JwtProperties::class.java)

    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.key.toByteArray())

    fun extractEmail(token: String): String? = getAllClaims(token).subject


    fun getAllClaims(token: String): Claims {
        return Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(token).payload
    }


    fun buildToken(extraClaims: Map<String, Any>, userDetail: UserDetails, expire: Long): String {
        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetail.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expire))
            .signWith( secretKey)
            .compact()
    }


    fun isTokenExpired(token: String): Boolean {
        return try {
            getAllClaims(token).let {
                return it.expiration.before(Date())
            }
        }
        catch (exp: ExpiredJwtException) {
            autoWired(LogUtils::class.java).logError(exp)
            true
        }
        catch (e: Exception) {
            autoWired(LogUtils::class.java).logError(e)
            true
        }

    }


    fun isTokenValid(token: String, userEmail: String): Boolean {
        val email = getAllClaims(token).subject
        return userEmail == email && !isTokenExpired(token)
    }


//    class TokenBody {
//        var userId = ""
//        var name = ""
//        var refreshExp = 0L
//    }

}
