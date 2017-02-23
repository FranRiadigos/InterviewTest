package com.example.fgonzalez.interviewtest.internal.di.components;

import com.example.fgonzalez.interviewtest.feature.post.PostsFragment;
import com.example.fgonzalez.interviewtest.feature.post.detail.PostDetailsFragment;
import com.example.fgonzalez.interviewtest.internal.di.PerFragment;
import com.example.fgonzalez.interviewtest.internal.di.modules.FragmentModule;

import dagger.Component;

@PerFragment
@Component(dependencies = ApplicationComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {
    void inject(PostsFragment fragment);
    void inject(PostDetailsFragment fragment);
}
