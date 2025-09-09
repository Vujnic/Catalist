package com.example.catalist.features.registration.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RegistrationScreen(
    onRegistrationComplete: () -> Unit,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState().value

    LaunchedEffect(state.isRegistrationComplete) {
        if (state.isRegistrationComplete) {
            onRegistrationComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = state.fullName,
            onValueChange = viewModel::onFullNameChanged,
            label = { Text("Full Name") },
            isError = state.fullNameError != null,
            supportingText = state.fullNameError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.nickname,
            onValueChange = viewModel::onNicknameChanged,
            label = { Text("Nickname") },
            isError = state.nicknameError != null,
            supportingText = state.nicknameError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChanged,
            label = { Text("Email") },
            isError = state.emailError != null,
            supportingText = state.emailError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = viewModel::onRegister,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Create Account")
            }
        }
    }
}