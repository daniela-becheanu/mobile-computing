package com.example.billboards.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.billboards.ui.common.UiState

@Composable
fun AuthScreen(
    vm: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val state by vm.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is UiState.Success) {
            onLoginSuccess()
            vm.resetState()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Autentificare", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Parolă") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { vm.login(email, password) },
            enabled = state !is UiState.Loading
        ) {
            Text("Login")
        }

        Spacer(Modifier.height(12.dp))

        when (val s = state) {
            UiState.Loading -> {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("Se autentifică...")
            }

            is UiState.Error -> {
                Text("Eroare: ${s.message}")
            }

            else -> Unit
        }
    }
}
