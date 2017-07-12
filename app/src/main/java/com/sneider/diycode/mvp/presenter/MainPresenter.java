package com.sneider.diycode.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.sneider.diycode.event.GetUnreadCountEvent;
import com.sneider.diycode.mvp.contract.MainContract;
import com.sneider.diycode.mvp.model.bean.Count;

import org.simple.eventbus.EventBus;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

@ActivityScope
public class MainPresenter extends BasePresenter<MainContract.Model, MainContract.View> {

    private Application mApplication;
    private AppManager mAppManager;
    private RxErrorHandler mErrorHandler;

    @Inject
    public MainPresenter(MainContract.Model model, MainContract.View rootView, RxErrorHandler handler, AppManager appManager, Application application) {
        super(model, rootView);
        this.mApplication = application;
        this.mAppManager = appManager;
        this.mErrorHandler = handler;
    }

    public void getUnreadCount() {
        mModel.getUnreadCount()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorHandleSubscriber<Count>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull Count data) {
                        EventBus.getDefault().post(new GetUnreadCountEvent(data.getCount() > 0 ? true : false));
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mErrorHandler = null;
        mAppManager = null;
        mApplication = null;
    }
}
