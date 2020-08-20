package com.degradators.data.degradators.database.user.api

import com.degradators.data.degradators.database.user.model.article.ArticlesEntity
import com.degradators.data.degradators.database.user.model.article.ImageEntity
import com.degradators.degradators.model.NewComment
import com.degradators.degradators.model.NewPost
import com.degradators.degradators.model.PostIds
import com.degradators.degradators.model.comment.Comments
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*


interface ArticlesAPI {

    @GET("/api/p/message?=query")
    fun getListOfPost(
        @Header("Client-Id") clientId: String,
        @Query("type") type: String,
        @Query("skip") skip: Long
    ): Single<ArticlesEntity>

    @GET("/api/p/likeOrDislike?")
    fun getLike(
        @Header("X-Auth-Token") token: String,
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
        @Header("X-Auth-Token") token: String,
        @Header("Client-Id") clientId: String,
        @Body newPost: NewPost
    ): Completable

    @Headers("Content-Type: application/json")
    @POST("/api/p/message")
    fun getArticlesByList(
        @Body postIds: PostIds
    ): Single<ArticlesEntity>

    @PUT("/api/p/message")
    fun addComment(
        @Header("X-Auth-Token") token: String,
        @Body newComment: NewComment
    ): Completable

    @GET("api/p/comment/{id}")
    fun getComment(
        @Header("X-Auth-Token") token: String,
        @Path("id") productId: String
    ): Single<Comments>

    @DELETE("/api/s/message/{messageId}")
    fun removeArticles(
        @Header("Client-Id") clientId: String,
        @Path("messageId") messageId: String
    ) : Completable

}
