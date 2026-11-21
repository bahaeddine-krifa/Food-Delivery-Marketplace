package com.fooddelivery.clientmobile.di

import com.fooddelivery.clientmobile.data.api.ApiClient
import com.fooddelivery.clientmobile.data.api.ApiService
import com.fooddelivery.clientmobile.data.api.AuthTokenProvider
import com.fooddelivery.clientmobile.data.local.TokenDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(apiClient: ApiClient): ApiService = apiClient.apiService

    @Provides
    @Singleton
    fun provideAuthTokenProvider(tokenDataStore: TokenDataStore): AuthTokenProvider = tokenDataStore
}