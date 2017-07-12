package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.MainContract;
import com.sneider.diycode.mvp.model.MainModel;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {

    private MainContract.View mView;

    public MainModule(MainContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    MainContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    MainContract.Model provideModel(MainModel model) {
        return model;
    }
}
