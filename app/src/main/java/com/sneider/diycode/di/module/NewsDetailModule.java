package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.NewsDetailContract;
import com.sneider.diycode.mvp.model.NewsDetailModel;

import dagger.Module;
import dagger.Provides;

@Module
public class NewsDetailModule {

    private NewsDetailContract.View mView;

    public NewsDetailModule(NewsDetailContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    NewsDetailContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    NewsDetailContract.Model provideModel(NewsDetailModel model) {
        return model;
    }
}
