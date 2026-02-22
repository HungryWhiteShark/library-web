package com.example.app.model.model

import com.example.app.config.ApplicationConfig
import com.example.app.db.UserInfo
import com.example.app.model.dto.UserInfoDTO
import com.example.app.model.repo.UserInfoRepo
import com.example.app.model.service.AuthenticationService
import com.example.app.model.service.DatabaseService
import com.example.app.model.service.Result
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate



class UserModel(private val jdbc: NamedParameterJdbcTemplate, private val db: DatabaseService) {

    fun registerUser(user: UserInfoDTO, authService: AuthenticationService): Result {
        return try {
            UserInfoRepo(jdbc, db).getUserInfo(user.email, user.citizenId).firstOrNull().let {
                if (it == null) {
                    val hashedPassword = ApplicationConfig().encoder().encode(user.password)
                    val new = UserInfo(
                        email = user.email,
                        phoneNumber = user.phoneNumber,
                        citizenId = user.citizenId,
                        password = hashedPassword!!,
                        fullName = user.fullName,
                        age = user.age,
                        gender = user.gender,
                        role = 2
                    )

                    return Result("", 100, UserInfoRepo(jdbc, db).addUser(new))
                }
            }
            Result("user-already-exist", 101)
        }
        catch (e: Exception) {
            Result(e.message.toString(), 101)
        }
    }

}
