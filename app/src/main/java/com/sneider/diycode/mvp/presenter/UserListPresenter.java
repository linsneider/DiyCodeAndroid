package com.sneider.diycode.mvp.presenter;

import android.app.Application;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ToastUtils;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.PermissionUtil;
import com.sneider.diycode.R;
import com.sneider.diycode.mvp.contract.UserListContract;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.User;
import com.sneider.diycode.mvp.ui.adapter.UserListAdapter;
import com.sneider.diycode.utils.Constant;
import com.sneider.diycode.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

import static com.sneider.diycode.app.ARouterPaths.USER_DETAIL;
import static com.sneider.diycode.mvp.ui.activity.UserDetailActivity.EXTRA_USERNAME;
import static com.sneider.diycode.mvp.ui.activity.UserListActivity.USER_BLOCK;
import static com.sneider.diycode.mvp.ui.activity.UserListActivity.USER_FOLLOWER;
import static com.sneider.diycode.mvp.ui.activity.UserListActivity.USER_FOLLOWING;

@ActivityScope
public class UserListPresenter extends BasePresenter<UserListContract.Model, UserListContract.View> {

    private Application mApplication;
    private AppManager mAppManager;
    private RxErrorHandler mErrorHandler;
    private List<User> mList = new ArrayList<>();
    private UserListAdapter mAdapter;
    private int offset = 0;
    private boolean isFirst = true;

    @Inject
    public UserListPresenter(UserListContract.Model model, UserListContract.View rootView, RxErrorHandler handler, AppManager appManager, Application application) {
        super(model, rootView);
        mApplication = application;
        mAppManager = appManager;
        mErrorHandler = handler;
    }

    public void initAdapter(int userType) {
        if (mAdapter == null) {
            mAdapter = new UserListAdapter(mList);
            mRootView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener((view, user) -> ARouter.getInstance().build(USER_DETAIL)
                    .withString(EXTRA_USERNAME, user.getLogin())
                    .navigation());
            mAdapter.setOnItemLongClickListener((view, user) -> {
                switch (userType) {
                    case USER_FOLLOWING:
                        new MaterialDialog.Builder(mAppManager.getCurrentActivity()).items(R.array.unfollow)
                                .itemsCallback((dialog, itemView, position, text) -> unfollowUser(user)).show();
                        break;
                    case USER_BLOCK:
                        new MaterialDialog.Builder(mAppManager.getCurrentActivity()).items(R.array.unblock)
                                .itemsCallback((dialog, itemView, position, text) -> unblockUser(user)).show();
                        break;
                    default:
                        break;
                }
            });
        }
    }

    public void getUsers(int userType, String username, boolean isRefresh) {
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
        Observable<List<User>> observable = null;
        switch (userType) {
            case USER_FOLLOWER:
                observable = mModel.getFollowers(username, offset, isEvictCache);
                break;
            case USER_FOLLOWING:
                observable = mModel.getFollowings(username, offset, isEvictCache);
                break;
            case USER_BLOCK:
                observable = mModel.getBlockedUsers(username, offset, isEvictCache);
                break;
            default:
                break;
        }
        observable.subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> {
                    if (isRefresh) mRootView.showLoading();
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    if (isRefresh) mRootView.hideLoading();
                })
//                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<List<User>>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        mRootView.onLoadMoreError();
                    }

                    @Override
                    public void onNext(@NonNull List<User> data) {
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

    public void unfollowUser(User user) {
        mModel.unfollowUser(user.getLogin())
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Ok>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort(R.string.unfollow_failed);
                    }

                    @Override
                    public void onNext(@NonNull Ok ok) {
                        mList.remove(user);
                        mAdapter.notifyDataSetChanged();
                        mRootView.setEmpty(mList.isEmpty());
                    }
                });
    }

    private void unblockUser(User user) {
        mModel.unblockUser(user.getLogin())
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Ok>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort(R.string.unblock_failed);
                    }

                    @Override
                    public void onNext(@NonNull Ok ok) {
                        mList.remove(user);
                        mAdapter.notifyDataSetChanged();
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
