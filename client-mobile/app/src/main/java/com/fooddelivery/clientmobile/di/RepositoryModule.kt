package com.fooddelivery.clientmobile.di

import com.fooddelivery.clientmobile.data.repository.*
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
    fun provideAuthRepository(repository: AuthRepository): AuthRepository = repository

    @Provides
    @Singleton
    fun provideRestaurantRepository(repository: RestaurantRepository): RestaurantRepository = repository

    @Provides
    @Singleton
    fun provideCategoryRepository(repository: CategoryRepository): CategoryRepository = repository

    @Provides
    @Singleton
    fun provideProductRepository(repository: ProductRepository): ProductRepository = repository

    @Provides
    @Singleton
    fun provideOrderRepository(repository: OrderRepository): OrderRepository = repository

    @Provides
    @Singleton
    fun provideAddressRepository(repository: AddressRepository): AddressRepository = repository

    @Provides
    @Singleton
    fun provideReviewRepository(repository: ReviewRepository): ReviewRepository = repository
}