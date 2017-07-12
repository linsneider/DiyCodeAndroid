package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.di.module.SettingModule;
import com.sneider.diycode.mvp.ui.activity.SettingActivity;

import dagger.Component;

@ActivityScope
@Component(modules = SettingModule.class, dependencies = AppComponent.class)
public interface SettingComponent {

    void inject(SettingActivity activity);
}
