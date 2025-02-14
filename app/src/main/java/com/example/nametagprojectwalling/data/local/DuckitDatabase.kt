package com.example.nametagprojectwalling.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class],
    version = 1
)
abstract class DuckitDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}