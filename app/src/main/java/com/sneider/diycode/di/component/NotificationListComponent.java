package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.di.module.NotificationListModule;
import com.sneider.diycode.mvp.ui.activity.NotificationListActivity;

import dagger.Component;

@ActivityScope
@Component(modules = NotificationListModule.class, dependencies = AppComponent.class)
public interface NotificationListComponent {

    void inject(NotificationListActivity activity);
}
