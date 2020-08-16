package com.degradators.degradators.usecase.user

import com.degradators.degradators.model.user.User
import com.degradators.degradators.repo.UserAuthRepository
import io.reactivex.Single
import javax.inject.Inject

class UserInfoUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository

) {
    fun execute(token: String): Single<User> {
        return userAuthRepository.getUserInfo(token)
    }
}
