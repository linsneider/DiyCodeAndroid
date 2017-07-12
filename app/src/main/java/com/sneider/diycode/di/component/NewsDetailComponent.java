package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.di.module.NewsDetailModule;
import com.sneider.diycode.mvp.ui.activity.NewsDetailActivity;

import dagger.Component;

@ActivityScope
@Component(modules = NewsDetailModule.class, dependencies = AppComponent.class)
public interface NewsDetailComponent {

    void inject(NewsDetailActivity activity);
}
