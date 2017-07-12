package com.sneider.diycode.mvp.presenter;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.PermissionUtil;
import com.sneider.diycode.mvp.contract.ReplyListContract;
import com.sneider.diycode.mvp.model.bean.Reply;
import com.sneider.diycode.mvp.ui.adapter.ReplyListAdapter;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.Constant;
import com.sneider.diycode.utils.RxUtils;
import com.sneider.diycode.utils.html.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

import static com.sneider.diycode.app.ARouterPaths.PUBLIC_PHOTO;
import static com.sneider.diycode.app.ARouterPaths.TOPIC_DETAIL;
import static com.sneider.diycode.app.ARouterPaths.USER_DETAIL;
import static com.sneider.diycode.mvp.ui.activity.PhotoActivity.EXTRA_PHOTO_URL;
import static com.sneider.diycode.mvp.ui.activity.TopicDetailActivity.EXTRA_TOPIC_ID;
import static com.sneider.diycode.mvp.ui.activity.UserDetailActivity.EXTRA_USERNAME;

@ActivityScope
public class ReplyListPresenter extends BasePresenter<ReplyListContract.Model, ReplyListContract.View> {

    private Application mApplication;
    private AppManager mAppManager;
    private RxErrorHandler mErrorHandler;
    private List<Reply> mList = new ArrayList<>();
    private ReplyListAdapter mAdapter;
    private int offset = 0;
    private boolean isFirst = true;

    @Inject
    public ReplyListPresenter(ReplyListContract.Model model, ReplyListContract.View rootView, RxErrorHandler handler, AppManager appManager, Application application) {
        super(model, rootView);
        mApplication = application;
        mAppManager = appManager;
        mErrorHandler = handler;
    }

    public void initAdapter() {
        if (mAdapter == null) {
            mAdapter = new ReplyListAdapter(mList);
            mRootView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener((view, data) ->
                    ARouter.getInstance().build(TOPIC_DETAIL)
                            .withInt(EXTRA_TOPIC_ID, data.getTopic_id())
                            .navigation());
            mAdapter.setCallback(new HtmlUtils.Callback() {
                @Override
                public void clickUrl(String url) {
                    if (url.contains("http")) {
                        if (url.startsWith("https://www.diycode.cc/topics/")) {
                            ARouter.getInstance().build(TOPIC_DETAIL)
                                    .withInt(EXTRA_TOPIC_ID, Integer.valueOf(url.substring(30)))
                                    .navigation();
                            return;
                        }
                        DiycodeUtils.openWebActivity(url);
                    } else if (url.startsWith("/")) {
                        ARouter.getInstance().build(USER_DETAIL)
                                .withString(EXTRA_USERNAME, url.substring(1))
                                .navigation();
                    }
                }

                @Override
                public void clickImage(String source) {
                    ARouter.getInstance().build(PUBLIC_PHOTO)
                            .withString(EXTRA_PHOTO_URL, source)
                            .navigation();
                }
            });
        }
    }

    public void getUserReplies(String username, boolean isRefresh) {
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
        mModel.getUserReplies(username, offset, isEvictCache)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> {
                    if (isRefresh) mRootView.showLoading();
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    if (isRefresh) mRootView.hideLoading();
                })
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<List<Reply>>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        mRootView.onLoadMoreError();
                    }

                    @Override
                    public void onNext(@NonNull List<Reply> data) {
                        mRootView.onLoadMoreComplete();
                        if (offset == 0) mList.clear();
                        mList.addAll(data);
                        mAdapter.notifyDataSetChanged();
                        offset = mAdapter.getItemCount();
                        if (data.size() < Constant.PAGE_SIZE) mRootView.onLoadMoreEnd();
                        mRootView.setEmpty(mList.isEmpty());
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
