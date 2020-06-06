package com.degradators.data.degradators.database.user.api

import com.degradators.data.degradators.database.user.model.ImageEntity
import com.degradators.degradators.model.Articles
import com.degradators.degradators.model.NewPost
import com.google.gson.JsonObject
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*


interface ArticlesAPI {

    @GET("/api/p/message?=query")
    fun getListOfPost(
        @Header("Client-Id") clientId: String,
        @Query("type") type: String,
        @Query("skip") skip: Long
    ): Single<Articles>

    @GET("/api/p/likeOrDislike?")
    fun getLike(
        @Header("Client-Id") clientId: String,
        @Query("messageId") messageId : String,
        @Query("value") like: Int
    ):  Completable


    @Multipart
    @POST("/api/p/upload")
    fun addImage(
        @Part file: MultipartBody.Part
    ): Single<ImageEntity>

    @PUT("/api/p/message")
    fun addArticle(
        @Header("Client-Id") clientId: String,
        @Body newPost: NewPost
    ): Completable

}
