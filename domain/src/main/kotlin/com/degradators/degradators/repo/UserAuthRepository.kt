package com.degradators.degradators.repo

import com.degradators.degradators.model.User
import io.reactivex.Completable
import io.reactivex.Single

interface UserAuthRepository {
    fun insertNewUser(user: User): Completable
    fun login(user: User): Single<String>
    fun getSystemSettings(): Single<String>
    fun getUser(token: String, clientId: String): Single<User>
}