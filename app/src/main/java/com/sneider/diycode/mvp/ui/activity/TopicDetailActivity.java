package com.sneider.diycode.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.jess.arms.widget.imageloader.glide.GlideImageConfig;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.di.component.DaggerTopicDetailComponent;
import com.sneider.diycode.di.module.TopicDetailModule;
import com.sneider.diycode.event.ReplyEvent;
import com.sneider.diycode.event.UpdateTopicEvent;
import com.sneider.diycode.mvp.contract.TopicDetailContract;
import com.sneider.diycode.mvp.model.bean.Like;
import com.sneider.diycode.mvp.model.bean.Topic;
import com.sneider.diycode.mvp.presenter.TopicDetailPresenter;
import com.sneider.diycode.utils.DateUtils;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.GlideCircleTransform;
import com.sneider.diycode.utils.WebImageListener;
import com.sneider.diycode.utils.WebViewUtils;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.simple.eventbus.Subscriber;

import java.text.MessageFormat;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;

import static com.sneider.diycode.app.ARouterPaths.TOPIC_ADD;
import static com.sneider.diycode.app.ARouterPaths.TOPIC_DETAIL;
import static com.sneider.diycode.app.ARouterPaths.TOPIC_LIST;
import static com.sneider.diycode.app.ARouterPaths.TOPIC_REPLY;
import static com.sneider.diycode.app.ARouterPaths.USER_DETAIL;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.EXTRA_TOPIC_NODE_ID;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.EXTRA_TOPIC_NODE_NAME;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.EXTRA_TOPIC_TYPE;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.EXTRA_TOPIC_USER;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.TOPIC_BY_NODE_ID;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.TOPIC_FAVORITES;
import static com.sneider.diycode.mvp.ui.activity.UserDetailActivity.EXTRA_USERNAME;

@Route(path = TOPIC_DETAIL)
public class TopicDetailActivity extends BaseActivity<TopicDetailPresenter> implements TopicDetailContract.View {

    public static final String EXTRA_TOPIC = "EXTRA_TOPIC";
    public static final String EXTRA_TOPIC_ID = "EXTRA_TOPIC_ID";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.normal_layout) LinearLayout mNormalLayout;
    @BindView(R.id.scroll_view) NestedScrollView mScrollView;
    @BindView(R.id.error_layout) LinearLayout mErrorLayout;
    @BindView(R.id.iv_avatar) ImageView mIvAvatar;
    @BindView(R.id.tv_name) TextView mTvName;
    @BindView(R.id.tv_node_name) TextView mTvNodeName;
    @BindView(R.id.tv_time) TextView mTvTime;
    @BindView(R.id.tv_hit) TextView mTvHit;
    @BindView(R.id.tv_title) TextView mTvTitle;
    @BindView(R.id.web_view_container) RelativeLayout mWvContainer;
    @BindView(R.id.btn_like) ImageView mBtnLike;
    @BindView(R.id.tv_like_count) TextView mTvLikeCount;
    @BindView(R.id.btn_favorite) ImageView mBtnFavorite;
    @BindView(R.id.btn_reply) ImageView mBtnReply;
    @BindView(R.id.tv_reply_count) TextView mTvReplyCount;
    @BindView(R.id.btn_edit) ImageView mBtnEdit;

    @BindColor(R.color.colorAccent) int colorAccent;
    @BindColor(R.color.color_62646c) int color_62646c;

    private AppComponent mAppComponent;
    private Topic mTopic;
    private WebView mWvContent;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        mAppComponent = appComponent;
        DaggerTopicDetailComponent.builder().appComponent(appComponent)
                .topicDetailModule(new TopicDetailModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_topic_detail;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mToolbar.setTitle(R.string.topic);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setOnClickListener(v -> mScrollView.scrollTo(0, 0));
        setSupportActionBar(mToolbar);

        initTopic();
    }

    private void initTopic() {
        mTopic = (Topic) getIntent().getSerializableExtra(EXTRA_TOPIC);
        if (mTopic != null) {
            mPresenter.getTopicDetail(mTopic.getId());
        } else {
            int topicId = getIntent().getIntExtra(EXTRA_TOPIC_ID, 0);
            if (topicId != 0) {
                mPresenter.getTopicDetail(topicId);
            }
        }
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mNormalLayout.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
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
    protected void onDestroy() {
        mAppComponent.imageLoader().clear(this, GlideImageConfig.builder().imageViews(mIvAvatar).build());
        clearWebViewResource();
        super.onDestroy();
    }

    private void clearWebViewResource() {
        if (mWvContent != null) {
            mWvContent.removeAllViews();
            // in android 5.1(sdk:21) we should invoke this to avoid memory leak
            // see (https://coolpers.github.io/webview/memory/leak/2015/07/16/android-5.1-webview-memory-leak.html)
            ((ViewGroup) mWvContent.getParent()).removeView(mWvContent);
            mWvContent.setTag(null);
            mWvContent.clearHistory();
            mWvContent.destroy();
            mWvContent = null;
        }
    }

    @Subscriber
    private void onReplyEvent(ReplyEvent event) {
        initTopic();
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
            DiycodeUtils.shareText(this, mTopic.getTitle(), "https://www.diycode.cc/topics/" + mTopic.getId());
        } else if (id == R.id.action_open_web) {
            DiycodeUtils.openWebActivity("https://www.diycode.cc/topics/" + mTopic.getId());
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.iv_avatar, R.id.tv_name})
    void onUserClick() {
        ARouter.getInstance().build(USER_DETAIL)
                .withString(EXTRA_USERNAME, mTopic.getUser().getLogin())
                .navigation();
    }

    @OnClick(R.id.tv_node_name)
    void onNodeNameClick() {
        ARouter.getInstance().build(TOPIC_LIST)
                .withInt(EXTRA_TOPIC_TYPE, TOPIC_BY_NODE_ID)
                .withString(EXTRA_TOPIC_NODE_NAME, mTopic.getNode_name())
                .withInt(EXTRA_TOPIC_NODE_ID, mTopic.getNode_id())
                .navigation();
    }

    @OnClick(R.id.btn_like)
    void onLikeClick() {
        if (DiycodeUtils.checkToken(this)) {
            if (mTopic.isLiked()) {
                mPresenter.unlikeTopic(mTopic.getId());
            } else {
                mPresenter.likeTopic(mTopic.getId());
            }
        }
    }

    @OnClick(R.id.btn_favorite)
    void onFavoriteClick() {
        if (DiycodeUtils.checkToken(this)) {
            if (mTopic.isFavorited()) {
                mPresenter.unfavoriteTopic(mTopic.getId());
            } else {
                mPresenter.favoriteTopic(mTopic.getId());
            }
        }
    }

    @OnClick(R.id.btn_reply)
    void onReplyClick() {
        ARouter.getInstance().build(TOPIC_REPLY)
                .withSerializable(TopicReplyActivity.EXTRA_TOPIC, mTopic)
                .navigation();
    }

    @OnClick(R.id.btn_edit)
    void onEditClick() {
        if (DiycodeUtils.checkToken(this)) {
            ARouter.getInstance().build(TOPIC_ADD)
                    .withSerializable(AddTopicActivity.EXTRA_TOPIC, mTopic)
                    .navigation();
        }
    }

    @OnClick(R.id.reloading)
    void onReloading() {
        initTopic();
    }

    @Subscriber
    private void onUpdateTopic(UpdateTopicEvent event) {
        initTopic();
    }

    @Override
    public void onGetTopicDetail(Topic topic) {
        if (topic == null) return;
        mTopic = topic;
        String avatarUrl = mTopic.getUser().getAvatar_url();
        if (avatarUrl.contains("diycode"))
            avatarUrl = avatarUrl.replace("large_avatar", "avatar");
        mAppComponent.imageLoader().loadImage(mAppComponent.application(), GlideImageConfig.builder()
                .transformation(new GlideCircleTransform(mAppComponent.application()))
                .url(avatarUrl).imageView(mIvAvatar).build());
        mTvName.setText(mTopic.getUser().getLogin());
        mTvNodeName.setText(mTopic.getNode_name());
        String intervalTime = DateUtils.getIntervalTime(mTopic.getCreated_at());
        mTvTime.setText(MessageFormat.format(getString(R.string.publish_time), intervalTime));
        mTvTitle.setText(mTopic.getTitle());
        if (mTopic.getReplies_count() == 0) {
            mTvReplyCount.setText("");
        } else {
            mTvReplyCount.setText(String.valueOf(mTopic.getReplies_count()));
        }
        mTvHit.setText(MessageFormat.format(getResources().getString(R.string.read_count), mTopic.getHits()));

        mWvContent = new WebView(getApplication());
        mWvContent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mWvContainer.addView(mWvContent);
        WebSettings settings = mWvContent.getSettings();
        settings.setJavaScriptEnabled(true);
        WebImageListener listener = new WebImageListener(this, ImageActivity.class);
        mWvContent.addJavascriptInterface(listener, "listener");
        mWvContent.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("https://www.diycode.cc/topics/")) {
                    ARouter.getInstance().build(TOPIC_DETAIL)
                            .withInt(EXTRA_TOPIC_ID, Integer.valueOf(url.substring(30)))
                            .navigation();
                    return true;
                }
                DiycodeUtils.openWebActivity(url);
                return true;
            }
        });
        mWvContent.loadDataWithBaseURL(null, WebViewUtils.convertTopicContent(mTopic.getBody_html()), "text/html", "utf-8", null);

        mBtnLike.setImageResource(mTopic.isLiked() ? R.drawable.ic_like_yes : R.drawable.ic_like);
        mTvLikeCount.setTextColor(mTopic.isLiked() ? colorAccent : color_62646c);
        mTvLikeCount.setText(mTopic.getLikes_count() > 0 ? String.valueOf(mTopic.getLikes_count()) : "");
        mBtnFavorite.setImageResource(mTopic.isFavorited() ? R.drawable.ic_favorite_yes : R.drawable.ic_favorite);
        mBtnEdit.setVisibility(mTopic.getAbilities().isUpdate() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onFavoriteTopic() {
        mTopic.setFavorited(!mTopic.isFavorited());
        mBtnFavorite.setImageResource(mTopic.isFavorited() ? R.drawable.ic_favorite_yes : R.drawable.ic_favorite);
        if (mTopic.isFavorited()) {
            Snackbar.make(mCoordinatorLayout, "已收藏", Snackbar.LENGTH_SHORT)
                    .setAction("查看我的收藏", v -> {
                        if (DiycodeUtils.checkToken(this)) {
                            ARouter.getInstance().build(TOPIC_LIST)
                                    .withInt(EXTRA_TOPIC_TYPE, TOPIC_FAVORITES)
                                    .withString(EXTRA_TOPIC_USER, DiycodeUtils.getUser(this).getLogin())
                                    .navigation();
                        }
                    }).show();
        }
    }

    @Override
    public void onLikeTopic(Like like) {
        mTopic.setLiked(!mTopic.isLiked());
        mBtnLike.setImageResource(mTopic.isLiked() ? R.drawable.ic_like_yes : R.drawable.ic_like);
        mTvLikeCount.setTextColor(mTopic.isLiked() ? colorAccent : color_62646c);
        mTvLikeCount.setText(like.getCount() > 0 ? String.valueOf(like.getCount()) : "");
    }

    @Override
    public void setLayout(boolean isNormal) {
        mNormalLayout.setVisibility(isNormal ? View.VISIBLE : View.GONE);
        mErrorLayout.setVisibility(isNormal ? View.GONE : View.VISIBLE);
    }
}
