package com.example.app.model.repo

import com.example.app.base.BaseRepository
import com.example.app.db.refreshtoken.RefreshToken
import com.example.app.model.service.DatabaseService



class RefreshTokenRepo(base: Any? = null): BaseRepository(base) {
    val db = autoWired(DatabaseService::class.java)

    fun getRefreshToken(email: String, deviceInfo: String): RefreshToken? {
        val param = ArrayList<Any>()
        val sql = buildString {
            append(" select * from ${RefreshToken.TABLE} ")

        }

        return db.find(sql, RefreshToken::class.java, param)

    }

    fun addRefreshToken(data: RefreshToken): RefreshToken? = db.save(data)

}