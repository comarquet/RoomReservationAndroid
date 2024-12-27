package com.roomreservation.model

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class BookingDto(
    val id: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val roomId: Long,
    val roomName: String = "",
    val userId: Long
)