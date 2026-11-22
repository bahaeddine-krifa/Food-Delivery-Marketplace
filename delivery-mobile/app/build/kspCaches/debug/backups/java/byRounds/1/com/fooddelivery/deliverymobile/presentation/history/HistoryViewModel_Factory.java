package com.fooddelivery.deliverymobile.presentation.history;

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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<DeliveryRepository> deliveryRepositoryProvider;

  public HistoryViewModel_Factory(Provider<DeliveryRepository> deliveryRepositoryProvider) {
    this.deliveryRepositoryProvider = deliveryRepositoryProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(deliveryRepositoryProvider.get());
  }

  public static HistoryViewModel_Factory create(
      Provider<DeliveryRepository> deliveryRepositoryProvider) {
    return new HistoryViewModel_Factory(deliveryRepositoryProvider);
  }

  public static HistoryViewModel newInstance(DeliveryRepository deliveryRepository) {
    return new HistoryViewModel(deliveryRepository);
  }
}
