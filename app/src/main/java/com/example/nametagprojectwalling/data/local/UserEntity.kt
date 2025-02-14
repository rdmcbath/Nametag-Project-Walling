package com.example.nametagprojectwalling.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val hashedPassword: String,
    val lastLoginTimestamp: Long = System.currentTimeMillis()
)