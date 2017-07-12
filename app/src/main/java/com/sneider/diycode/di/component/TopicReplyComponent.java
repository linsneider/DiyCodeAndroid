package com.sneider.diycode.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.di.module.TopicReplyModule;
import com.sneider.diycode.mvp.ui.activity.TopicReplyActivity;

import dagger.Component;

@ActivityScope
@Component(modules = TopicReplyModule.class, dependencies = AppComponent.class)
public interface TopicReplyComponent {

    void inject(TopicReplyActivity activity);
}
