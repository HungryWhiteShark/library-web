package com.example.app.model.repo

import com.example.app.base.BaseRepository
import com.example.app.db.user.UserInfo
import com.example.app.model.service.DatabaseService



class UserInfoRepo(base: Any? = null): BaseRepository(base) {
    val db = autoWired(DatabaseService::class.java)

    fun getUserInfo(search: String?, searchField : String?): List<UserInfo> {
        val param = ArrayList<Any>()
        val sql = buildString {
            append(" select * from ${tableName(UserInfo::class.java)} ")
            search?.let {
                append(" where $searchField = '$search' ")
                append(" union ")
                append(" select * from ${tableName(UserInfo::class.java)} ")
                append(" where $searchField like '%$search%' ")
            }
            append(" order by dateUpdated desc ")
        }

        return db.loadList(sql, UserInfo::class.java, param)
    }


    fun updateUserInfo(oldUserInfo: UserInfo, newUserInfo: UserInfo): UserInfo? {
        db.delete(oldUserInfo)
        return db.save(newUserInfo)
    }


    fun addUserInfo(info: UserInfo): UserInfo? = db.save(info)


    fun deleteUserInfo(info: UserInfo): UserInfo? = db.delete(info)

}
