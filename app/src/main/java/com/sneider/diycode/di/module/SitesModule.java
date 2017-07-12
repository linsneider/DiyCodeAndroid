package com.sneider.diycode.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.sneider.diycode.mvp.contract.SitesContract;
import com.sneider.diycode.mvp.model.SitesModel;

import dagger.Module;
import dagger.Provides;

@Module
public class SitesModule {

    private SitesContract.View mView;

    public SitesModule(SitesContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    SitesContract.View provideView() {
        return mView;
    }

    @ActivityScope
    @Provides
    SitesContract.Model provideModel(SitesModel model) {
        return model;
    }
}
