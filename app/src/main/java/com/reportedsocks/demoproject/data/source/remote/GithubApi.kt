package com.reportedsocks.demoproject.data.source.remote

import com.reportedsocks.demoproject.data.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {

    @GET("/users")
    suspend fun getUsers(@Query("since") lastId: Int): Response<List<User>>

    @GET("/users")
    fun getUsersSync(@Query("since") lastId: Int): Call<List<User>>

}