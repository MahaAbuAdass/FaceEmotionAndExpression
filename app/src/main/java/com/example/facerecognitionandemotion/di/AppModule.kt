package com.example.facerecognitionandemotion.di

import com.example.facerecognitionandemotion.Repository
import com.example.facerecognitionandemotion.UserApi
import com.example.facerecognitionandemotion.UserApi.Companion.BASE_URL
import com.example.facerecognitionandemotion.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {



    @Provides
    @Singleton
    fun provideApi() : UserApi{
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(userApi: UserApi) : Repository {
        return UserRepositoryImpl(userApi)
    }

}