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
import com.sneider.diycode.di.component.DaggerReplyListComponent;
import com.sneider.diycode.di.module.ReplyListModule;
import com.sneider.diycode.mvp.contract.ReplyListContract;
import com.sneider.diycode.mvp.model.bean.User;
import com.sneider.diycode.mvp.presenter.ReplyListPresenter;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.widget.loadmore.LoadMoreRecyclerView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.text.MessageFormat;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sneider.diycode.app.ARouterPaths.REPLY_LIST;

@Route(path = REPLY_LIST)
public class ReplyListActivity extends BaseActivity<ReplyListPresenter> implements ReplyListContract.View, SwipeRefreshLayout.OnRefreshListener {

    public static final String EXTRA_REPLY_USER = "EXTRA_REPLY_USER";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.srl) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list) LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.tv_no_data) TextView mTvNoData;

    private RxPermissions mRxPermissions;
    private String mUsername;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        mRxPermissions = new RxPermissions(this);
        DaggerReplyListComponent.builder().appComponent(appComponent)
                .replyListModule(new ReplyListModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_list;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mUsername = getIntent().getStringExtra(EXTRA_REPLY_USER);
        User user = DiycodeUtils.getUser(this);
        mToolbar.setTitle(user != null && mUsername.equals(user.getLogin()) ? getString(R.string.my_reply) :
                MessageFormat.format(getString(R.string.who_reply), mUsername));
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(mToolbar);

        mPresenter.initAdapter();
        mPresenter.getUserReplies(mUsername, true);
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
    public void setAdapter(DefaultAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setLoadMoreListener(() -> mPresenter.getUserReplies(mUsername, false));
        mRecyclerView.setOnClickReloadListener(() -> {
            mRecyclerView.setCanloadMore(true);
            mRecyclerView.showLoadMore();
            mPresenter.getUserReplies(mUsername, false);
        });
    }

    @Override
    public void setEmpty(boolean isEmpty) {
        mRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        mTvNoData.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
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
    public void onRefresh() {
        mPresenter.getUserReplies(mUsername, true);
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
    public void onDestroy() {
        DefaultAdapter.releaseAllHolder(mRecyclerView);
        super.onDestroy();
        mRxPermissions = null;
    }
}
