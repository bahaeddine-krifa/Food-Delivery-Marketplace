package com.fooddelivery.deliverymobile.di;

import com.fooddelivery.deliverymobile.data.api.ApiClient;
import com.fooddelivery.deliverymobile.data.api.ApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class NetworkModule_ProvideApiServiceFactory implements Factory<ApiService> {
  private final Provider<ApiClient> apiClientProvider;

  public NetworkModule_ProvideApiServiceFactory(Provider<ApiClient> apiClientProvider) {
    this.apiClientProvider = apiClientProvider;
  }

  @Override
  public ApiService get() {
    return provideApiService(apiClientProvider.get());
  }

  public static NetworkModule_ProvideApiServiceFactory create(
      Provider<ApiClient> apiClientProvider) {
    return new NetworkModule_ProvideApiServiceFactory(apiClientProvider);
  }

  public static ApiService provideApiService(ApiClient apiClient) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideApiService(apiClient));
  }
}
