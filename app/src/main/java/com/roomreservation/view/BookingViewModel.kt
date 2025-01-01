package com.roomreservation.view

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roomreservation.model.BookingDto
import com.roomreservation.model.RoomDto
import com.roomreservation.service.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZoneOffset

class BookingViewModel : ViewModel() {
    var availableRooms by mutableStateOf<List<RoomDto>>(emptyList())
    var error by mutableStateOf<String?>(null)
    var bookingCreated by mutableStateOf<BookingDto?>(null)
    var bookings by mutableStateOf<List<BookingDto>>(emptyList())

    init {
        loadAvailableRooms()
    }

    private fun loadAvailableRooms() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiServices.roomsApiService.getAvailableRooms().execute()
                if (response.isSuccessful) {
                    availableRooms = response.body() ?: emptyList()
                } else {
                    error = "Failed to load rooms: ${response.code()}"
                    Log.e("BookingViewModel", "Error loading rooms: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                error = "Error: ${e.message}"
                Log.e("BookingViewModel", "Exception loading rooms", e)
            }
        }
    }

    fun createBooking(bookingDto: BookingDto) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val localZone = ZoneId.systemDefault()
                val utcBookingDto = bookingDto.copy(
                    startTime = bookingDto.startTime
                        .atZone(localZone)
                        .toOffsetDateTime()
                        .toZonedDateTime()
                        .withZoneSameInstant(ZoneOffset.UTC)
                        .toLocalDateTime(),
                    endTime = bookingDto.endTime
                        .atZone(localZone)
                        .toOffsetDateTime()
                        .toZonedDateTime()
                        .withZoneSameInstant(ZoneOffset.UTC)
                        .toLocalDateTime()
                )

                val response = ApiServices.bookingsApiService.createBooking(utcBookingDto).execute()
                if (response.isSuccessful) {
                    bookingCreated = response.body()
                } else {
                    error = when (response.code()) {
                        409 -> "This room is already booked for the selected time slot"
                        else -> "Failed to create booking: ${response.code()} - ${response.errorBody()?.string()}"
                    }
                }
            } catch (e: Exception) {
                error = "Error: ${e.message}"
            }
        }
    }

    fun loadUserBookings(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiServices.bookingsApiService.getUserBookings(userId).execute()
                if (response.isSuccessful) {
                    bookings = response.body() ?: emptyList()
                } else {
                    error = "Failed to load bookings: ${response.code()}"
                    Log.e("BookingsViewModel", "Error loading bookings: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                error = "Error: ${e.message}"
                Log.e("BookingsViewModel", "Exception loading bookings", e)
            }
        }
    }

    fun deleteBooking(bookingId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiServices.bookingsApiService.deleteBooking(bookingId).execute()
                if (response.isSuccessful) {
                    // Remove the deleted booking from the list
                    bookings = bookings.filter { it.id != bookingId }
                } else {
                    error = "Failed to delete booking: ${response.code()}"
                    Log.e("BookingsViewModel", "Error deleting booking: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                error = "Error: ${e.message}"
                Log.e("BookingsViewModel", "Exception deleting booking", e)
            }
        }
    }

    fun updateBooking(booking: BookingDto) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val localZone = ZoneId.systemDefault()
                val utcBooking = booking.copy(
                    startTime = booking.startTime
                        .atZone(localZone)
                        .toOffsetDateTime()
                        .toZonedDateTime()
                        .withZoneSameInstant(ZoneOffset.UTC)
                        .toLocalDateTime(),
                    endTime = booking.endTime
                        .atZone(localZone)
                        .toOffsetDateTime()
                        .toZonedDateTime()
                        .withZoneSameInstant(ZoneOffset.UTC)
                        .toLocalDateTime()
                )

                val response = ApiServices.bookingsApiService.updateBooking(utcBooking.id, utcBooking).execute()
                if (response.isSuccessful) {
                    bookingCreated = response.body()
                } else {
                    error = when (response.code()) {
                        409 -> "This room is already booked for the selected time slot"
                        else -> "Failed to update booking: ${response.code()} - ${response.errorBody()?.string()}"
                    }
                }
            } catch (e: Exception) {
                error = "Error: ${e.message}"
            }
        }
    }
}