package com.reportedsocks.demoproject.data.source.remote

import com.reportedsocks.demoproject.data.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {

    /**
     * Returns next page with users, page size is 30,
     * equal to Constants.PAGE_SIZE
     */
    @GET("/users")
    suspend fun getUsers(@Query("since") lastId: Int): Response<List<User>>

    /**
     * For synchronous call
     */
    @GET("/users")
    fun getUsersSync(@Query("since") lastId: Int): Call<List<User>>

}