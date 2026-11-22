package com.fooddelivery.deliverymobile.presentation.workflow;

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
public final class WorkflowViewModel_Factory implements Factory<WorkflowViewModel> {
  private final Provider<DeliveryRepository> deliveryRepositoryProvider;

  public WorkflowViewModel_Factory(Provider<DeliveryRepository> deliveryRepositoryProvider) {
    this.deliveryRepositoryProvider = deliveryRepositoryProvider;
  }

  @Override
  public WorkflowViewModel get() {
    return newInstance(deliveryRepositoryProvider.get());
  }

  public static WorkflowViewModel_Factory create(
      Provider<DeliveryRepository> deliveryRepositoryProvider) {
    return new WorkflowViewModel_Factory(deliveryRepositoryProvider);
  }

  public static WorkflowViewModel newInstance(DeliveryRepository deliveryRepository) {
    return new WorkflowViewModel(deliveryRepository);
  }
}
