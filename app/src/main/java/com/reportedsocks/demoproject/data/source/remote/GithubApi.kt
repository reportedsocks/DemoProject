package com.reportedsocks.demoproject.data.source.remote

import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {

    companion object {
        const val server = "https://api.github.com"
    }

    @GET("/users")
    suspend fun getUsers(): Response<List<User>>

    @GET("/users")
    suspend fun getUsersFromId(@Query("since") lastId: Int): Result<List<User>>

}