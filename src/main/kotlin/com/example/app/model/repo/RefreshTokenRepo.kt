package com.example.app.model.repo

import com.example.app.db.RefreshToken
import com.example.app.model.service.DatabaseService
import jakarta.transaction.Transactional
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.Instant



@Repository
@Transactional
class RefreshTokenRepo(private val jdbc: NamedParameterJdbcTemplate, private val db: DatabaseService) {

    fun getRefreshToken(email: String? = null, deviceInfo: String? = null, requestToken: String? = null): List<RefreshToken> {
        val params = hashMapOf<String, Any>()
        val sql = buildString {
            append("select * from ${RefreshToken.TABLE} where expiry_date >= :expiry ")
            params["expiry"] = Instant.now().toEpochMilli()
            email?.let {
                append(" and email like '%:email%' ")
                params["email"] = email
            }
            deviceInfo?.let {
                append(" and device_info like '%:device%' ")
                params["device"] = deviceInfo
            }
            requestToken?.let {
                append(" and token_value = :token ")
                params["token"] = requestToken
            }
        }

        return jdbc.query(
            sql, params,
            BeanPropertyRowMapper(RefreshToken::class.java)
        )
    }


    fun addRefreshToken(data: RefreshToken): RefreshToken? = db.save(data)

    fun deleteRefreshToken(data: RefreshToken): RefreshToken? = db.delete(data)
}