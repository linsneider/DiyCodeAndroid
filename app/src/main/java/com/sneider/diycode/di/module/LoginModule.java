package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.LoginContract;
import com.sneider.diycode.mvp.model.LoginModel;

import dagger.Module;
import dagger.Provides;

@Module
public class LoginModule {

    private LoginContract.View mView;

    public LoginModule(LoginContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    LoginContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    LoginContract.Model provideModel(LoginModel model) {
        return model;
    }
}
