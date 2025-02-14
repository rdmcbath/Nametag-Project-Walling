package com.example.nametagprojectwalling.di

import android.content.Context
import androidx.room.Room
import com.example.nametagprojectwalling.data.local.DuckitDatabase
import com.example.nametagprojectwalling.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDuckitDatabase(
        @ApplicationContext context: Context
    ): DuckitDatabase {
        return Room.databaseBuilder(
            context,
            DuckitDatabase::class.java,
            "duckit_database"
        ).build()
    }

    @Provides
    fun provideUserDao(database: DuckitDatabase): UserDao {
        return database.userDao()
    }
}