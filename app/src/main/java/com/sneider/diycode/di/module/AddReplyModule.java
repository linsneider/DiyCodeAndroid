package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.AddReplyContract;
import com.sneider.diycode.mvp.model.AddReplyModel;

import dagger.Module;
import dagger.Provides;

@Module
public class AddReplyModule {

    private AddReplyContract.View mView;

    public AddReplyModule(AddReplyContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    AddReplyContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    AddReplyContract.Model provideModel(AddReplyModel model) {
        return model;
    }
}
