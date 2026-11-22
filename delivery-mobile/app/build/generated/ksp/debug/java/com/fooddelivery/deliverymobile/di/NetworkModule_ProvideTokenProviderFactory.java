package com.fooddelivery.deliverymobile.di;

import com.fooddelivery.deliverymobile.data.local.TokenDataStore;
import com.fooddelivery.deliverymobile.data.local.TokenProvider;
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
public final class NetworkModule_ProvideTokenProviderFactory implements Factory<TokenProvider> {
  private final Provider<TokenDataStore> tokenDataStoreProvider;

  public NetworkModule_ProvideTokenProviderFactory(
      Provider<TokenDataStore> tokenDataStoreProvider) {
    this.tokenDataStoreProvider = tokenDataStoreProvider;
  }

  @Override
  public TokenProvider get() {
    return provideTokenProvider(tokenDataStoreProvider.get());
  }

  public static NetworkModule_ProvideTokenProviderFactory create(
      Provider<TokenDataStore> tokenDataStoreProvider) {
    return new NetworkModule_ProvideTokenProviderFactory(tokenDataStoreProvider);
  }

  public static TokenProvider provideTokenProvider(TokenDataStore tokenDataStore) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideTokenProvider(tokenDataStore));
  }
}
