package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.NewsListContract;
import com.sneider.diycode.mvp.model.NewsListModel;

import dagger.Module;
import dagger.Provides;

@Module
public class NewsListModule {

    private NewsListContract.View mView;

    public NewsListModule(NewsListContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    NewsListContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    NewsListContract.Model provideModel(NewsListModel model) {
        return model;
    }
}
