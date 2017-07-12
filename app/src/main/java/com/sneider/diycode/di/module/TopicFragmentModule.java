package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.FragmentScope;
import com.sneider.diycode.mvp.contract.TopicFragmentContract;
import com.sneider.diycode.mvp.model.TopicFragmentModel;

import dagger.Module;
import dagger.Provides;

@Module
public class TopicFragmentModule {

    private TopicFragmentContract.View mView;

    public TopicFragmentModule(TopicFragmentContract.View view) {
        mView = view;
    }

    @FragmentScope
    @Provides
    TopicFragmentContract.View provideView() {
        return mView;
    }

    @FragmentScope
    @Provides
    TopicFragmentContract.Model provideModel(TopicFragmentModel model) {
        return model;
    }
}
