package com.degradators.data.degradators.database.user.api

import com.google.gson.JsonObject
import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.PUT

interface UserAuthAPI {


    @Headers("Content-Type: application/json")
    @PUT("/api/p/user")
    fun registerAndLogin(@Body user: JsonObject): Completable
}
