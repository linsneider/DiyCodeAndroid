package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.di.module.ReplyListModule;
import com.sneider.diycode.mvp.ui.activity.ReplyListActivity;

import dagger.Component;

@ActivityScope
@Component(modules = ReplyListModule.class, dependencies = AppComponent.class)
public interface ReplyListComponent {

    void inject(ReplyListActivity activity);
}
