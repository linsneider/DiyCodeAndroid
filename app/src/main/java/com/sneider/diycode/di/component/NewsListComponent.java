package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.di.module.NewsListModule;
import com.sneider.diycode.mvp.ui.activity.NewsListActivity;

import dagger.Component;

@ActivityScope
@Component(modules = NewsListModule.class, dependencies = AppComponent.class)
public interface NewsListComponent {

    void inject(NewsListActivity activity);
}
