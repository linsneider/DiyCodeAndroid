package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.NotificationListContract;
import com.sneider.diycode.mvp.model.NotificationListModel;

import dagger.Module;
import dagger.Provides;

@Module
public class NotificationListModule {

    private NotificationListContract.View mView;

    public NotificationListModule(NotificationListContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    NotificationListContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    NotificationListContract.Model provideModel(NotificationListModel model) {
        return model;
    }
}
