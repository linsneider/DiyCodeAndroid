package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.di.module.TopicDetailModule;
import com.sneider.diycode.mvp.ui.activity.TopicDetailActivity;

import dagger.Component;

@ActivityScope
@Component(modules = TopicDetailModule.class, dependencies = AppComponent.class)
public interface TopicDetailComponent {

    void inject(TopicDetailActivity activity);
}
