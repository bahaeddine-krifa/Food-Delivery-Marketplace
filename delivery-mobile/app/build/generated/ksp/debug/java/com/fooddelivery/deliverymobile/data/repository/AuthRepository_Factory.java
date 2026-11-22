package com.fooddelivery.deliverymobile.data.repository;

import com.fooddelivery.deliverymobile.data.api.ApiService;
import com.fooddelivery.deliverymobile.data.local.TokenProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("dagger.hilt.android.scopes.ActivityRetainedScoped")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AuthRepository_Factory implements Factory<AuthRepository> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<TokenProvider> tokenProvider;

  public AuthRepository_Factory(Provider<ApiService> apiServiceProvider,
      Provider<TokenProvider> tokenProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.tokenProvider = tokenProvider;
  }

  @Override
  public AuthRepository get() {
    return newInstance(apiServiceProvider.get(), tokenProvider.get());
  }

  public static AuthRepository_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<TokenProvider> tokenProvider) {
    return new AuthRepository_Factory(apiServiceProvider, tokenProvider);
  }

  public static AuthRepository newInstance(ApiService apiService, TokenProvider tokenProvider) {
    return new AuthRepository(apiService, tokenProvider);
  }
}
