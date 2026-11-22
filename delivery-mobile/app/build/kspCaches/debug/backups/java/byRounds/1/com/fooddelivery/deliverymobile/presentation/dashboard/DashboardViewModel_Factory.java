package com.fooddelivery.deliverymobile.presentation.dashboard;

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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<DeliveryRepository> deliveryRepositoryProvider;

  public DashboardViewModel_Factory(Provider<DeliveryRepository> deliveryRepositoryProvider) {
    this.deliveryRepositoryProvider = deliveryRepositoryProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(deliveryRepositoryProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<DeliveryRepository> deliveryRepositoryProvider) {
    return new DashboardViewModel_Factory(deliveryRepositoryProvider);
  }

  public static DashboardViewModel newInstance(DeliveryRepository deliveryRepository) {
    return new DashboardViewModel(deliveryRepository);
  }
}
