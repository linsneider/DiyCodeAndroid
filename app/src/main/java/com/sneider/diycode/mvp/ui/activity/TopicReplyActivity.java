package com.sneider.diycode.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.di.component.DaggerTopicReplyComponent;
import com.sneider.diycode.di.module.TopicReplyModule;
import com.sneider.diycode.event.ReplyEvent;
import com.sneider.diycode.mvp.contract.TopicReplyContract;
import com.sneider.diycode.mvp.model.bean.Topic;
import com.sneider.diycode.mvp.presenter.TopicReplyPresenter;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.widget.loadmore.LoadMoreRecyclerView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.simple.eventbus.Subscriber;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sneider.diycode.app.ARouterPaths.REPLY_ADD;
import static com.sneider.diycode.app.ARouterPaths.TOPIC_REPLY;
import static com.sneider.diycode.mvp.ui.activity.AddReplyActivity.EXTRA_DATA;
import static com.sneider.diycode.mvp.ui.activity.AddReplyActivity.EXTRA_REPLY_TYPE;
import static com.sneider.diycode.mvp.ui.activity.AddReplyActivity.TYPE_TOPIC;

@Route(path = TOPIC_REPLY)
public class TopicReplyActivity extends BaseActivity<TopicReplyPresenter> implements TopicReplyContract.View, SwipeRefreshLayout.OnRefreshListener {

    public static final String EXTRA_TOPIC = "EXTRA_TOPIC";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.srl) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list) LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.tv_no_data) TextView mTvNoData;
    @BindView(R.id.fab) FloatingActionButton mFab;

    private RxPermissions mRxPermissions;
    private Topic mTopic;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        mRxPermissions = new RxPermissions(this);
        DaggerTopicReplyComponent.builder().appComponent(appComponent)
                .topicReplyModule(new TopicReplyModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_list;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mTopic = (Topic) getIntent().getSerializableExtra(EXTRA_TOPIC);
        mToolbar.setTitle(R.string.reply);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(mToolbar);

        mFab.setVisibility(View.VISIBLE);

        mPresenter.initAdapter(mTopic);
        mPresenter.getTopicReplies(mTopic.getId(), true);
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
        mRecyclerView.setLoadMoreListener(() -> mPresenter.getTopicReplies(mTopic.getId(), false));
        mRecyclerView.setOnClickReloadListener(() -> {
            mRecyclerView.setCanloadMore(true);
            mRecyclerView.showLoadMore();
            mPresenter.getTopicReplies(mTopic.getId(), false);
        });
    }

    @Override
    public void onRefresh() {
        mPresenter.getTopicReplies(mTopic.getId(), true);
        mRecyclerView.setCanloadMore(true);
    }

    @Subscriber
    private void onReplyEvent(ReplyEvent event) {
        mPresenter.getTopicReplies(mTopic.getId(), true);
        mRecyclerView.setCanloadMore(true);
    }

    @OnClick(R.id.fab)
    void clickFab() {
        if (DiycodeUtils.checkToken(this)) {
            ARouter.getInstance().build(REPLY_ADD)
                    .withInt(EXTRA_REPLY_TYPE, TYPE_TOPIC)
                    .withSerializable(EXTRA_DATA, mTopic)
                    .navigation();
        }
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
    public void smoothToPosition(int position) {
        mRecyclerView.smoothScrollToPosition(position);
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
