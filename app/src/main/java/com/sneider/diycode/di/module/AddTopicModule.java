package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.AddTopicContract;
import com.sneider.diycode.mvp.model.AddTopicModel;

import dagger.Module;
import dagger.Provides;

@Module
public class AddTopicModule {

    private AddTopicContract.View mView;

    public AddTopicModule(AddTopicContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    AddTopicContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    AddTopicContract.Model provideModel(AddTopicModel model) {
        return model;
    }
}
