package com.example.fgonzalez.interviewtest.internal.di.components;

import android.content.Context;

import com.example.fgonzalez.domain.executor.ThreadExecutor;
import com.example.fgonzalez.domain.repository.PostRepository;
import com.example.fgonzalez.domain.repository.UserRepository;
import com.example.fgonzalez.interviewtest.DispatcherActivity;
import com.example.fgonzalez.interviewtest.internal.di.modules.ApplicationModule;
import com.example.fgonzalez.interviewtest.util.Navigator;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component( modules = ApplicationModule.class )
public interface ApplicationComponent {
    void inject(DispatcherActivity activity);
    Context context();
    Navigator navigator();
    @Named("SubscriberThread") ThreadExecutor provideSubscriberThread();
    @Named("ObserverThread") ThreadExecutor provideObserverThread();
    @Named("ConcurrentExecutor") ThreadExecutor provideConcurrentExecutor();
    @Named("AvailableProcessors") int provideAvailableProcessors();
    PostRepository postRepository();
    UserRepository userRepository();
}
