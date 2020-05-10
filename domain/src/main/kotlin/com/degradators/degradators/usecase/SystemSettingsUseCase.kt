package com.degradators.degradators.usecase

import com.degradators.degradators.repo.UserAuthRepository
import io.reactivex.Single
import javax.inject.Inject

class SystemSettingsUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository

) {
    fun execute(): Single<String> {
        return userAuthRepository.getSystemSettings()
    }
}
