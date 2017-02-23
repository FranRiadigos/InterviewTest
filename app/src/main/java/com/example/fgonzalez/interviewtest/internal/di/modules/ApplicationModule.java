package com.example.fgonzalez.interviewtest.internal.di.modules;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.example.fgonzalez.data.repository.PostRepositoryImpl;
import com.example.fgonzalez.data.repository.UserRepositoryImpl;
import com.example.fgonzalez.data.service.adapter.RestFactory;
import com.example.fgonzalez.data.service.adapter.RetrofitFactory;
import com.example.fgonzalez.domain.executor.ThreadExecutor;
import com.example.fgonzalez.domain.repository.PostRepository;
import com.example.fgonzalez.domain.repository.UserRepository;
import com.example.fgonzalez.interviewtest.AndroidApplication;
import com.example.fgonzalez.interviewtest.BuildConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.support.annotation.VisibleForTesting.PACKAGE_PRIVATE;

@Module
public class ApplicationModule {
    private final AndroidApplication application;

    public ApplicationModule(AndroidApplication application) {
        this.application = application;
    }

    @Provides @Singleton
    Context provideApplicationContext() {
        return this.application;
    }

    @Provides @Singleton @Named("SubscriberThread")
    ThreadExecutor provideSubscriberThread() {
        return Schedulers::io;
    }

    @Provides @Singleton @Named("ObserverThread")
    ThreadExecutor provideObserverThread() {
        return AndroidSchedulers::mainThread;
    }

    @VisibleForTesting(otherwise = PACKAGE_PRIVATE)
    @Provides @Singleton @Named("ConcurrentExecutor")
    public ThreadExecutor provideConcurrentExecutor(@Named("AvailableProcessors") int threads) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        return () -> Schedulers.from(executor);
    }

    @Provides @Singleton @Named("AvailableProcessors")
    int provideAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors() + 1;
    }

    @Provides @Singleton
    RestFactory provideRestFactory(RetrofitFactory factory) {
        return factory;
    }

    @Provides @Singleton @Named("ApiUrl")
    String provideApiUrl() {
        return BuildConfig.API_URL;
    }

    @Provides @Singleton
    PostRepository providePostRepository(PostRepositoryImpl postRepository) {
        return postRepository;
    }

    @Provides @Singleton
    UserRepository provideUserRepository(UserRepositoryImpl userRepository) {
        return userRepository;
    }
}
