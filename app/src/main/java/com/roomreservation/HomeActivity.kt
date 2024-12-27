package com.roomreservation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.roomreservation.model.UserDto
import com.roomreservation.ui.theme.RoomReservationTheme
import com.roomreservation.view.UserViewModel

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: UserViewModel by viewModels()

        val email = intent.getStringExtra("email") ?: ""
        val password = intent.getStringExtra("password") ?: ""

        viewModel.login(email, password)

        setContent {
            RoomReservationTheme {
                viewModel.error?.let { error ->
                    LaunchedEffect(error) {
                        Toast.makeText(this@HomeActivity, error, Toast.LENGTH_LONG).show()
                        finish()
                    }
                }

                Scaffold { innerPadding ->
                    HomeScreen(
                        user = viewModel.user,
                        onViewBookings = {
                            viewModel.user?.let { user ->
                                startActivity(Intent(this, ViewBookingsActivity::class.java).apply {
                                    putExtra("userId", user.id)
                                })
                            }
                        },
                        onCreateBooking = {
                            viewModel.user?.let { user ->
                                startActivity(Intent(this, CreateBookingActivity::class.java).apply {
                                    putExtra("userId", user.id)
                                })
                            }
                        },
                        onLogout = {
                            viewModel.logout()
                            finish()
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    user: UserDto?,
    onViewBookings: () -> Unit,
    onCreateBooking: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (user != null) {
            Text(
                text = "Hello ${user.firstName}",
                style = MaterialTheme.typography.headlineMedium
            )

            Button(onClick = onViewBookings, modifier = Modifier.fillMaxWidth()) {
                Text("View My Bookings")
            }

            Button(onClick = onCreateBooking, modifier = Modifier.fillMaxWidth()) {
                Text("Create New Booking")
            }

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Log Out")
            }
        } else {
            CircularProgressIndicator()
        }
    }
}