package com.example.app.config

import com.example.app.base.BaseService
import com.example.app.utils.LogUtils
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date



@Service
@EnableConfigurationProperties(JwtProperties::class)
class JwtUtil(private val jwtProperties: JwtProperties): BaseService() {
    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.key.toByteArray())

    fun extractEmail(token: String): String? = getAllClaims(token).subject


    fun getAllClaims(token: String): Claims {
        return Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(token).payload
    }


    fun buildToken(extraClaims: HashMap<String, Any>, userDetail: UserDetails, expire: Long): String {
        val roles = userDetail.authorities.map { it.authority }
        println(roles)
        extraClaims["roles"] = roles

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
            LogUtils.logError(exp.message.toString())
            true
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString())
            true
        }
    }


    fun isTokenValid(token: String, userEmail: String): Boolean {
        val email = getAllClaims(token).subject
        return userEmail == email && !isTokenExpired(token)
    }

    fun generateAccessToken(userDetail: UserDetails): String {
        return buildToken(hashMapOf(), userDetail, jwtProperties.accessTokenExpiration.toMillis())
    }


    fun generateRefreshToken(userDetail: UserDetails): String {
        return buildToken(hashMapOf(), userDetail, jwtProperties.refreshTokenExpiration.toMillis())
    }

}
