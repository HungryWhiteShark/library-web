package com.example.app.model.repo

import com.example.app.db.RefreshToken
import com.example.app.model.service.DatabaseService
import jakarta.transaction.Transactional
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.Instant



@Repository
@Transactional
class RefreshTokenRepo(private val jdbc: NamedParameterJdbcTemplate, private val db: DatabaseService) {

    fun getRefreshToken(email: String? = null, deviceInfo: String? = null, requestToken: String? = null): List<RefreshToken> {

        val params = MapSqlParameterSource()
        val sql = buildString {
            append("select * from ${RefreshToken.TABLE} where expiry_date >= :expiry ")
            params.addValue("expiry", System.currentTimeMillis() / 1000)
            email?.let {
                append(" and email like '%' || :email || '%' ")
                params.addValue("email", it)
            }
            deviceInfo?.let {
                append(" and device_info like '%' || :device || '%' ")
                params.addValue("device", it)
            }
            requestToken?.let {
                append(" and token_value = :token ")
                params.addValue("token", it)
            }
        }
        return jdbc.query(
            sql, params,
            DataClassRowMapper(RefreshToken::class.java)
        )
    }


    fun addRefreshToken(data: RefreshToken): RefreshToken? = db.save(data)

    fun deleteRefreshToken(data: RefreshToken): RefreshToken? = db.delete(data)
}