package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.di.module.TopicListModule;
import com.sneider.diycode.mvp.ui.activity.TopicListActivity;

import dagger.Component;

@ActivityScope
@Component(modules = TopicListModule.class, dependencies = AppComponent.class)
public interface TopicListComponent {

    void inject(TopicListActivity activity);
}
