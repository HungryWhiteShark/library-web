package com.example.app.model.user

import com.example.app.base.BaseController
import com.example.app.base.BaseModel
import com.example.app.db.user.UserInfo
import com.example.app.model.enum.Role
import com.example.app.model.repo.UserInfoRepo
import org.springframework.context.ApplicationContext



class UserModel(ctl: BaseController?): BaseModel(ctl) {

    constructor(context: ApplicationContext): this(null) {
        this.context = context
    }


    fun getListUser(search: String?, searchField: String?): List<UserInfo> =
        UserInfoRepo(this).getUserInfo(search, searchField)


    fun addUserInfo(info: UserInfo): String {
        if (getListUser(info.email, "email").isNotEmpty()) return "email"
        else if (getListUser(info.citizenId, "citizenId").isNotEmpty()) return "citizenId"
        else {
            UserInfoRepo(this).addUserInfo(info)
            return ""
        }
    }


    fun updateUserInfo(oldUserInfo: UserInfo, newUserInfo: UserInfo): String {
        if (getListUser(newUserInfo.email, "email").isNotEmpty()) return "email"
        else if (getListUser(newUserInfo.citizenId, "citizenId").isNotEmpty()) return "citizenId"
        else {
            UserInfoRepo(this).updateUserInfo(oldUserInfo, newUserInfo)
            return ""
        }

    }


    fun deleteUserInfo(info: UserInfo): UserInfo? {
        if (getListUser(info.citizenId, "citizenId").isNotEmpty() &&
            info.role == Role.USER.key)
            return UserInfoRepo(this).deleteUserInfo(info)
        return null
    }

}
