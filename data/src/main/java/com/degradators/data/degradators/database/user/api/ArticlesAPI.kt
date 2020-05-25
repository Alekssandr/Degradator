package com.degradators.data.degradators.database.user.api

import com.degradators.data.degradators.database.user.model.ArticlesEntity
import com.degradators.degradators.model.Articles
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
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

//    /api/p/message?type="new"&skip=0
    ///api/p/likeOrDislike?messageId="someId"&value=1
    //"/api/p/likeOrDislike"
    @GET("/api/p/likeOrDislike?")
    fun getLike(
        @Header("Client-Id") clientId: String,
        @Query("messageId") messageId : String,
        @Query("value") like: Int
    ):  Completable

}
