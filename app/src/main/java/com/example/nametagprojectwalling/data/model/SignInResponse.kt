package com.example.nametagprojectwalling.data.model

import androidx.room.Entity

@Entity(tableName = "token")
data class SignInResponse(
    val token: String
)