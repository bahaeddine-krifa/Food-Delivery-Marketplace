package com.fooddelivery.deliverymobile.di;

import com.fooddelivery.deliverymobile.data.api.ApiService;
import com.fooddelivery.deliverymobile.data.local.TokenProvider;
import com.fooddelivery.deliverymobile.data.repository.DeliveryRepository;
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
public final class RepositoryModule_ProvideDeliveryRepositoryFactory implements Factory<DeliveryRepository> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<TokenProvider> tokenProvider;

  public RepositoryModule_ProvideDeliveryRepositoryFactory(Provider<ApiService> apiServiceProvider,
      Provider<TokenProvider> tokenProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.tokenProvider = tokenProvider;
  }

  @Override
  public DeliveryRepository get() {
    return provideDeliveryRepository(apiServiceProvider.get(), tokenProvider.get());
  }

  public static RepositoryModule_ProvideDeliveryRepositoryFactory create(
      Provider<ApiService> apiServiceProvider, Provider<TokenProvider> tokenProvider) {
    return new RepositoryModule_ProvideDeliveryRepositoryFactory(apiServiceProvider, tokenProvider);
  }

  public static DeliveryRepository provideDeliveryRepository(ApiService apiService,
      TokenProvider tokenProvider) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideDeliveryRepository(apiService, tokenProvider));
  }
}
