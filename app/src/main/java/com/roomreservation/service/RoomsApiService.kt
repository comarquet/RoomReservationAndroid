package com.roomreservation.service

import com.roomreservation.model.RoomDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDateTime

interface RoomsApiService {
    @GET("rooms")
    fun getAvailableRooms(): Call<List<RoomDto>>

    @GET("rooms/available")
    fun getAvailableRoomsForTimeSlot(
        @Query("startTime") startTime: LocalDateTime,
        @Query("endTime") endTime: LocalDateTime
    ): Call<List<RoomDto>>
}