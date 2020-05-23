package com.degradators.data.degradators.database.user.api

import com.degradators.data.degradators.database.user.model.ArticlesEntity
import com.degradators.degradators.model.Articles
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticlesAPI {

    @GET("/api/p/message?=query")
    fun getListOfPost(
        @Header("Client-Id") clientId: String,
        @Query("type") type: String,
        @Query("skip") skip: Long
    ): Single<Articles>

    @GET("/api/p/likeOrDislike?messageId={id}&value=1")
    fun getLike(
        @Path("id") articleId: String
    ): Single<Int>

}
