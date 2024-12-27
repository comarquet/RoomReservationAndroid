package com.roomreservation.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserCommandDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)