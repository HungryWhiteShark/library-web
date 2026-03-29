package com.example.app.model.repo

import com.example.app.db.UserInfo
import com.example.app.model.dto.UserInfoResponse
import com.example.app.model.service.DatabaseService
import jakarta.transaction.Transactional
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository



@Repository
@Transactional
class UserInfoRepo(private val jdbc: NamedParameterJdbcTemplate, private val db: DatabaseService) {
    fun getUserInfo(password: String? = null, userId: Long = 0L, email: String? = null,
                    citizenId: String? = null, deleted: Boolean = false): List<UserInfoResponse> {
        val params = hashMapOf<String, Any>()
        val sql = buildString {
            append(" select * from ${UserInfo.TABLE} where email ")
            email?.let {
                append(" = :email ")
                params["email"] = it
            } ?: run {
                append(" like '%' ")
            }

            deleted.let {
                append(" and deleted = :deleted ")
                params["deleted"] = it
            }
            if (userId > 0) {
                append(" and user_id = :id ")
                params["id"] = userId
            }
            citizenId?.let {
                append(" and citizen_id = :citizen ")
                params["citizen"] = it
            }
            password?.let {
                append(" and password = :password ")
                params["password"] = it
            }
        }

        return jdbc.query(
            sql, params,
            BeanPropertyRowMapper(UserInfoResponse::class.java)
        )
    }


    fun addUser(data: UserInfo): UserInfo? = db.save(data)


    fun deleteUser(data: UserInfo): UserInfo? {
        data.deleted = true
        return db.save(data)
    }

}
