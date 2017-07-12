package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.di.module.UserListModule;
import com.sneider.diycode.mvp.ui.activity.UserListActivity;

import dagger.Component;

@ActivityScope
@Component(modules = UserListModule.class, dependencies = AppComponent.class)
public interface UserListComponent {

    void inject(UserListActivity activity);
}
