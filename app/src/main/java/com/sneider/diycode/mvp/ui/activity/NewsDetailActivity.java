package com.sneider.diycode.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.di.component.DaggerNewsDetailComponent;
import com.sneider.diycode.di.module.NewsDetailModule;
import com.sneider.diycode.mvp.contract.NewsDetailContract;
import com.sneider.diycode.mvp.model.bean.News;
import com.sneider.diycode.mvp.presenter.NewsDetailPresenter;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.widget.loadmore.LoadMoreRecyclerView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;

import static com.sneider.diycode.app.ARouterPaths.NEWS_DETAIL;

@Route(path = NEWS_DETAIL)
public class NewsDetailActivity extends BaseActivity<NewsDetailPresenter> implements NewsDetailContract.View {

    public static final String EXTRA_NEWS = "EXTRA_NEWS";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.list) LoadMoreRecyclerView mRecyclerView;

    private RxPermissions mRxPermissions;
    private News mNews;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        mRxPermissions = new RxPermissions(this);
        DaggerNewsDetailComponent.builder().appComponent(appComponent)
                .newsDetailModule(new NewsDetailModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_detail;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setOnClickListener(v -> mRecyclerView.scrollToPosition(0));
        setSupportActionBar(mToolbar);

        mNews = (News) getIntent().getSerializableExtra(EXTRA_NEWS);
        if (mNews != null) {
            mToolbar.setTitle(mNews.getTitle());
            mPresenter.initAdapter(mNews);
            if (mNews.getReplies_count() != 0) {
                mPresenter.getNewsReplies(mNews.getId(), true);
            } else {
                mRecyclerView.loadMoreEnd();
                mRecyclerView.setCanloadMore(false);
            }
        }
    }

    @Override
    public void setAdapter(DefaultAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setLoadMoreListener(() -> mPresenter.getNewsReplies(mNews.getId(), false));
        mRecyclerView.setOnClickReloadListener(() -> {
            mRecyclerView.setCanloadMore(true);
            mRecyclerView.showLoadMore();
            mPresenter.getNewsReplies(mNews.getId(), false);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_detail_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_share) {
            DiycodeUtils.shareText(this, mNews.getTitle(), "https://www.diycode.cc/news/" + mNews.getId());
        } else if (id == R.id.action_open_web) {
            DiycodeUtils.openWebActivity("https://www.diycode.cc/news/" + mNews.getId());
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
    public void smoothToPosition(int position) {
        mRecyclerView.smoothScrollToPosition(position);
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