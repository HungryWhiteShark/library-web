package com.example.app.config

import org.springframework.stereotype.Component
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.ExpiredJwtException
import java.security.Key
import java.util.Date


@Component
class JwtUtil {
    private lateinit var key: Key

    fun getAllClaimsFromToken(token: String?): Claims? {
        return try {
            return Jwts.parser().setSigningKey(key).parseClaimsJws(token).body
        }
        catch (exp: ExpiredJwtException) {
            exp.claims
        }
        catch (e: Exception) {
            null
        }

    }


    fun isTokenExpired(token: String): Boolean {
        return try {
            getAllClaimsFromToken(token)?.let {
                return it.expiration.before(Date())
            }
            return true
        }
        catch (exp: ExpiredJwtException) {true}
        catch (e: Exception) {true}

    }


    class TokenBody {
        var userId = ""
        var name = ""
        var refreshExp = 0L
    }

}
