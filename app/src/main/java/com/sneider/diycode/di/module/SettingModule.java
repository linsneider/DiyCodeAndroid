package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.SettingContract;
import com.sneider.diycode.mvp.model.SettingModel;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingModule {

    private SettingContract.View mView;

    public SettingModule(SettingContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    SettingContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    SettingContract.Model provideModel(SettingModel model) {
        return model;
    }
}
