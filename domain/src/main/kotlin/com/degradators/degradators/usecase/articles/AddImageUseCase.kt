package com.degradators.degradators.usecase.articles

import com.degradators.degradators.model.Articles
import com.degradators.degradators.repo.ImageRepository
import io.reactivex.Single
import javax.inject.Inject

class AddImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository

) {
    fun execute(clientId: String, filePath: String): Single<String> {
        return imageRepository.uploadImage(clientId, filePath)
    }
}
