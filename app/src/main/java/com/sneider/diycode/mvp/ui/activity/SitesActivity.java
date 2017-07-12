package com.sneider.diycode.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.di.component.DaggerSitesComponent;
import com.sneider.diycode.di.module.SitesModule;
import com.sneider.diycode.mvp.contract.SitesContract;
import com.sneider.diycode.mvp.presenter.SitesPresenter;
import com.sneider.diycode.widget.loadmore.LoadMoreRecyclerView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;

import static com.sneider.diycode.app.ARouterPaths.PUBLIC_SITES;

@Route(path = PUBLIC_SITES)
public class SitesActivity extends BaseActivity<SitesPresenter> implements SitesContract.View {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.list) LoadMoreRecyclerView mRecyclerView;

    private RxPermissions mRxPermissions;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        mRxPermissions = new RxPermissions(this);
        DaggerSitesComponent.builder().appComponent(appComponent)
                .sitesModule(new SitesModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_sites;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mToolbar.setTitle(R.string.sites);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(mToolbar);

        mPresenter.initAdapter();
        mPresenter.getSites(true);
    }

    @Override
    public void setAdapter(DefaultAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setLoadMoreListener(() -> mPresenter.getSites(false));
        mRecyclerView.setOnClickReloadListener(() -> {
            mRecyclerView.setCanloadMore(true);
            mRecyclerView.showLoadMore();
            mPresenter.getSites(false);
        });
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
        mRecyclerView.showLoadMore();
    }

    @Override
    public void hideLoading() {
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
