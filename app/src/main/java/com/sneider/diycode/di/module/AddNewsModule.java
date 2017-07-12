package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.AddNewsContract;
import com.sneider.diycode.mvp.model.AddNewsModel;

import dagger.Module;
import dagger.Provides;

@Module
public class AddNewsModule {

    private AddNewsContract.View mView;

    public AddNewsModule(AddNewsContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    AddNewsContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    AddNewsContract.Model provideModel(AddNewsModel model) {
        return model;
    }
}
