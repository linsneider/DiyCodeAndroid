package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.di.module.SitesModule;
import com.sneider.diycode.mvp.ui.activity.SitesActivity;

import dagger.Component;

@ActivityScope
@Component(modules = SitesModule.class, dependencies = AppComponent.class)
public interface SitesComponent {

    void inject(SitesActivity activity);
}
