package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.UserListContract;
import com.sneider.diycode.mvp.model.UserListModel;

import dagger.Module;
import dagger.Provides;

@Module
public class UserListModule {

    private UserListContract.View mView;

    public UserListModule(UserListContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    UserListContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    UserListContract.Model provideModel(UserListModel model) {
        return model;
    }
}
