package com.example.nametagprojectwalling.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nametagprojectwalling.data.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users ORDER BY lastLoginTimestamp DESC LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("UPDATE users SET lastLoginTimestamp = :timestamp WHERE email = :email")
    suspend fun updateLoginTimestamp(email: String, timestamp: Long = System.currentTimeMillis())
}