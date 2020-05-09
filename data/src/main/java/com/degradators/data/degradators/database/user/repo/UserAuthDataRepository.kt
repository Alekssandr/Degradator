package com.degradators.data.degradators.database.user.repo

import com.degradators.data.degradators.database.user.api.UserAuthAPI
import com.degradators.degradators.model.User
import com.degradators.degradators.repo.UserAuthRepository
import com.google.gson.JsonObject
import io.reactivex.Completable

class UserAuthDataRepository(
    private val api: UserAuthAPI
) : UserAuthRepository {
    override fun insertNewUser(user: User): Completable  {
        val userNew = JsonObject()
        userNew.addProperty("login", user.mail)
        userNew.addProperty("password", user.password)
       return api.registerAndLogin(userNew)

    }
}

