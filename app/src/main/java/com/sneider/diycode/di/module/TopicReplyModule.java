package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.TopicReplyContract;
import com.sneider.diycode.mvp.model.TopicReplyModel;

import dagger.Module;
import dagger.Provides;

@Module
public class TopicReplyModule {

    private TopicReplyContract.View mView;

    public TopicReplyModule(TopicReplyContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    TopicReplyContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    TopicReplyContract.Model provideModel(TopicReplyModel model) {
        return model;
    }
}
