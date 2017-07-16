package com.sneider.diycode.mvp.presenter;

import android.app.Application;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.PermissionUtil;
import com.sneider.diycode.mvp.contract.ProjectFragmentContract;
import com.sneider.diycode.mvp.model.bean.Project;
import com.sneider.diycode.mvp.ui.adapter.ProjectListAdapter;
import com.sneider.diycode.utils.Constant;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

import static com.sneider.diycode.app.ARouterPaths.PROJECT_DETAIL;
import static com.sneider.diycode.mvp.ui.activity.ProjectDetailActivity.EXTRA_PROJECT;

@FragmentScope
public class ProjectFragmentPresenter extends BasePresenter<ProjectFragmentContract.Model, ProjectFragmentContract.View> {

    private Application mApplication;
    private AppManager mAppManager;
    private RxErrorHandler mErrorHandler;
    private List<Project> mList = new ArrayList<>();
    private ProjectListAdapter mAdapter;
    private int offset = 0;
    private boolean isFirst = true;

    @Inject
    public ProjectFragmentPresenter(ProjectFragmentContract.Model model, ProjectFragmentContract.View rootView, RxErrorHandler handler, AppManager appManager, Application application) {
        super(model, rootView);
        mApplication = application;
        mAppManager = appManager;
        mErrorHandler = handler;
    }

    public void initAdapter() {
        if (mAdapter == null) {
            mAdapter = new ProjectListAdapter(mList);
            mRootView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new ProjectListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, Project project) {
                    ARouter.getInstance().build(PROJECT_DETAIL)
                            .withSerializable(EXTRA_PROJECT, project)
                            .navigation();
                }

                @Override
                public void onNameClick(View view, String github) {
                    DiycodeUtils.openWebActivity(github);
                }

                @Override
                public void onCategoryClick(View view, String categoryName) {
                    DiycodeUtils.openWebActivity("https://www.diycode.cc/categories/" + categoryName);
                }

                @Override
                public void onSubCategoryClick(View view, int subCategoryId) {
                    DiycodeUtils.openWebActivity("https://www.diycode.cc/sub_categories/" + subCategoryId);
                }
            });
        }
    }

    public void getProjects(boolean pullToRefresh) {
        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
            }

            @Override
            public void onRequestPermissionFailure() {
            }
        }, mRootView.getRxPermissions(), mErrorHandler);
        if (pullToRefresh) offset = 0;
        boolean isEvictCache = true;
//        if (pullToRefresh && isFirst) {
//            isFirst = false;
//            isEvictCache = false;
//        }
        mModel.getProjects(offset, isEvictCache)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> {
                    if (pullToRefresh) mRootView.showLoading();
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    if (pullToRefresh) mRootView.hideLoading();
                })
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<List<Project>>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        mRootView.onLoadMoreError();
                    }

                    @Override
                    public void onNext(@NonNull List<Project> data) {
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
