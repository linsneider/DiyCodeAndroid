package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.FragmentScope;
import com.sneider.diycode.mvp.contract.NewsFragmentContract;
import com.sneider.diycode.mvp.model.NewsFragmentModel;

import dagger.Module;
import dagger.Provides;

@Module
public class NewsFragmentModule {

    private NewsFragmentContract.View mView;

    public NewsFragmentModule(NewsFragmentContract.View view) {
        mView = view;
    }

    @FragmentScope
    @Provides
    NewsFragmentContract.View provideView() {
        return mView;
    }

    @FragmentScope
    @Provides
    NewsFragmentContract.Model provideModel(NewsFragmentModel model) {
        return model;
    }
}
