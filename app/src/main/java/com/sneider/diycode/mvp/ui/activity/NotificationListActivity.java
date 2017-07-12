package com.sneider.diycode.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.di.component.DaggerNotificationListComponent;
import com.sneider.diycode.di.module.NotificationListModule;
import com.sneider.diycode.mvp.contract.NotificationListContract;
import com.sneider.diycode.mvp.presenter.NotificationListPresenter;
import com.sneider.diycode.widget.loadmore.LoadMoreRecyclerView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindColor;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sneider.diycode.app.ARouterPaths.USER_NOTIFICATION;

@Route(path = USER_NOTIFICATION)
public class NotificationListActivity extends BaseActivity<NotificationListPresenter>
        implements NotificationListContract.View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.srl) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list) LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.tv_no_data) TextView mTvNoData;

    @BindColor(R.color.color_4d4d4d) int color_4d4d4d;
    @BindColor(R.color.color_999999) int color_999999;

    private RxPermissions mRxPermissions;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        mRxPermissions = new RxPermissions(this);
        DaggerNotificationListComponent.builder().appComponent(appComponent)
                .notificationListModule(new NotificationListModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_list;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mToolbar.setTitle(R.string.my_notification);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(mToolbar);

        mPresenter.initAdapter();
        mPresenter.getNotifications(true);
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
        mRecyclerView.setLoadMoreListener(() -> mPresenter.getNotifications(false));
        mRecyclerView.setOnClickReloadListener(() -> {
            mRecyclerView.setCanloadMore(true);
            mRecyclerView.showLoadMore();
            mPresenter.getNotifications(false);
        });
    }

    @Override
    public void onRefresh() {
        mPresenter.getNotifications(true);
        mRecyclerView.setCanloadMore(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_notification_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_clear_all) {
            new MaterialDialog.Builder(this)
                    .content("确定要清空全部通知吗？")
                    .contentColor(color_4d4d4d)
                    .positiveText(R.string.confirm)
                    .onPositive((dialog, which) -> mPresenter.deleteAllNotifications())
                    .negativeText(R.string.cancel)
                    .negativeColor(color_999999)
                    .show();
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
    public void setSubtitle(String subtitle) {
        mToolbar.setSubtitle(subtitle);
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
