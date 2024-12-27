package com.roomreservation.service

import com.roomreservation.model.LoginDto
import com.roomreservation.model.UserDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    fun login(@Body loginDto: LoginDto): Call<UserDto>
}