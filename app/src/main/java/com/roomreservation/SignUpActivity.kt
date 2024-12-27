package com.roomreservation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roomreservation.model.UserDto
import com.roomreservation.ui.theme.RoomReservationTheme
import com.roomreservation.view.UserViewModel

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: UserViewModel by viewModels()

        setContent {
            RoomReservationTheme {
                viewModel.user?.let {
                    LaunchedEffect(it) {
                        Toast.makeText(this@SignUpActivity, "User created successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                viewModel.error?.let { error ->
                    LaunchedEffect(error) {
                        Toast.makeText(this@SignUpActivity, error, Toast.LENGTH_LONG).show()
                        viewModel.error = null
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SignUp(
                        onClick = { viewModel.createUser(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SignUp(
    onClick: (UserDto) -> Unit,
    modifier: Modifier = Modifier
) {
    var user by remember { mutableStateOf(UserDto(0, "", "", "", "", 0)) }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Column {
        Text(
            stringResource(R.string.first_name),
            modifier = Modifier.padding(12.dp)
        )
        OutlinedTextField(
            value = user.firstName,
            onValueChange = { user = user.copy(firstName = it) },
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        )

        Text(
            stringResource(R.string.last_name),
            modifier = Modifier.padding(12.dp)
        )
        OutlinedTextField(
            value = user.lastName,
            onValueChange = { user = user.copy(lastName = it) },
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        )

        Text(
            stringResource(R.string.email),
            modifier = Modifier.padding(12.dp)
        )
        OutlinedTextField(
            value = user.email,
            onValueChange = { user = user.copy(email = it) },
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        )

        Text(
            stringResource(R.string.password),
            modifier = Modifier.padding(12.dp)
        )
        OutlinedTextField(
            value = user.password,
            onValueChange = { user = user.copy(password = it) },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = passwordError != null,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = passwordError != null,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        )

        if (passwordError != null) {
            Text(
                text = passwordError!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        Button(
            onClick = {
                if (user.password != confirmPassword) {
                    passwordError = "Passwords do not match"
                } else if (user.password.length < 8) {
                    passwordError = "Password must be at least 8 characters"
                } else {
                    passwordError = null
                    onClick(user)
                }
            },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.create_account))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    RoomReservationTheme {
        SignUp(
            onClick = { },
            modifier = Modifier
        )
    }
}