package com.sneider.diycode.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.sneider.diycode.R;
import com.sneider.diycode.di.component.DaggerTopicFragmentComponent;
import com.sneider.diycode.di.module.TopicFragmentModule;
import com.sneider.diycode.event.ReplyEvent;
import com.sneider.diycode.event.UpdateTopicEvent;
import com.sneider.diycode.mvp.contract.TopicFragmentContract;
import com.sneider.diycode.mvp.presenter.TopicFragmentPresenter;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.widget.loadmore.LoadMoreRecyclerView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.simple.eventbus.Subscriber;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sneider.diycode.app.ARouterPaths.TOPIC_LIST;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.EXTRA_TOPIC_TYPE;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.EXTRA_TOPIC_USER;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.TOPIC_FAVORITES;

public class TopicFragment extends BaseFragment<TopicFragmentPresenter> implements TopicFragmentContract.View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.srl) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list) LoadMoreRecyclerView mRecyclerView;

    private RxPermissions mRxPermissions;

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
        mRxPermissions = new RxPermissions(getActivity());
        DaggerTopicFragmentComponent.builder().appComponent(appComponent)
                .topicFragmentModule(new TopicFragmentModule(this)).build().inject(this);
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mPresenter.initAdapter();
        mPresenter.getTopics(true);
    }

    @Override
    public void setData(Object data) {
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
    }

    @Override
    public void setAdapter(DefaultAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setLoadMoreListener(() -> mPresenter.getTopics(false));
        mRecyclerView.setOnClickReloadListener(() -> {
            mRecyclerView.setCanloadMore(true);
            mRecyclerView.showLoadMore();
            mPresenter.getTopics(false);
        });
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
    public void onFavoriteSuccess() {
        Snackbar.make(mSwipeRefreshLayout, R.string.favorited, Snackbar.LENGTH_SHORT)
                .setAction(R.string.view_my_favorite, v -> {
                    if (DiycodeUtils.checkToken(getContext())) {
                        ARouter.getInstance().build(TOPIC_LIST)
                                .withInt(EXTRA_TOPIC_TYPE, TOPIC_FAVORITES)
                                .withString(EXTRA_TOPIC_USER, DiycodeUtils.getUser(getContext()).getLogin())
                                .navigation();
                    }
                }).show();
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
    }

    @Override
    public void onRefresh() {
        mPresenter.getTopics(true);
        mRecyclerView.setCanloadMore(true);
    }

    @Override
    public void onDestroy() {
        DefaultAdapter.releaseAllHolder(mRecyclerView);
        super.onDestroy();
        mRxPermissions = null;
    }

    @Subscriber
    private void onReplyEvent(ReplyEvent event) {
        mPresenter.getTopics(true);
        mRecyclerView.setCanloadMore(true);
    }

    @Subscriber
    private void onUpdateTopic(UpdateTopicEvent event) {
        mPresenter.getTopics(true);
        mRecyclerView.setCanloadMore(true);
    }
}
