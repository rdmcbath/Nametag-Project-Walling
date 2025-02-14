package com.example.nametagprojectwalling.data.model

import androidx.room.Entity

@Entity(tableName = "signIn")
data class SignInRequest(
    val email: String,
    val password: String
)