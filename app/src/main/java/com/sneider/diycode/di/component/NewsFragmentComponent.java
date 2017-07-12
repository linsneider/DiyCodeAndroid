package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;
import com.sneider.diycode.di.module.NewsFragmentModule;
import com.sneider.diycode.mvp.ui.fragment.NewsFragment;

import dagger.Component;

@FragmentScope
@Component(modules = NewsFragmentModule.class, dependencies = AppComponent.class)
public interface NewsFragmentComponent {

    void inject(NewsFragment fragment);
}
