package com.sneider.diycode.mvp.presenter;

import android.app.Application;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.PermissionUtil;
import com.sneider.diycode.app.ARouterPaths;
import com.sneider.diycode.mvp.contract.NewsFragmentContract;
import com.sneider.diycode.mvp.model.bean.News;
import com.sneider.diycode.mvp.ui.adapter.NewsListAdapter;
import com.sneider.diycode.utils.Constant;
import com.sneider.diycode.utils.DiycodeUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

import static com.sneider.diycode.app.ARouterPaths.NEWS_DETAIL;
import static com.sneider.diycode.app.ARouterPaths.USER_DETAIL;
import static com.sneider.diycode.mvp.ui.activity.NewsDetailActivity.EXTRA_NEWS;
import static com.sneider.diycode.mvp.ui.activity.NewsListActivity.EXTRA_NEWS_NODE_ID;
import static com.sneider.diycode.mvp.ui.activity.NewsListActivity.EXTRA_NEWS_NODE_NAME;
import static com.sneider.diycode.mvp.ui.activity.UserDetailActivity.EXTRA_USERNAME;

@FragmentScope
public class NewsFragmentPresenter extends BasePresenter<NewsFragmentContract.Model, NewsFragmentContract.View> {

    private Application mApplication;
    private AppManager mAppManager;
    private RxErrorHandler mErrorHandler;
    private List<News> mList = new ArrayList<>();
    private NewsListAdapter mAdapter;
    private int offset = 0;
    private boolean isFirst = true;

    @Inject
    public NewsFragmentPresenter(NewsFragmentContract.Model model, NewsFragmentContract.View rootView, RxErrorHandler handler, AppManager appManager, Application application) {
        super(model, rootView);
        mApplication = application;
        mAppManager = appManager;
        mErrorHandler = handler;
    }

    public void initAdapter() {
        if (mAdapter == null) {
            mAdapter = new NewsListAdapter(mList);
            mRootView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new NewsListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, News news) {
                    ARouter.getInstance().build(NEWS_DETAIL)
                            .withSerializable(EXTRA_NEWS, news)
                            .navigation();
                }

                @Override
                public void onNameClick(View view, String username) {
                    ARouter.getInstance().build(USER_DETAIL)
                            .withString(EXTRA_USERNAME, username)
                            .navigation();
                }

                @Override
                public void onNodeNameClick(View view, String nodeName, int nodeId) {
                    ARouter.getInstance().build(ARouterPaths.NEWS_LIST)
                            .withString(EXTRA_NEWS_NODE_NAME, nodeName)
                            .withInt(EXTRA_NEWS_NODE_ID, nodeId)
                            .navigation();
                }

                @Override
                public void onNewsClick(View view, String url) {
                    DiycodeUtils.openWebActivity(url);
                }
            });
        }
    }

    public void getNews(boolean isRefresh) {
        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
            }

            @Override
            public void onRequestPermissionFailure() {
            }
        }, mRootView.getRxPermissions(), mErrorHandler);
        if (isRefresh) offset = 0;
        boolean isEvictCache = true;
//        if (isRefresh && isFirst) {
//            isFirst = false;
//            isEvictCache = false;
//        }
        mModel.getNews(offset, isEvictCache)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> {
                    if (isRefresh) mRootView.showLoading();
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    if (isRefresh) mRootView.hideLoading();
                })
//                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<List<News>>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        mRootView.onLoadMoreError();
                    }

                    @Override
                    public void onNext(@NonNull List<News> data) {
                        mRootView.onLoadMoreComplete();
                        if (offset == 0) mList.clear();
                        mList.addAll(data);
                        mAdapter.notifyDataSetChanged();
                        offset = mAdapter.getItemCount();
                        if (data.size() < Constant.PAGE_SIZE) mRootView.onLoadMoreEnd();
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter = null;
        mList = null;
        mErrorHandler = null;
        mAppManager = null;
        mApplication = null;
    }
}
