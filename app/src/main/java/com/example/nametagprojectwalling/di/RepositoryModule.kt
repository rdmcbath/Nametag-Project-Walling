package com.example.nametagprojectwalling.di


import android.content.SharedPreferences
import com.example.nametagprojectwalling.api.DuckitApi
import com.example.nametagprojectwalling.data.local.UserDao
import com.example.nametagprojectwalling.repository.DuckitRepository
import com.example.nametagprojectwalling.repository.DuckitRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDuckitRepository(
        api: DuckitApi,
        userDao: UserDao,
        sharedPreferences: SharedPreferences
    ): DuckitRepository {
        return DuckitRepositoryImpl(api, userDao, sharedPreferences)
    }
}
