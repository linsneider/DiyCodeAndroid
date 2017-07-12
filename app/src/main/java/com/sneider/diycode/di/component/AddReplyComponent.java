package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.di.module.AddReplyModule;
import com.sneider.diycode.mvp.ui.activity.AddReplyActivity;

import dagger.Component;

@ActivityScope
@Component(modules = AddReplyModule.class, dependencies = AppComponent.class)
public interface AddReplyComponent {

    void inject(AddReplyActivity activity);
}
