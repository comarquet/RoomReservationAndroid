package com.roomreservation.view

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roomreservation.model.LoginDto
import com.roomreservation.model.UserCommandDto
import com.roomreservation.model.UserDto
import com.roomreservation.service.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {
    var user by mutableStateOf<UserDto?>(null)
    var error by mutableStateOf<String?>(null)

    fun createUser(userDto: UserDto) {
        val command = UserCommandDto(
            firstName = userDto.firstName,
            lastName = userDto.lastName,
            email = userDto.email,
            password = userDto.password
        )
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiServices.usersApiService.createUser(command).execute()
                when {
                    response.isSuccessful -> {
                        user = response.body()
                    }
                    response.code() == 400 -> {
                        error = "This email address is already in use. Please use a different email."
                    }
                    else -> {
                        error = "An error occurred while creating your account. Please try again."
                    }
                }
            } catch (e: Exception) {
                error = "Network error: ${e.message}"
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val loginDto = LoginDto(email, password)
                val response = ApiServices.authApiService.login(loginDto).execute()

                if (response.isSuccessful) {
                    user = response.body()
                } else {
                    error = "Invalid email or password"
                }
            } catch (e: Exception) {
                error = "Error: ${e.message}"
            }
        }
    }

    fun logout() {
        user = null
        error = null
    }
}