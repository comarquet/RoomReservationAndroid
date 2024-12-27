package com.roomreservation

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.roomreservation.model.BookingDto
import com.roomreservation.model.RoomDto
import com.roomreservation.ui.theme.RoomReservationTheme
import com.roomreservation.view.BookingViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId

class CreateBookingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: BookingViewModel by viewModels()
        val userId = intent.getLongExtra("userId", -1)
        val bookingId = intent.getLongExtra("bookingId", -1)
        val existingStartTime = intent.getStringExtra("startTime")?.let { LocalDateTime.parse(it) }
        val existingEndTime = intent.getStringExtra("endTime")?.let { LocalDateTime.parse(it) }
        val existingRoomId = intent.getLongExtra("roomId", -1)

        setContent {
            RoomReservationTheme {
                viewModel.error?.let { error ->
                    LaunchedEffect(error) {
                        Toast.makeText(this@CreateBookingActivity, error, Toast.LENGTH_LONG).show()
                    }
                }

                viewModel.bookingCreated?.let {
                    LaunchedEffect(Unit) {
                        Toast.makeText(this@CreateBookingActivity,
                            if (bookingId != -1L) "Booking updated successfully!"
                            else "Booking created successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }

                Scaffold { padding ->
                    CreateBookingScreen(
                        rooms = viewModel.availableRooms,
                        initialDate = existingStartTime?.toLocalDate() ?: LocalDate.now(),
                        initialStartTime = existingStartTime?.toLocalTime() ?: LocalTime.of(9, 0),
                        initialEndTime = existingEndTime?.toLocalTime() ?: LocalTime.of(10, 0),
                        initialRoomId = if (existingRoomId != -1L) existingRoomId else null,
                        onCreateBooking = { date, startTime, endTime, roomId ->
                            val booking = BookingDto(
                                id = bookingId,
                                startTime = LocalDateTime.of(date, startTime),
                                endTime = LocalDateTime.of(date, endTime),
                                roomId = roomId,
                                userId = userId
                            )
                            if (bookingId != -1L) {
                                viewModel.updateBooking(booking)
                            } else {
                                viewModel.createBooking(booking)
                            }
                        },
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBookingScreen(
    rooms: List<RoomDto>,
    initialDate: LocalDate = LocalDate.now(),
    initialStartTime: LocalTime = LocalTime.of(9, 0),
    initialEndTime: LocalTime = LocalTime.of(10, 0),
    initialRoomId: Long? = null,
    onCreateBooking: (LocalDate, LocalTime, LocalTime, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    var startTime by remember { mutableStateOf(initialStartTime) }
    var endTime by remember { mutableStateOf(initialEndTime) }
    var selectedRoomId by remember { mutableStateOf(initialRoomId) }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DatePicker(
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it }
        )

        TimePicker(
            label = "Start Time",
            time = startTime,
            onTimeSelected = { startTime = it }
        )

        TimePicker(
            label = "End Time",
            time = endTime,
            onTimeSelected = { endTime = it }
        )

        if (rooms.isNotEmpty()) {
            Text("Select Room:", style = MaterialTheme.typography.labelLarge)
            rooms.forEach { room ->
                RadioButton(
                    selected = selectedRoomId == room.id,
                    onClick = { selectedRoomId = room.id },
                    label = "${room.name} (Capacity: ${room.capacity})"
                )
            }
        } else {
            Text("No rooms available for selected time")
        }

        Button(
            onClick = {
                selectedRoomId?.let { roomId ->
                    onCreateBooking(selectedDate, startTime, endTime, roomId)
                }
            },
            enabled = selectedRoomId != null,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Create Booking")
        }
    }
}

@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    OutlinedTextField(
        value = selectedDate.toString(),
        onValueChange = { },
        label = { Text("Date") },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        enabled = false
    )

    Button(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Select Date")
    }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(newDate)
                    }
                    showDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    label: String,
    time: LocalTime,
    onTimeSelected: (LocalTime) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val state = rememberTimePickerState(
        initialHour = time.hour,
        initialMinute = time.minute
    )

    OutlinedTextField(
        value = time.format(DateTimeFormatter.ofPattern("HH:mm")),
        onValueChange = { },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        enabled = false
    )

    Button(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Select $label")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onTimeSelected(LocalTime.of(state.hour, state.minute))
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
            text = {
                TimePicker(state = state)
            }
        )
    }
}