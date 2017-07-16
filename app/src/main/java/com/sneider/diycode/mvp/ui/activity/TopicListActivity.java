package com.sneider.diycode.mvp.ui.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.reflect.TypeToken;
import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.di.component.DaggerTopicListComponent;
import com.sneider.diycode.di.module.TopicListModule;
import com.sneider.diycode.event.ReplyEvent;
import com.sneider.diycode.mvp.contract.TopicListContract;
import com.sneider.diycode.mvp.model.bean.Section;
import com.sneider.diycode.mvp.model.bean.User;
import com.sneider.diycode.mvp.presenter.TopicListPresenter;
import com.sneider.diycode.mvp.ui.adapter.NodeListAdapter;
import com.sneider.diycode.mvp.ui.adapter.SectionListAdapter;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.PrefUtils;
import com.sneider.diycode.widget.loadmore.LoadMoreRecyclerView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.simple.eventbus.Subscriber;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sneider.diycode.app.ARouterPaths.TOPIC_LIST;

@Route(path = TOPIC_LIST)
public class TopicListActivity extends BaseActivity<TopicListPresenter> implements TopicListContract.View, SwipeRefreshLayout.OnRefreshListener {

    public static final String EXTRA_TOPIC_TYPE = "EXTRA_TOPIC_TYPE";
    public static final String EXTRA_TOPIC_NODE_NAME = "EXTRA_TOPIC_NODE_NAME";
    public static final String EXTRA_TOPIC_NODE_ID = "EXTRA_TOPIC_NODE_ID";
    public static final String EXTRA_TOPIC_USER = "EXTRA_TOPIC_USER";
    public static final int TOPIC_CREATE = 0;
    public static final int TOPIC_FAVORITES = 1;
    public static final int TOPIC_BY_NODE_ID = 2;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.srl) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list) LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.tv_no_data) TextView mTvNoData;

    private AppComponent mAppComponent;
    private RxPermissions mRxPermissions;
    private PopupWindow mWindow;
    private SectionListAdapter mSectionListAdapter;
    private NodeListAdapter mNodeListAdapter;
    private int mTopicType;
    private int mNodeId;
    private String mUsername;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        mAppComponent = appComponent;
        mRxPermissions = new RxPermissions(this);
        DaggerTopicListComponent.builder().appComponent(appComponent)
                .topicListModule(new TopicListModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_list;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mTopicType = getIntent().getIntExtra(EXTRA_TOPIC_TYPE, 0);
        mUsername = getIntent().getStringExtra(EXTRA_TOPIC_USER);
        User user = DiycodeUtils.getUser(this);
        switch (mTopicType) {
            case TOPIC_CREATE:
                mToolbar.setTitle(user != null && mUsername.equals(user.getLogin()) ? getString(R.string.my_topic) :
                        MessageFormat.format(getString(R.string.who_topic), mUsername));
                break;
            case TOPIC_FAVORITES:
                mToolbar.setTitle(user != null && mUsername.equals(user.getLogin()) ? getString(R.string.my_favorite) :
                        MessageFormat.format(getString(R.string.who_favorite), mUsername));
                break;
            case TOPIC_BY_NODE_ID:
                mToolbar.setTitle(MessageFormat.format(getString(R.string.what_topics), getIntent().getStringExtra(EXTRA_TOPIC_NODE_NAME)));
                mNodeId = getIntent().getIntExtra(EXTRA_TOPIC_NODE_ID, 0);
                break;
            default:
                break;
        }
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(mToolbar);
        initPopupWindow();

        mPresenter.initAdapter(mTopicType, mUsername);
        mPresenter.getTopics(mTopicType, mUsername, mNodeId, true);
    }

    private void initPopupWindow() {
        View view = View.inflate(this, R.layout.popup_topic_nodes, null);
        RecyclerView listSection = ButterKnife.findById(view, R.id.list_section);
        LinearLayoutManager sectionManager = new LinearLayoutManager(this);
        sectionManager.setOrientation(LinearLayoutManager.VERTICAL);
        listSection.setLayoutManager(sectionManager);
        mSectionListAdapter = new SectionListAdapter(null);
        mSectionListAdapter.setOnItemClickListener((view1, data) -> {
            mNodeListAdapter.clearData();
            mNodeListAdapter.addData(data.getNodes());
        });
        listSection.setAdapter(mSectionListAdapter);

        RecyclerView listNode = ButterKnife.findById(view, R.id.list_node);
        LinearLayoutManager nodeManager = new LinearLayoutManager(this);
        nodeManager.setOrientation(LinearLayoutManager.VERTICAL);
        listNode.setLayoutManager(nodeManager);
        mNodeListAdapter = new NodeListAdapter(null);
        mNodeListAdapter.setOnItemClickListener((view2, data) -> {
            mToolbar.setTitle(MessageFormat.format(getString(R.string.what_topics), data.getName()));
            mNodeId = data.getId();
            mRecyclerView.setCanloadMore(true);
            mPresenter.getTopics(mTopicType, mUsername, mNodeId, true);
            mWindow.dismiss();
        });
        listNode.setAdapter(mNodeListAdapter);

        String json = PrefUtils.getInstance(this).getString("topic_nodes", "");
        if (!TextUtils.isEmpty(json)) {
            List<Section> list = mAppComponent.gson().fromJson(json, new TypeToken<List<Section>>() {
            }.getType());
            mSectionListAdapter.addData(list);
            mNodeListAdapter.addData(list.get(0).getNodes());
        } else {
            mPresenter.getNodes(true);
        }

        mWindow = new PopupWindow(view);
        mWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mWindow.setFocusable(true);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
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
        mRecyclerView.setLoadMoreListener(() -> mPresenter.getTopics(mTopicType, mUsername, mNodeId, false));
        mRecyclerView.setOnClickReloadListener(() -> {
            mRecyclerView.setCanloadMore(true);
            mRecyclerView.showLoadMore();
            mPresenter.getTopics(mTopicType, mUsername, mNodeId, false);
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
    public void onFavoriteSuccess() {
        Snackbar.make(mSwipeRefreshLayout, R.string.favorited, Snackbar.LENGTH_SHORT)
                .setAction(R.string.view_my_favorite, v -> {
                    if (DiycodeUtils.checkToken(TopicListActivity.this)) {
                        ARouter.getInstance().build(TOPIC_LIST)
                                .withInt(EXTRA_TOPIC_TYPE, TOPIC_FAVORITES)
                                .withString(EXTRA_TOPIC_USER, DiycodeUtils.getUser(this).getLogin())
                                .navigation();
                    }
                }).show();
    }

    @Override
    public void onGetNodes(List<Section> sections) {
        mSectionListAdapter.addData(sections);
        mNodeListAdapter.addData(sections.get(0).getNodes());
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
    }

    @Override
    public void onRefresh() {
        mPresenter.getTopics(mTopicType, mUsername, mNodeId, true);
        mRecyclerView.setCanloadMore(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mTopicType == TOPIC_BY_NODE_ID) {
            getMenuInflater().inflate(R.menu.menu_toolbar_list_activity, menu);
        }
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
    public void onDestroy() {
        DefaultAdapter.releaseAllHolder(mRecyclerView);
        super.onDestroy();
        mRxPermissions = null;
    }

    @Subscriber
    private void onReplyEvent(ReplyEvent event) {
        mPresenter.getTopics(mTopicType, mUsername, mNodeId, true);
        mRecyclerView.setCanloadMore(true);
    }
}
