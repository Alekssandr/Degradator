package com.degradators.degradators.usecase

import com.degradators.degradators.model.user.User
import com.degradators.degradators.repo.UserAuthRepository
import io.reactivex.Completable
import javax.inject.Inject

class InsertNewUserUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository

) {
    fun execute(user: User): Completable {
        return userAuthRepository.insertNewUser(user)
    }
}
