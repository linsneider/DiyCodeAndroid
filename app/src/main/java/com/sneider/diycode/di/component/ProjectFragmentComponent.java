package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;
import com.sneider.diycode.di.module.ProjectFragmentModule;
import com.sneider.diycode.mvp.ui.fragment.ProjectFragment;

import dagger.Component;

@FragmentScope
@Component(modules = ProjectFragmentModule.class, dependencies = AppComponent.class)
public interface ProjectFragmentComponent {

    void inject(ProjectFragment fragment);
}
