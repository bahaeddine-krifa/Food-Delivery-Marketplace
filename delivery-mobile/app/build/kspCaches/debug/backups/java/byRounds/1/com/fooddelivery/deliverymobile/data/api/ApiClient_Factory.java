package com.fooddelivery.deliverymobile.data.api;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ApiClient_Factory implements Factory<ApiClient> {
  private final Provider<Context> contextProvider;

  private final Provider<AuthInterceptor> authInterceptorProvider;

  public ApiClient_Factory(Provider<Context> contextProvider,
      Provider<AuthInterceptor> authInterceptorProvider) {
    this.contextProvider = contextProvider;
    this.authInterceptorProvider = authInterceptorProvider;
  }

  @Override
  public ApiClient get() {
    return newInstance(contextProvider.get(), authInterceptorProvider.get());
  }

  public static ApiClient_Factory create(Provider<Context> contextProvider,
      Provider<AuthInterceptor> authInterceptorProvider) {
    return new ApiClient_Factory(contextProvider, authInterceptorProvider);
  }

  public static ApiClient newInstance(Context context, AuthInterceptor authInterceptor) {
    return new ApiClient(context, authInterceptor);
  }
}
