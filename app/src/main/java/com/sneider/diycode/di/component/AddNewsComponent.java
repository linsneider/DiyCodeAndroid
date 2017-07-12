package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.di.module.AddNewsModule;
import com.sneider.diycode.mvp.ui.activity.AddNewsActivity;

import dagger.Component;

@ActivityScope
@Component(modules = AddNewsModule.class, dependencies = AppComponent.class)
public interface AddNewsComponent {

    void inject(AddNewsActivity activity);
}
