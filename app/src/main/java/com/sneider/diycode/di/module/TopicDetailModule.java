package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.TopicDetailContract;
import com.sneider.diycode.mvp.model.TopicDetailModel;

import dagger.Module;
import dagger.Provides;

@Module
public class TopicDetailModule {

    private TopicDetailContract.View mView;

    public TopicDetailModule(TopicDetailContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    TopicDetailContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    TopicDetailContract.Model provideModel(TopicDetailModel model) {
        return model;
    }
}
