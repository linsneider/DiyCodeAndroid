package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.di.module.AddTopicModule;
import com.sneider.diycode.mvp.ui.activity.AddTopicActivity;

import dagger.Component;

@ActivityScope
@Component(modules = AddTopicModule.class, dependencies = AppComponent.class)
public interface AddTopicComponent {

    void inject(AddTopicActivity activity);
}
