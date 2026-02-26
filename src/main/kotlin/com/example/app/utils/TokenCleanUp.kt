package com.example.app.utils

import com.example.app.db.RefreshToken
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component



@Component
class TokenCleanUp(private val jdbc: NamedParameterJdbcTemplate) {

    @Scheduled(fixedRate = 3600000) // runs every 60 mins
    fun deleteExpiryToken() {

        val params = hashMapOf<String, Any>()
        val sql = buildString {
            append(" delete from ${RefreshToken.TABLE} where expiry_date < :now ")
            params["now"] = System.currentTimeMillis()
        }

        val deleted = jdbc.update(sql, params)
        if (deleted > 0) {
            LogUtils.logInfo("Delete $deleted expired refresh tokens.")
        }
    }

}
