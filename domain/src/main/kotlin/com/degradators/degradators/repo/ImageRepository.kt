package com.degradators.degradators.repo

import io.reactivex.Single
import java.io.File

interface ImageRepository {
    fun uploadImage(clientId: String, filePath: String): Single<String>
}