package com.fooddelivery.clientmobile.di

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fooddelivery.clientmobile.presentation.auth.AuthViewModel
import com.fooddelivery.clientmobile.presentation.home.HomeViewModel
import com.fooddelivery.clientmobile.presentation.restaurant.RestaurantDetailViewModel
import com.fooddelivery.clientmobile.presentation.cart.CartViewModel
import com.fooddelivery.clientmobile.presentation.checkout.CheckoutViewModel
import com.fooddelivery.clientmobile.presentation.orders.OrdersViewModel
import com.fooddelivery.clientmobile.presentation.orderdetail.OrderDetailViewModel
import com.fooddelivery.clientmobile.presentation.profile.ProfileViewModel
import com.fooddelivery.clientmobile.presentation.address.AddressViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.ActivityRetainedComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(viewModel: AuthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RestaurantDetailViewModel::class)
    abstract fun bindRestaurantDetailViewModel(viewModel: RestaurantDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CartViewModel::class)
    abstract fun bindCartViewModel(viewModel: CartViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CheckoutViewModel::class)
    abstract fun bindCheckoutViewModel(viewModel: CheckoutViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrdersViewModel::class)
    abstract fun bindOrdersViewModel(viewModel: OrdersViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrderDetailViewModel::class)
    abstract fun bindOrderDetailViewModel(viewModel: OrderDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindProfileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddressViewModel::class)
    abstract fun bindAddressViewModel(viewModel: AddressViewModel): ViewModel
}