package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.TopicListContract;
import com.sneider.diycode.mvp.model.TopicListModel;

import dagger.Module;
import dagger.Provides;

@Module
public class TopicListModule {

    private TopicListContract.View mView;

    public TopicListModule(TopicListContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    TopicListContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    TopicListContract.Model provideModel(TopicListModel model) {
        return model;
    }
}
