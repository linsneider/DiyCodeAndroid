package com.sneider.diycode.mvp.presenter;

import android.app.Application;

import com.blankj.utilcode.util.ToastUtils;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.sneider.diycode.R;
import com.sneider.diycode.mvp.contract.TopicDetailContract;
import com.sneider.diycode.mvp.model.bean.Like;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.Topic;
import com.sneider.diycode.utils.RxUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

@ActivityScope
public class TopicDetailPresenter extends BasePresenter<TopicDetailContract.Model, TopicDetailContract.View> {

    private Application mApplication;
    private AppManager mAppManager;
    private RxErrorHandler mErrorHandler;

    @Inject
    public TopicDetailPresenter(TopicDetailContract.Model model, TopicDetailContract.View rootView, RxErrorHandler handler, AppManager appManager, Application application) {
        super(model, rootView);
        this.mApplication = application;
        this.mAppManager = appManager;
        this.mErrorHandler = handler;
    }

    public void getTopicDetail(int id) {
        mModel.getTopicDetail(id, true)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> mRootView.hideLoading())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Topic>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        mRootView.setLayout(false);
                    }

                    @Override
                    public void onNext(@NonNull Topic data) {
                        mRootView.setLayout(true);
                        mRootView.onGetTopicDetail(data);
                    }
                });
    }

    public void favoriteTopic(int id) {
        mModel.favoriteTopic(id)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Ok>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort(R.string.favorite_failed);
                    }

                    @Override
                    public void onNext(@NonNull Ok data) {
                        mRootView.onFavoriteTopic();
                    }
                });
    }

    public void unfavoriteTopic(int id) {
        mModel.unfavoriteTopic(id)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Ok>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort(R.string.unfavorite_failed);
                    }

                    @Override
                    public void onNext(@NonNull Ok data) {
                        mRootView.onFavoriteTopic();
                    }
                });
    }

    public void likeTopic(int id) {
        mModel.likeTopic(id)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Like>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort(R.string.like_failed);
                    }

                    @Override
                    public void onNext(@NonNull Like data) {
                        mRootView.onLikeTopic(data);
                    }
                });
    }

    public void unlikeTopic(int id) {
        mModel.unlikeTopic(id)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Like>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort(R.string.unlike_failed);
                    }

                    @Override
                    public void onNext(@NonNull Like data) {
                        mRootView.onLikeTopic(data);
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
