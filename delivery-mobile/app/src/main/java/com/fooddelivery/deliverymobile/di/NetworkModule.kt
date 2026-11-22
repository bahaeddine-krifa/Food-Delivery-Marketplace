package com.fooddelivery.deliverymobile.di

import com.fooddelivery.deliverymobile.data.api.ApiClient
import com.fooddelivery.deliverymobile.data.api.ApiService
import com.fooddelivery.deliverymobile.data.api.AuthInterceptor
import com.fooddelivery.deliverymobile.data.local.TokenDataStore
import com.fooddelivery.deliverymobile.data.local.TokenProvider
import com.fooddelivery.deliverymobile.data.repository.AuthRepository
import com.fooddelivery.deliverymobile.data.repository.DeliveryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideAuthInterceptor(tokenProvider: TokenProvider): AuthInterceptor = AuthInterceptor(tokenProvider)

    @Provides
    @Singleton
    fun provideTokenProvider(tokenDataStore: TokenDataStore): TokenProvider = tokenDataStore
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        tokenProvider: TokenProvider
    ): AuthRepository = AuthRepository(apiService, tokenProvider)

    @Provides
    @Singleton
    fun provideDeliveryRepository(
        apiService: ApiService,
        tokenProvider: TokenProvider
    ): DeliveryRepository = DeliveryRepository(apiService, tokenProvider)
}