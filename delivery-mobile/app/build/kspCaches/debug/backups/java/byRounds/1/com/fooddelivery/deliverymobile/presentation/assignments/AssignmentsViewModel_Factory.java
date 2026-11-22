package com.fooddelivery.deliverymobile.presentation.assignments;

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
public final class AssignmentsViewModel_Factory implements Factory<AssignmentsViewModel> {
  private final Provider<DeliveryRepository> deliveryRepositoryProvider;

  public AssignmentsViewModel_Factory(Provider<DeliveryRepository> deliveryRepositoryProvider) {
    this.deliveryRepositoryProvider = deliveryRepositoryProvider;
  }

  @Override
  public AssignmentsViewModel get() {
    return newInstance(deliveryRepositoryProvider.get());
  }

  public static AssignmentsViewModel_Factory create(
      Provider<DeliveryRepository> deliveryRepositoryProvider) {
    return new AssignmentsViewModel_Factory(deliveryRepositoryProvider);
  }

  public static AssignmentsViewModel newInstance(DeliveryRepository deliveryRepository) {
    return new AssignmentsViewModel(deliveryRepository);
  }
}
