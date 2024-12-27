package com.roomreservation.model

data class UserDto(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val cardId: Long? = null
)