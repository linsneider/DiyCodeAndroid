package com.sneider.diycode.mvp.presenter;

import android.app.Application;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ToastUtils;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.PermissionUtil;
import com.sneider.diycode.R;
import com.sneider.diycode.event.GetUnreadCountEvent;
import com.sneider.diycode.mvp.contract.NotificationListContract;
import com.sneider.diycode.mvp.model.bean.Count;
import com.sneider.diycode.mvp.model.bean.Notification;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.ui.adapter.NotificationListAdapter;
import com.sneider.diycode.utils.Constant;
import com.sneider.diycode.utils.RxUtils;

import org.simple.eventbus.EventBus;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

import static com.sneider.diycode.app.ARouterPaths.TOPIC_DETAIL;
import static com.sneider.diycode.app.ARouterPaths.USER_DETAIL;
import static com.sneider.diycode.mvp.ui.activity.TopicDetailActivity.EXTRA_TOPIC_ID;
import static com.sneider.diycode.mvp.ui.activity.UserDetailActivity.EXTRA_USERNAME;

@ActivityScope
public class NotificationListPresenter extends BasePresenter<NotificationListContract.Model, NotificationListContract.View> {

    private Application mApplication;
    private AppManager mAppManager;
    private RxErrorHandler mErrorHandler;
    private List<Notification> mList = new ArrayList<>();
    private NotificationListAdapter mAdapter;
    private int offset = 0;
    private boolean isFirst = true;

    @Inject
    public NotificationListPresenter(NotificationListContract.Model model, NotificationListContract.View rootView, RxErrorHandler handler, AppManager appManager, Application application) {
        super(model, rootView);
        this.mApplication = application;
        this.mAppManager = appManager;
        this.mErrorHandler = handler;
    }

    public void initAdapter() {
        if (mAdapter == null) {
            mAdapter = new NotificationListAdapter(mList);
            mRootView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new NotificationListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, Notification notification) {
                    // int[] ids = new int[]{data.getId()};
                    // readNotification(ids);
                    getUnreadCount();
                    switch (notification.getType()) {
                        case "TopicReply":
                            ARouter.getInstance().build(TOPIC_DETAIL)
                                    .withInt(EXTRA_TOPIC_ID, notification.getReply().getTopic_id())
                                    .navigation();
                            break;
                        case "Mention":
                            ARouter.getInstance().build(TOPIC_DETAIL)
                                    .withInt(EXTRA_TOPIC_ID, notification.getMention().getTopic_id())
                                    .navigation();
                            break;
                        case "Topic":
                        case "NodeChanged":
                            ARouter.getInstance().build(TOPIC_DETAIL)
                                    .withInt(EXTRA_TOPIC_ID, notification.getTopic().getId())
                                    .navigation();
                            break;
                        case "Hacknews":
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onNameClick(View view, String username) {
                    ARouter.getInstance().build(USER_DETAIL)
                            .withString(EXTRA_USERNAME, username)
                            .navigation();
                }
            });
            mAdapter.setOnItemLongClickListener((view, notification) ->
                    new MaterialDialog.Builder(mAppManager.getCurrentActivity()).items(R.array.delete)
                            .itemsCallback((dialog, itemView, position, text) ->
                                    deleteNotification(notification)).show());
        }
    }

    public void getNotifications(boolean isRefresh) {
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
        mModel.getNotifications(offset, isEvictCache)
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
                .subscribe(new ErrorHandleSubscriber<List<Notification>>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        mRootView.onLoadMoreError();
                    }

                    @Override
                    public void onNext(@NonNull List<Notification> data) {
                        mRootView.onLoadMoreComplete();
                        if (offset == 0) {
                            mList.clear();
                            getUnreadCount();
                        }
                        mList.addAll(data);
                        mAdapter.notifyDataSetChanged();
                        offset = mAdapter.getItemCount();
                        if (data.size() < Constant.PAGE_SIZE) mRootView.onLoadMoreEnd();
                        mRootView.setEmpty(mList.isEmpty());
                    }
                });
    }

    private void deleteNotification(Notification notification) {
        mModel.deleteNotification(notification.getId())
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Ok>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort(R.string.delete_failed);
                    }

                    @Override
                    public void onNext(@NonNull Ok ok) {
                        mList.remove(notification);
                        mAdapter.notifyDataSetChanged();
                        getUnreadCount();
                    }
                });
    }

    public void deleteAllNotifications() {
        mModel.deleteAllNotifications()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Ok>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort(R.string.delete_failed);
                    }

                    @Override
                    public void onNext(@NonNull Ok ok) {
                        mList.clear();
                        mAdapter.notifyDataSetChanged();
                        getUnreadCount();
                    }
                });
    }

    private void getUnreadCount() {
        mModel.getUnreadCount()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorHandleSubscriber<Count>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull Count data) {
                        int count = data.getCount();
                        mRootView.setSubtitle(count > 0 ?
                                MessageFormat.format(mApplication.getString(R.string.what_unread), data.getCount())
                                : mApplication.getString(R.string.no_unread));
                        EventBus.getDefault().post(new GetUnreadCountEvent(data.getCount() > 0));
                    }
                });
    }

    private void readNotification(int[] ids) {
        mModel.readNotification(ids)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Ok>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort(R.string.read_failed);
                    }

                    @Override
                    public void onNext(@NonNull Ok data) {
                        ToastUtils.showShort(R.string.read_success);
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
