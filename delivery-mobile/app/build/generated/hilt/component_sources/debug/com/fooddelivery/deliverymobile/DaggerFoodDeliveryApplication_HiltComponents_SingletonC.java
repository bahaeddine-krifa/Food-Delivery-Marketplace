package com.fooddelivery.deliverymobile;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.fooddelivery.deliverymobile.data.api.ApiClient;
import com.fooddelivery.deliverymobile.data.api.ApiService;
import com.fooddelivery.deliverymobile.data.api.AuthInterceptor;
import com.fooddelivery.deliverymobile.data.local.TokenDataStore;
import com.fooddelivery.deliverymobile.data.local.TokenProvider;
import com.fooddelivery.deliverymobile.data.repository.AuthRepository;
import com.fooddelivery.deliverymobile.data.repository.DeliveryRepository;
import com.fooddelivery.deliverymobile.di.NetworkModule_ProvideApiServiceFactory;
import com.fooddelivery.deliverymobile.di.NetworkModule_ProvideAuthInterceptorFactory;
import com.fooddelivery.deliverymobile.di.NetworkModule_ProvideTokenProviderFactory;
import com.fooddelivery.deliverymobile.di.RepositoryModule_ProvideAuthRepositoryFactory;
import com.fooddelivery.deliverymobile.di.RepositoryModule_ProvideDeliveryRepositoryFactory;
import com.fooddelivery.deliverymobile.presentation.assignments.AssignmentsViewModel;
import com.fooddelivery.deliverymobile.presentation.assignments.AssignmentsViewModel_HiltModules;
import com.fooddelivery.deliverymobile.presentation.auth.AuthViewModel;
import com.fooddelivery.deliverymobile.presentation.auth.AuthViewModel_HiltModules;
import com.fooddelivery.deliverymobile.presentation.dashboard.DashboardViewModel;
import com.fooddelivery.deliverymobile.presentation.dashboard.DashboardViewModel_HiltModules;
import com.fooddelivery.deliverymobile.presentation.detail.DetailViewModel;
import com.fooddelivery.deliverymobile.presentation.detail.DetailViewModel_HiltModules;
import com.fooddelivery.deliverymobile.presentation.history.HistoryViewModel;
import com.fooddelivery.deliverymobile.presentation.history.HistoryViewModel_HiltModules;
import com.fooddelivery.deliverymobile.presentation.workflow.WorkflowViewModel;
import com.fooddelivery.deliverymobile.presentation.workflow.WorkflowViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerFoodDeliveryApplication_HiltComponents_SingletonC {
  private DaggerFoodDeliveryApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public FoodDeliveryApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements FoodDeliveryApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public FoodDeliveryApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements FoodDeliveryApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public FoodDeliveryApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements FoodDeliveryApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public FoodDeliveryApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements FoodDeliveryApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public FoodDeliveryApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements FoodDeliveryApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public FoodDeliveryApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements FoodDeliveryApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public FoodDeliveryApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements FoodDeliveryApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public FoodDeliveryApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends FoodDeliveryApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends FoodDeliveryApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends FoodDeliveryApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends FoodDeliveryApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(6).put(LazyClassKeyProvider.com_fooddelivery_deliverymobile_presentation_assignments_AssignmentsViewModel, AssignmentsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fooddelivery_deliverymobile_presentation_auth_AuthViewModel, AuthViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fooddelivery_deliverymobile_presentation_dashboard_DashboardViewModel, DashboardViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fooddelivery_deliverymobile_presentation_detail_DetailViewModel, DetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fooddelivery_deliverymobile_presentation_history_HistoryViewModel, HistoryViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fooddelivery_deliverymobile_presentation_workflow_WorkflowViewModel, WorkflowViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_fooddelivery_deliverymobile_presentation_assignments_AssignmentsViewModel = "com.fooddelivery.deliverymobile.presentation.assignments.AssignmentsViewModel";

      static String com_fooddelivery_deliverymobile_presentation_dashboard_DashboardViewModel = "com.fooddelivery.deliverymobile.presentation.dashboard.DashboardViewModel";

      static String com_fooddelivery_deliverymobile_presentation_detail_DetailViewModel = "com.fooddelivery.deliverymobile.presentation.detail.DetailViewModel";

      static String com_fooddelivery_deliverymobile_presentation_workflow_WorkflowViewModel = "com.fooddelivery.deliverymobile.presentation.workflow.WorkflowViewModel";

      static String com_fooddelivery_deliverymobile_presentation_auth_AuthViewModel = "com.fooddelivery.deliverymobile.presentation.auth.AuthViewModel";

      static String com_fooddelivery_deliverymobile_presentation_history_HistoryViewModel = "com.fooddelivery.deliverymobile.presentation.history.HistoryViewModel";

      @KeepFieldType
      AssignmentsViewModel com_fooddelivery_deliverymobile_presentation_assignments_AssignmentsViewModel2;

      @KeepFieldType
      DashboardViewModel com_fooddelivery_deliverymobile_presentation_dashboard_DashboardViewModel2;

      @KeepFieldType
      DetailViewModel com_fooddelivery_deliverymobile_presentation_detail_DetailViewModel2;

      @KeepFieldType
      WorkflowViewModel com_fooddelivery_deliverymobile_presentation_workflow_WorkflowViewModel2;

      @KeepFieldType
      AuthViewModel com_fooddelivery_deliverymobile_presentation_auth_AuthViewModel2;

      @KeepFieldType
      HistoryViewModel com_fooddelivery_deliverymobile_presentation_history_HistoryViewModel2;
    }
  }

  private static final class ViewModelCImpl extends FoodDeliveryApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AssignmentsViewModel> assignmentsViewModelProvider;

    private Provider<AuthViewModel> authViewModelProvider;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<DetailViewModel> detailViewModelProvider;

    private Provider<HistoryViewModel> historyViewModelProvider;

    private Provider<WorkflowViewModel> workflowViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.assignmentsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.authViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.detailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.historyViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.workflowViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(6).put(LazyClassKeyProvider.com_fooddelivery_deliverymobile_presentation_assignments_AssignmentsViewModel, ((Provider) assignmentsViewModelProvider)).put(LazyClassKeyProvider.com_fooddelivery_deliverymobile_presentation_auth_AuthViewModel, ((Provider) authViewModelProvider)).put(LazyClassKeyProvider.com_fooddelivery_deliverymobile_presentation_dashboard_DashboardViewModel, ((Provider) dashboardViewModelProvider)).put(LazyClassKeyProvider.com_fooddelivery_deliverymobile_presentation_detail_DetailViewModel, ((Provider) detailViewModelProvider)).put(LazyClassKeyProvider.com_fooddelivery_deliverymobile_presentation_history_HistoryViewModel, ((Provider) historyViewModelProvider)).put(LazyClassKeyProvider.com_fooddelivery_deliverymobile_presentation_workflow_WorkflowViewModel, ((Provider) workflowViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_fooddelivery_deliverymobile_presentation_auth_AuthViewModel = "com.fooddelivery.deliverymobile.presentation.auth.AuthViewModel";

      static String com_fooddelivery_deliverymobile_presentation_detail_DetailViewModel = "com.fooddelivery.deliverymobile.presentation.detail.DetailViewModel";

      static String com_fooddelivery_deliverymobile_presentation_history_HistoryViewModel = "com.fooddelivery.deliverymobile.presentation.history.HistoryViewModel";

      static String com_fooddelivery_deliverymobile_presentation_assignments_AssignmentsViewModel = "com.fooddelivery.deliverymobile.presentation.assignments.AssignmentsViewModel";

      static String com_fooddelivery_deliverymobile_presentation_dashboard_DashboardViewModel = "com.fooddelivery.deliverymobile.presentation.dashboard.DashboardViewModel";

      static String com_fooddelivery_deliverymobile_presentation_workflow_WorkflowViewModel = "com.fooddelivery.deliverymobile.presentation.workflow.WorkflowViewModel";

      @KeepFieldType
      AuthViewModel com_fooddelivery_deliverymobile_presentation_auth_AuthViewModel2;

      @KeepFieldType
      DetailViewModel com_fooddelivery_deliverymobile_presentation_detail_DetailViewModel2;

      @KeepFieldType
      HistoryViewModel com_fooddelivery_deliverymobile_presentation_history_HistoryViewModel2;

      @KeepFieldType
      AssignmentsViewModel com_fooddelivery_deliverymobile_presentation_assignments_AssignmentsViewModel2;

      @KeepFieldType
      DashboardViewModel com_fooddelivery_deliverymobile_presentation_dashboard_DashboardViewModel2;

      @KeepFieldType
      WorkflowViewModel com_fooddelivery_deliverymobile_presentation_workflow_WorkflowViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.fooddelivery.deliverymobile.presentation.assignments.AssignmentsViewModel 
          return (T) new AssignmentsViewModel(singletonCImpl.provideDeliveryRepositoryProvider.get());

          case 1: // com.fooddelivery.deliverymobile.presentation.auth.AuthViewModel 
          return (T) new AuthViewModel(singletonCImpl.provideAuthRepositoryProvider.get());

          case 2: // com.fooddelivery.deliverymobile.presentation.dashboard.DashboardViewModel 
          return (T) new DashboardViewModel(singletonCImpl.provideDeliveryRepositoryProvider.get());

          case 3: // com.fooddelivery.deliverymobile.presentation.detail.DetailViewModel 
          return (T) new DetailViewModel(singletonCImpl.provideDeliveryRepositoryProvider.get());

          case 4: // com.fooddelivery.deliverymobile.presentation.history.HistoryViewModel 
          return (T) new HistoryViewModel(singletonCImpl.provideDeliveryRepositoryProvider.get());

          case 5: // com.fooddelivery.deliverymobile.presentation.workflow.WorkflowViewModel 
          return (T) new WorkflowViewModel(singletonCImpl.provideDeliveryRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends FoodDeliveryApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends FoodDeliveryApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends FoodDeliveryApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<TokenDataStore> tokenDataStoreProvider;

    private Provider<TokenProvider> provideTokenProvider;

    private Provider<AuthInterceptor> provideAuthInterceptorProvider;

    private Provider<ApiClient> apiClientProvider;

    private Provider<ApiService> provideApiServiceProvider;

    private Provider<DeliveryRepository> provideDeliveryRepositoryProvider;

    private Provider<AuthRepository> provideAuthRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.tokenDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<TokenDataStore>(singletonCImpl, 5));
      this.provideTokenProvider = DoubleCheck.provider(new SwitchingProvider<TokenProvider>(singletonCImpl, 4));
      this.provideAuthInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<AuthInterceptor>(singletonCImpl, 3));
      this.apiClientProvider = DoubleCheck.provider(new SwitchingProvider<ApiClient>(singletonCImpl, 2));
      this.provideApiServiceProvider = DoubleCheck.provider(new SwitchingProvider<ApiService>(singletonCImpl, 1));
      this.provideDeliveryRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<DeliveryRepository>(singletonCImpl, 0));
      this.provideAuthRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AuthRepository>(singletonCImpl, 6));
    }

    @Override
    public void injectFoodDeliveryApplication(FoodDeliveryApplication foodDeliveryApplication) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.fooddelivery.deliverymobile.data.repository.DeliveryRepository 
          return (T) RepositoryModule_ProvideDeliveryRepositoryFactory.provideDeliveryRepository(singletonCImpl.provideApiServiceProvider.get(), singletonCImpl.provideTokenProvider.get());

          case 1: // com.fooddelivery.deliverymobile.data.api.ApiService 
          return (T) NetworkModule_ProvideApiServiceFactory.provideApiService(singletonCImpl.apiClientProvider.get());

          case 2: // com.fooddelivery.deliverymobile.data.api.ApiClient 
          return (T) new ApiClient(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideAuthInterceptorProvider.get());

          case 3: // com.fooddelivery.deliverymobile.data.api.AuthInterceptor 
          return (T) NetworkModule_ProvideAuthInterceptorFactory.provideAuthInterceptor(singletonCImpl.provideTokenProvider.get());

          case 4: // com.fooddelivery.deliverymobile.data.local.TokenProvider 
          return (T) NetworkModule_ProvideTokenProviderFactory.provideTokenProvider(singletonCImpl.tokenDataStoreProvider.get());

          case 5: // com.fooddelivery.deliverymobile.data.local.TokenDataStore 
          return (T) new TokenDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // com.fooddelivery.deliverymobile.data.repository.AuthRepository 
          return (T) RepositoryModule_ProvideAuthRepositoryFactory.provideAuthRepository(singletonCImpl.provideApiServiceProvider.get(), singletonCImpl.provideTokenProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
