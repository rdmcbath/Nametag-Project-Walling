package com.example.nametagprojectwalling.state

import com.example.nametagprojectwalling.data.model.User

sealed class AuthUIState {
    data class Authenticated(val user: User? = null) : AuthUIState()
    data class Unauthenticated(val error: String? = null): AuthUIState()
}