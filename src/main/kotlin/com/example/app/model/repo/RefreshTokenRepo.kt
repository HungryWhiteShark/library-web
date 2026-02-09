package com.example.app.model.repo

import com.example.app.base.BaseRepository
import com.example.app.db.RefreshToken
import com.example.app.model.service.DatabaseService
import java.time.Instant



class RefreshTokenRepo(base: Any? = null): BaseRepository(base) {
    val db = autoWired(DatabaseService::class.java)

    fun getRefreshToken(email: String? = null, deviceInfo: String? = null): RefreshToken? {
        val param = ArrayList<Any>()
        val sql = buildString {
            append(" select * from ${RefreshToken.TABLE} ")
            append(" where ${RefreshToken::expiryDate.name} < ${param.size} ")
            param.add(Instant.now().toEpochMilli())

            email?.let {
                append(" and ${RefreshToken::email.name} like '%' + ?${param.size} + '%' ")
                param.add(it)
            }
            deviceInfo?.let {
                append(" and ${RefreshToken::deviceInfo.name} like '%' + ?${param.size} + '%' ")
                param.add(it)
            }
        }

        return db.find(sql, RefreshToken::class.java, param)

    }

    fun addRefreshToken(data: RefreshToken): RefreshToken? = db.save(data)

}