package com.degradators.data.degradators.database.user.repo

import android.util.Base64
import com.degradators.data.degradators.database.user.api.UserAuthAPI
import com.degradators.degradators.model.user.User
import com.degradators.degradators.repo.UserAuthRepository
import com.google.gson.JsonObject
import io.reactivex.Completable
import io.reactivex.Single

class UserAuthDataRepository(
    private val api: UserAuthAPI
) : UserAuthRepository {

    override fun socialSignIn(token: String): Single<User> =
        api.socialSignIn(token)

    override fun getSystemSettings(): Single<String> = api.getSystemSettings().map {
        it.clientId
    }

    override fun login(user: User): Single<String> {
        val userData = "${user.mail}:${user.password}"
        return api.login("Basic ${userData.encode()}").map {
            it.headers().get("X-Auth-Token")
        }
    }

    override fun getUserInfo(token: String): Single<User> = api.getUser(token)

    override fun insertNewUser(user: User): Completable {
        val userNew = JsonObject()
        userNew.addProperty("login", user.mail)
        userNew.addProperty("password", user.password)
        userNew.addProperty("username", user.mail)
        return api.register(userNew)
    }

    private fun String.encode(): String {
        return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.NO_WRAP)
    }


}

