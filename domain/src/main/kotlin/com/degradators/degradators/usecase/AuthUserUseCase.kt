package com.degradators.degradators.usecase

import com.degradators.degradators.model.User
import com.degradators.degradators.repo.UserAuthRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class AuthUserUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository

) {
    fun execute(user: User): Single<String> {
        return userAuthRepository.login(user)
    }
}
