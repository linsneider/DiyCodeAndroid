package com.sneider.diycode.mvp.presenter;

import android.app.Application;

import com.blankj.utilcode.util.ToastUtils;
import com.jess.arms.base.App;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.PermissionUtil;
import com.sneider.diycode.R;
import com.sneider.diycode.mvp.contract.AddNewsContract;
import com.sneider.diycode.mvp.model.bean.News;
import com.sneider.diycode.mvp.model.bean.NewsNode;
import com.sneider.diycode.utils.PrefUtils;
import com.sneider.diycode.utils.RxUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

@ActivityScope
public class AddNewsPresenter extends BasePresenter<AddNewsContract.Model, AddNewsContract.View> {

    private Application mApplication;
    private AppManager mAppManager;
    private RxErrorHandler mErrorHandler;

    @Inject
    public AddNewsPresenter(AddNewsContract.Model model, AddNewsContract.View rootView, RxErrorHandler handler, AppManager appManager, Application application) {
        super(model, rootView);
        this.mApplication = application;
        this.mAppManager = appManager;
        this.mErrorHandler = handler;
    }

    public void createNews(String title, String link, int nodeId) {
        mModel.createNews(title, link, nodeId)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> mRootView.hideLoading())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<News>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort(R.string.add_failed);
                    }

                    @Override
                    public void onNext(@NonNull News news) {
                        ToastUtils.showShort(R.string.add_success);
                        mRootView.killMyself();
                    }
                });
    }

    public void getNewsNodes(boolean isRefresh) {
        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
            }

            @Override
            public void onRequestPermissionFailure() {
            }
        }, mRootView.getRxPermissions(), mErrorHandler);
        boolean isEvictCache = isRefresh;
        mModel.getNewsNodes(isEvictCache)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<List<NewsNode>>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull List<NewsNode> data) {
                        mRootView.onGetNodes(data);
                        // 缓存
                        AppComponent appComponent = ((App) mApplication).getAppComponent();
                        PrefUtils.getInstance(mApplication).put("news_nodes", appComponent.gson().toJson(data));
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
