package com.roomreservation.service

import com.roomreservation.model.UserCommandDto
import com.roomreservation.model.UserDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UsersApiService {
    @GET("users/{id}")
    fun findById(@Path("id") id: Long): Call<UserDto>

    @PUT("users/{id}")
    fun updateUser(@Path("id") id: Long, @Body user: UserCommandDto): Call<UserDto>

    @POST("users")
    fun createUser(@Body room: UserCommandDto): Call<UserDto>

    @DELETE("users/{id}")
    fun deleteUser(@Path("id") id: Long): Call<Void>
}