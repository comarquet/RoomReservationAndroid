package com.roomreservation.service

import com.roomreservation.model.BookingDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BookingsApiService {
    @POST("bookings")
    fun createBooking(@Body booking: BookingDto): Call<BookingDto>

    @GET("bookings/user/{userId}")
    fun getUserBookings(@Path("userId") userId: Long): Call<List<BookingDto>>

    @DELETE("bookings/{id}")
    fun deleteBooking(@Path("id") id: Long): Call<Void>

    @PUT("bookings/{id}")
    fun updateBooking(@Path("id") id: Long, @Body booking: BookingDto): Call<BookingDto>
}