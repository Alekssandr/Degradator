package com.degradators.data.degradators.database.user.repo

import com.degradators.data.degradators.database.user.api.ArticlesAPI
import com.degradators.degradators.repo.ImageRepository
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class ImageDataRepository(
    private val api: ArticlesAPI
) : ImageRepository {
    override fun uploadImage(clientId: String, filePath: String): Single<String> {
        val file = File(filePath)
        val requestFile =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body =
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        return api.addImage(body).map { it.url }
    }

}

