package com.sneider.diycode.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.sneider.diycode.mvp.contract.SettingContract;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

@ActivityScope
public class SettingPresenter extends BasePresenter<SettingContract.Model, SettingContract.View> {

    private Application mApplication;
    private AppManager mAppManager;
    private RxErrorHandler mErrorHandler;

    @Inject
    public SettingPresenter(SettingContract.Model model, SettingContract.View rootView, RxErrorHandler handler, AppManager appManager, Application application) {
        super(model, rootView);
        mApplication = application;
        mAppManager = appManager;
        mErrorHandler = handler;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mErrorHandler = null;
        mAppManager = null;
        mApplication = null;
    }
}
