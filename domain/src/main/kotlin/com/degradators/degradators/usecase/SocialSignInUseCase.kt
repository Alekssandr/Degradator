package com.degradators.degradators.usecase

import com.degradators.degradators.model.user.User
import com.degradators.degradators.repo.UserAuthRepository
import io.reactivex.Single
import javax.inject.Inject

class SocialSignInUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository

) {
    fun execute(token: String): Single<User> {
        return userAuthRepository.socialSignIn(token)
    }
}
