package com.example.app.model.repo

import com.example.app.base.BaseRepository
import com.example.app.db.UserInfo
import com.example.app.model.service.DatabaseService



class UserInfoRepo(base: Any? = null): BaseRepository(base) {
    val db = autoWired(DatabaseService::class.java)

    fun getUserInfo(search: Any? = "", searchField : String = "", offset: Int = 0, limit: Int = 5): List<UserInfo> {
        val param = ArrayList<Any>()
        val sql = buildString {
            append(" select * from ${UserInfo.TABLE} ")
            search?.let {
                append(" where ?${param.size} = ")
                param.add(searchField)
                append(" ?${param.size} ")
                param.add(search)

                append(" union ")
                append(" select * from ${UserInfo.TABLE} ")
                append(" where ?${param.size} like ")
                param.add(searchField)
                append(" N'%' + ?${param.size} + '%' ")
                param.add(search)
            }
            append(" order by dateUpdated desc ")
            append(" offset $offset rows fetch next $limit rows only ")
        }
        return db.loadList(sql, UserInfo::class.java, param)
    }


    fun addUserInfo(info: UserInfo): UserInfo? = db.save(info)


    fun deleteUserInfo(info: UserInfo): UserInfo? = db.delete(info)

}
