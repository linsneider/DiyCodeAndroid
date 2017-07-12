package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.di.module.ProjectDetailModule;
import com.sneider.diycode.mvp.ui.activity.ProjectDetailActivity;

import dagger.Component;

@ActivityScope
@Component(modules = ProjectDetailModule.class, dependencies = AppComponent.class)
public interface ProjectDetailComponent {

    void inject(ProjectDetailActivity activity);
}
