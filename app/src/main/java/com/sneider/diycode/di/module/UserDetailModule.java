package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.UserDetailContract;
import com.sneider.diycode.mvp.model.UserDetailModel;

import dagger.Module;
import dagger.Provides;

@Module
public class UserDetailModule {

    private UserDetailContract.View mView;

    public UserDetailModule(UserDetailContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    UserDetailContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    UserDetailContract.Model provideModel(UserDetailModel model) {
        return model;
    }
}
