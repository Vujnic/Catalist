package com.example.catalist.features.registration.domain

sealed class AuthState {
    object Loading : AuthState()
    object NotLoggedIn : AuthState()
    object LoggedIn : AuthState()
}

sealed class AuthEvent {
    object CheckAuth : AuthEvent()
}
