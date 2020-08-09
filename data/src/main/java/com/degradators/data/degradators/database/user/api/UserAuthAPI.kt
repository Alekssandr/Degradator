package com.degradators.data.degradators.database.user.api

import com.degradators.degradators.model.ClientId
import com.degradators.degradators.model.User
import com.google.gson.JsonObject
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UserAuthAPI {

    @Headers("Content-Type: application/json")
    @PUT("/api/p/user")
    fun register(@Body user: JsonObject): Completable

    @GET("/api/s/ka")
    fun login(@Header("Authorization") user: String): Single<Response<ResponseBody>>

    @GET("/api/s/user")
    fun socialSignIn(
        @Header("M-Token") token: String
    ): Single<User>

    //TODO we need different header for different sign in
    @GET("/api/s/user")
    fun getUser(
        @Header("X-Auth-Token") token: String
//        @Header("M-Token") token: String
    ): Single<User>

    @GET("/api/p/systemSettings")
    fun getSystemSettings(): Single<ClientId>
}
