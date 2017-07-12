package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.ReplyListContract;
import com.sneider.diycode.mvp.model.ReplyListModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ReplyListModule {

    private ReplyListContract.View mView;

    public ReplyListModule(ReplyListContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    ReplyListContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    ReplyListContract.Model provideModel(ReplyListModel model) {
        return model;
    }
}
