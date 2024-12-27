package com.roomreservation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.roomreservation.model.BookingDto
import com.roomreservation.ui.theme.RoomReservationTheme
import com.roomreservation.view.BookingViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class ViewBookingsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: BookingViewModel by viewModels()
        val userId = intent.getLongExtra("userId", -1)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.loadUserBookings(userId)
            }
        }

        setContent {
            RoomReservationTheme {
                viewModel.error?.let { error ->
                    LaunchedEffect(error) {
                        Toast.makeText(this@ViewBookingsActivity, error, Toast.LENGTH_LONG).show()
                        viewModel.error = null
                    }
                }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("My Bookings") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )
                    }
                ) { padding ->
                    BookingsList(
                        bookings = viewModel.bookings,
                        onDeleteBooking = { bookingId ->
                            viewModel.deleteBooking(bookingId)
                        },
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}

@Composable
fun BookingsList(
    bookings: List<BookingDto>,
    onDeleteBooking: (Long) -> Unit,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(bookings) { booking ->
            BookingCard(
                booking = booking,
                onDelete = { onDeleteBooking(booking.id) },
                onModify = {
                    val intent = Intent(context, CreateBookingActivity::class.java).apply {
                        putExtra("userId", booking.userId)
                        putExtra("bookingId", booking.id)
                        putExtra("startTime", booking.startTime.toString())
                        putExtra("endTime", booking.endTime.toString())
                        putExtra("roomId", booking.roomId)
                    }
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun BookingCard(
    booking: BookingDto,
    onDelete: () -> Unit,
    onModify: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")

    Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.roomName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Start: ${booking.startTime.format(formatter)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "End: ${booking.endTime.format(formatter)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = onModify,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Modify")
                    }
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}