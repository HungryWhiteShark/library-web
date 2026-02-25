package com.example.app.model.repo

import com.example.app.db.UserInfo
import com.example.app.model.service.DatabaseService
import jakarta.transaction.Transactional
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository



@Repository
@Transactional
class UserInfoRepo(private val jdbc: NamedParameterJdbcTemplate, private val db: DatabaseService) {
    fun getUserInfo(email: String, citizenId: String? = null): List<UserInfo> {
        val params = hashMapOf<String, Any>()
        val sql = buildString {
            append(" select * from ${UserInfo.TABLE} where email = :email ")
            params["email"] = email

            citizenId?.let {
                append(" and citizen_id = :citizen ")
                params["citizen"] = citizenId
            }
        }

        return jdbc.query(
            sql, params,
            BeanPropertyRowMapper(UserInfo::class.java)
        )
    }


    fun addUser(data: UserInfo): UserInfo? = db.save(data)

}
