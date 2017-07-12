package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;
import com.sneider.diycode.di.module.TopicFragmentModule;
import com.sneider.diycode.mvp.ui.fragment.TopicFragment;

import dagger.Component;

@FragmentScope
@Component(modules = TopicFragmentModule.class, dependencies = AppComponent.class)
public interface TopicFragmentComponent {

    void inject(TopicFragment fragment);
}
