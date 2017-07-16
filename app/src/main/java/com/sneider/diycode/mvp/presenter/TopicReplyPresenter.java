package com.sneider.diycode.mvp.presenter;

import android.app.Application;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ToastUtils;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.PermissionUtil;
import com.sneider.diycode.R;
import com.sneider.diycode.mvp.contract.TopicReplyContract;
import com.sneider.diycode.mvp.model.bean.Reply;
import com.sneider.diycode.mvp.model.bean.Topic;
import com.sneider.diycode.mvp.ui.adapter.TopicReplyAdapter;
import com.sneider.diycode.utils.Constant;
import com.sneider.diycode.utils.DiycodeUtils;
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
import static com.sneider.diycode.app.ARouterPaths.REPLY_ADD;
import static com.sneider.diycode.app.ARouterPaths.TOPIC_DETAIL;
import static com.sneider.diycode.app.ARouterPaths.USER_DETAIL;
import static com.sneider.diycode.mvp.ui.activity.AddReplyActivity.EXTRA_DATA;
import static com.sneider.diycode.mvp.ui.activity.AddReplyActivity.EXTRA_REPLY_FLOOR;
import static com.sneider.diycode.mvp.ui.activity.AddReplyActivity.EXTRA_REPLY_ID;
import static com.sneider.diycode.mvp.ui.activity.AddReplyActivity.EXTRA_REPLY_TYPE;
import static com.sneider.diycode.mvp.ui.activity.AddReplyActivity.EXTRA_REPLY_USER;
import static com.sneider.diycode.mvp.ui.activity.AddReplyActivity.TYPE_TOPIC;
import static com.sneider.diycode.mvp.ui.activity.PhotoActivity.EXTRA_PHOTO_URL;
import static com.sneider.diycode.mvp.ui.activity.TopicDetailActivity.EXTRA_TOPIC_ID;
import static com.sneider.diycode.mvp.ui.activity.UserDetailActivity.EXTRA_USERNAME;

@ActivityScope
public class TopicReplyPresenter extends BasePresenter<TopicReplyContract.Model, TopicReplyContract.View> {

    private Application mApplication;
    private AppManager mAppManager;
    private RxErrorHandler mErrorHandler;
    private List<Reply> mList = new ArrayList<>();
    private TopicReplyAdapter mAdapter;
    private int offset = 0;
    private boolean isFirst = true;

    @Inject
    public TopicReplyPresenter(TopicReplyContract.Model model, TopicReplyContract.View rootView, RxErrorHandler handler, AppManager appManager, Application application) {
        super(model, rootView);
        mApplication = application;
        mAppManager = appManager;
        mErrorHandler = handler;
    }

    public void initAdapter(Topic topic) {
        if (mAdapter == null) {
            mAdapter = new TopicReplyAdapter(mList);
            mRootView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new TopicReplyAdapter.OnItemClickListener() {
                @Override
                public void onUserClick(View view, String username) {
                    ARouter.getInstance().build(USER_DETAIL)
                            .withString(EXTRA_USERNAME, username)
                            .navigation();
                }

                @Override
                public void onEditReplyClick(View view, Reply reply) {
                    if (DiycodeUtils.checkToken(mApplication)) {
                        ARouter.getInstance().build(REPLY_ADD)
                                .withInt(EXTRA_REPLY_TYPE, TYPE_TOPIC)
                                .withSerializable(EXTRA_DATA, topic)
                                .withInt(EXTRA_REPLY_ID, reply.getId())
                                .navigation();
                    }
                }

                @Override
                public void onLikeReplyClick(View view, Reply reply) {
                    ToastUtils.showShort(R.string.no_implement);
                }

                @Override
                public void onReplyClick(View view, Reply reply, int floor) {
                    if (DiycodeUtils.checkToken(mApplication)) {
                        ARouter.getInstance().build(REPLY_ADD)
                                .withInt(EXTRA_REPLY_TYPE, TYPE_TOPIC)
                                .withSerializable(EXTRA_DATA, topic)
                                .withString(EXTRA_REPLY_USER, reply.getUser().getLogin())
                                .withInt(EXTRA_REPLY_FLOOR, floor)
                                .navigation();
                    }
                }
            });
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
                    } else if (url.startsWith("#reply")) {
                        String floorStr = url.substring(6);
                        try {
                            int floor = Integer.parseInt(floorStr);
                            mRootView.smoothToPosition(floor - 1);
                        } catch (NumberFormatException e) {

                        }
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

    public void getTopicReplies(int id, boolean isRefresh) {
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
        mModel.getTopicReplies(id, offset, isEvictCache)
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
