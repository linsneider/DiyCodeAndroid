package com.sneider.diycode.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.di.component.DaggerUserListComponent;
import com.sneider.diycode.di.module.UserListModule;
import com.sneider.diycode.mvp.contract.UserListContract;
import com.sneider.diycode.mvp.model.bean.User;
import com.sneider.diycode.mvp.presenter.UserListPresenter;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.widget.loadmore.LoadMoreRecyclerView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sneider.diycode.app.ARouterPaths.USER_LIST;

@Route(path = USER_LIST)
public class UserListActivity extends BaseActivity<UserListPresenter> implements UserListContract.View, SwipeRefreshLayout.OnRefreshListener {

    public static final String EXTRA_USER_TYPE = "EXTRA_USER_TYPE";
    public static final String EXTRA_USERNAME = "EXTRA_USERNAME";
    public static final int USER_FOLLOWER = 0;
    public static final int USER_FOLLOWING = 1;
    public static final int USER_BLOCK = 2;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.srl) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list) LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.tv_no_data) TextView mTvNoData;

    private RxPermissions mRxPermissions;
    private int mUserType;
    private String mUsername;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        mRxPermissions = new RxPermissions(this);
        DaggerUserListComponent.builder().appComponent(appComponent)
                .userListModule(new UserListModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_list;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mUsername = getIntent().getStringExtra(EXTRA_USERNAME);
        mUserType = getIntent().getIntExtra(EXTRA_USER_TYPE, 0);
        User user = DiycodeUtils.getUser(this);
        switch (mUserType) {
            case USER_FOLLOWER:
                if (user != null && mUsername.equals(user.getLogin())) {
                    mToolbar.setTitle("我的关注者");
                } else {
                    mToolbar.setTitle(mUsername + " 的关注者");
                }
                break;
            case USER_FOLLOWING:
                if (user != null && mUsername.equals(user.getLogin())) {
                    mToolbar.setTitle("我正在关注");
                } else {
                    mToolbar.setTitle(mUsername + " 正在关注");
                }
                break;
            case USER_BLOCK:
                if (user != null && mUsername.equals(user.getLogin())) {
                    mToolbar.setTitle("我已屏蔽");
                } else {
                    mToolbar.setTitle(mUsername + " 已屏蔽");
                }
                break;
            default:
                break;
        }
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(mToolbar);

        mPresenter.initAdapter(mUserType);
        mPresenter.getUsers(mUserType, mUsername, true);
    }

    @Override
    public void setAdapter(DefaultAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
        initRecyclerView();
    }

    @Override
    public void setEmpty(boolean isEmpty) {
        mRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        mTvNoData.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void initRecyclerView() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setLoadMoreListener(() -> mPresenter.getUsers(mUserType, mUsername, false));
        mRecyclerView.setOnClickReloadListener(() -> {
            mRecyclerView.setCanloadMore(true);
            mRecyclerView.showLoadMore();
            mPresenter.getUsers(mUserType, mUsername, false);
        });
    }

    @Override
    public void onRefresh() {
        mPresenter.getUsers(mUserType, mUsername, true);
        mRecyclerView.setCanloadMore(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadMoreComplete() {
        mRecyclerView.loadMoreComplete();
    }

    @Override
    public void onLoadMoreError() {
        mRecyclerView.loadMoreError();
        mRecyclerView.setCanloadMore(false);
    }

    @Override
    public void onLoadMoreEnd() {
        mRecyclerView.loadMoreEnd();
        mRecyclerView.setCanloadMore(false);
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
    }

    @Override
    public void showLoading() {
        Observable.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> mSwipeRefreshLayout.setRefreshing(true));
    }

    @Override
    public void hideLoading() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(String message) {
        UiUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(Intent intent) {
        UiUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    @Override
    public void onDestroy() {
        DefaultAdapter.releaseAllHolder(mRecyclerView);
        super.onDestroy();
        mRxPermissions = null;
    }
}
