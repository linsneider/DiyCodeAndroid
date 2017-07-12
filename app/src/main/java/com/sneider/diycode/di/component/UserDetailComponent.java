package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.di.module.UserDetailModule;
import com.sneider.diycode.mvp.ui.activity.UserDetailActivity;

import dagger.Component;

@ActivityScope
@Component(modules = UserDetailModule.class, dependencies = AppComponent.class)
public interface UserDetailComponent {

    void inject(UserDetailActivity activity);
}
