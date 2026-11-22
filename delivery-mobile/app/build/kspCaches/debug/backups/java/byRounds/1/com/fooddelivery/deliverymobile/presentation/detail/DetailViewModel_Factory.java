package com.fooddelivery.deliverymobile.presentation.detail;

import com.fooddelivery.deliverymobile.data.repository.DeliveryRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DetailViewModel_Factory implements Factory<DetailViewModel> {
  private final Provider<DeliveryRepository> deliveryRepositoryProvider;

  public DetailViewModel_Factory(Provider<DeliveryRepository> deliveryRepositoryProvider) {
    this.deliveryRepositoryProvider = deliveryRepositoryProvider;
  }

  @Override
  public DetailViewModel get() {
    return newInstance(deliveryRepositoryProvider.get());
  }

  public static DetailViewModel_Factory create(
      Provider<DeliveryRepository> deliveryRepositoryProvider) {
    return new DetailViewModel_Factory(deliveryRepositoryProvider);
  }

  public static DetailViewModel newInstance(DeliveryRepository deliveryRepository) {
    return new DetailViewModel(deliveryRepository);
  }
}
