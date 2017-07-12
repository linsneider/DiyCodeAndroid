package com.sneider.diycode.mvp.ui.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.gson.reflect.TypeToken;
import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.di.component.DaggerNewsListComponent;
import com.sneider.diycode.di.module.NewsListModule;
import com.sneider.diycode.mvp.contract.NewsListContract;
import com.sneider.diycode.mvp.model.bean.NewsNode;
import com.sneider.diycode.mvp.presenter.NewsListPresenter;
import com.sneider.diycode.utils.PrefUtils;
import com.sneider.diycode.widget.FlowLayout;
import com.sneider.diycode.widget.loadmore.LoadMoreRecyclerView;
import com.sneider.diycode.widget.tag.TagAdapter;
import com.sneider.diycode.widget.tag.TagFlowLayout;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sneider.diycode.app.ARouterPaths.NEWS_LIST;

@Route(path = NEWS_LIST)
public class NewsListActivity extends BaseActivity<NewsListPresenter> implements NewsListContract.View, SwipeRefreshLayout.OnRefreshListener {

    public static final String EXTRA_NEWS_NODE_NAME = "EXTRA_NEWS_NODE_NAME";
    public static final String EXTRA_NEWS_NODE_ID = "EXTRA_NEWS_NODE_ID";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.srl) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list) LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.tv_no_data) TextView mTvNoData;

    TagFlowLayout mFlowLayout;

    private AppComponent mAppComponent;
    private RxPermissions mRxPermissions;
    private PopupWindow mWindow;
    private int mNodeId;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        mAppComponent = appComponent;
        mRxPermissions = new RxPermissions(this);
        DaggerNewsListComponent.builder().appComponent(appComponent)
                .newsListModule(new NewsListModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_list;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mToolbar.setTitle("News : " + getIntent().getStringExtra(EXTRA_NEWS_NODE_NAME));
        mNodeId = getIntent().getIntExtra(EXTRA_NEWS_NODE_ID, 0);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(mToolbar);
        initPopupWindow();

        mPresenter.initAdapter();
        mPresenter.getNews(mNodeId, true);
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
        mRecyclerView.setLoadMoreListener(() -> mPresenter.getNews(mNodeId, false));
        mRecyclerView.setOnClickReloadListener(() -> {
            mRecyclerView.setCanloadMore(true);
            mRecyclerView.showLoadMore();
            mPresenter.getNews(mNodeId, false);
        });
    }

    private void initPopupWindow() {
        View view = View.inflate(this, R.layout.popup_news_nodes, null);
        mFlowLayout = ButterKnife.findById(view, R.id.flow_layout);

        String json = PrefUtils.getInstance(this).getString("news_nodes", "");
        if (!TextUtils.isEmpty(json)) {
            final List<NewsNode> list = mAppComponent.gson().fromJson(json, new TypeToken<List<NewsNode>>() {
            }.getType());
            initTagLayout(list);
        } else {
            mPresenter.getNewsNodes(true);
        }

        mWindow = new PopupWindow(view);
        mWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mWindow.setFocusable(true);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private void initTagLayout(List<NewsNode> list) {
        TagAdapter adapter = new TagAdapter<NewsNode>(list) {
            @Override
            public View getView(FlowLayout parent, int position, NewsNode node) {
                TextView view = (TextView) View.inflate(NewsListActivity.this, R.layout.item_news_node, null);
                view.setText(node.getName());
                return view;
            }
        };
        mFlowLayout.setOnTagClickListener((view1, position, parent) -> {
            mToolbar.setTitle("News : " + list.get(position).getName());
            mNodeId = list.get(position).getId();
            mRecyclerView.setCanloadMore(true);
            mPresenter.getNews(mNodeId, true);
            mWindow.dismiss();
            return true;
        });
        mFlowLayout.setAdapter(adapter);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == mNodeId) {
                adapter.setSelectedList(i);
                break;
            }
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.getNews(mNodeId, true);
        mRecyclerView.setCanloadMore(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_select_node) {
            if (mWindow != null) {
                mWindow.showAsDropDown(mToolbar);
            }
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
    public void onGetNodes(List<NewsNode> nodes) {
        initTagLayout(nodes);
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
