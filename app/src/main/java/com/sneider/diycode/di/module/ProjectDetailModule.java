package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.ProjectDetailContract;
import com.sneider.diycode.mvp.model.ProjectDetailModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ProjectDetailModule {

    private ProjectDetailContract.View mView;

    public ProjectDetailModule(ProjectDetailContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    ProjectDetailContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    ProjectDetailContract.Model provideModel(ProjectDetailModel model) {
        return model;
    }
}
