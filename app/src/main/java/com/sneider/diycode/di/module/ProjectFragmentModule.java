package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.FragmentScope;
import com.sneider.diycode.mvp.contract.ProjectFragmentContract;
import com.sneider.diycode.mvp.model.ProjectFragmentModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ProjectFragmentModule {

    private ProjectFragmentContract.View mView;

    public ProjectFragmentModule(ProjectFragmentContract.View view) {
        mView = view;
    }

    @FragmentScope
    @Provides
    ProjectFragmentContract.View provideView() {
        return mView;
    }

    @FragmentScope
    @Provides
    ProjectFragmentContract.Model provideModel(ProjectFragmentModel model) {
        return model;
    }
}
