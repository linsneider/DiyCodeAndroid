package com.sneider.diycode.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.jess.arms.widget.imageloader.glide.GlideImageConfig;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.di.component.DaggerUserDetailComponent;
import com.sneider.diycode.di.module.UserDetailModule;
import com.sneider.diycode.mvp.contract.UserDetailContract;
import com.sneider.diycode.mvp.model.bean.User;
import com.sneider.diycode.mvp.presenter.UserDetailPresenter;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.GlideCircleTransform;

import java.text.MessageFormat;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;

import static com.sneider.diycode.app.ARouterPaths.PUBLIC_PHOTO;
import static com.sneider.diycode.app.ARouterPaths.REPLY_LIST;
import static com.sneider.diycode.app.ARouterPaths.TOPIC_LIST;
import static com.sneider.diycode.app.ARouterPaths.USER_DETAIL;
import static com.sneider.diycode.app.ARouterPaths.USER_LIST;
import static com.sneider.diycode.mvp.ui.activity.PhotoActivity.EXTRA_PHOTO_URL;
import static com.sneider.diycode.mvp.ui.activity.ReplyListActivity.EXTRA_REPLY_USER;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.EXTRA_TOPIC_TYPE;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.EXTRA_TOPIC_USER;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.TOPIC_CREATE;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.TOPIC_FAVORITES;
import static com.sneider.diycode.mvp.ui.activity.UserListActivity.EXTRA_USER_TYPE;
import static com.sneider.diycode.mvp.ui.activity.UserListActivity.USER_BLOCK;
import static com.sneider.diycode.mvp.ui.activity.UserListActivity.USER_FOLLOWER;
import static com.sneider.diycode.mvp.ui.activity.UserListActivity.USER_FOLLOWING;

@Route(path = USER_DETAIL)
public class UserDetailActivity extends BaseActivity<UserDetailPresenter> implements UserDetailContract.View {

    public static final String EXTRA_USERNAME = "EXTRA_USERNAME";

    @BindView(R.id.coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.app_bar) AppBarLayout mAppBarLayout;
    @BindView(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.head_layout) LinearLayout mHeadLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.iv_avatar) ImageView mIvAvatar;
    @BindView(R.id.tv_username) TextView mTvUsername;
    @BindView(R.id.tv_number) TextView mTvNumber;
    @BindView(R.id.tv_time_location) TextView mTvTimeLocation;
    @BindView(R.id.btn_follow) TextView mBtnFollow;
    @BindView(R.id.btn_block) TextView mBtnBlock;
    @BindView(R.id.tv_topic) TextView mTvTopic;
    @BindView(R.id.tv_topic_count) TextView mTvTopicCount;
    @BindView(R.id.tv_favorite) TextView mTvFavorite;
    @BindView(R.id.tv_favorite_count) TextView mTvFavoriteCount;
    @BindView(R.id.tv_reply) TextView mTvReply;
    @BindView(R.id.tv_reply_count) TextView mTvReplyCount;
    @BindView(R.id.tv_share) TextView mTvShare;
    @BindView(R.id.tv_follower) TextView mTvFollower;
    @BindView(R.id.tv_follower_count) TextView mTvFollowerCount;
    @BindView(R.id.tv_following) TextView mTvFollowing;
    @BindView(R.id.tv_following_count) TextView mTvFollowingCount;
    @BindView(R.id.ll_block) LinearLayout mLlBlock;

    @BindColor(R.color.color_4d4d4d) int color_4d4d4d;

    private AppComponent mAppComponent;
    private String mUsername;
    private String mAvatarUrl;
    private User mUserMe;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        mAppComponent = appComponent;
        DaggerUserDetailComponent.builder().appComponent(appComponent)
                .userDetailModule(new UserDetailModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_user_detail;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(mToolbar);

        mUsername = getIntent().getStringExtra(EXTRA_USERNAME);
        if (!TextUtils.isEmpty(mUsername)) {
            mTvUsername.setText(mUsername);
            Glide.with(mAppComponent.application()).load(R.mipmap.ic_launcher)
                    .transform(new GlideCircleTransform(mAppComponent.application())).into(mIvAvatar);
            mPresenter.getUserInfo(mUsername);
            mUserMe = DiycodeUtils.getUser(this);
            if (mUserMe != null && mUsername.equals(mUserMe.getLogin())) {
                mBtnFollow.setVisibility(View.GONE);
                mBtnBlock.setVisibility(View.GONE);
                mLlBlock.setVisibility(View.VISIBLE);
                mTvTopic.setText(R.string.my_topic);
                mTvFavorite.setText(R.string.my_favorite);
                mTvReply.setText(R.string.my_reply);
                mTvShare.setText(R.string.my_share);
                mTvFollower.setText(R.string.my_follower);
                mTvFollowing.setText(R.string.my_following);
            }
        }

        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset <= -mHeadLayout.getHeight() / 2) {
                mToolbarLayout.setTitle(mUsername);
                mToolbarLayout.setCollapsedTitleTextColor(color_4d4d4d);
            } else {
                mToolbarLayout.setTitle("");
            }
        });
    }

    @Override
    public void showLoading() {
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
    public void onGetUserInfo(User user) {
        mAvatarUrl = user.getAvatar_url();
        if (mAvatarUrl.contains("diycode"))
            mAvatarUrl = mAvatarUrl.replace("large_avatar", "avatar");
        mAppComponent.imageLoader().loadImage(mAppComponent.application(), GlideImageConfig.builder()
                .transformation(new GlideCircleTransform(mAppComponent.application()))
//                .errorPic(R.mipmap.ic_launcher)
//                .placeholder(R.mipmap.ic_launcher)
                .url(mAvatarUrl).imageView(mIvAvatar).build());
        mTvUsername.setText(user.getLogin() + "(" + user.getName() + ")");
        mTvNumber.setText(MessageFormat.format(getString(R.string.what_number_level), user.getId(), user.getLevel_name()));
        String timeAndLocation = user.getCreated_at().substring(0, 10);
        if (!TextUtils.isEmpty(user.getLocation())) {
            timeAndLocation += (" • " + user.getLocation());
        }
        mTvTimeLocation.setText(timeAndLocation);
        mTvTopicCount.setText(String.valueOf(user.getTopics_count()));
        mTvFavoriteCount.setText(String.valueOf(user.getFavorites_count()));
        mTvReplyCount.setText(String.valueOf(user.getReplies_count()));
        mTvFollowerCount.setText(String.valueOf(user.getFollowers_count()));
        mTvFollowingCount.setText(String.valueOf(user.getFollowing_count()));
    }

    @Override
    public void onFollowUser() {
        Snackbar.make(mCoordinatorLayout, R.string.followed, Snackbar.LENGTH_SHORT)
                .setAction(R.string.view_my_follow, v -> {
                    if (DiycodeUtils.checkToken(this)) {
                        ARouter.getInstance().build(USER_LIST)
                                .withInt(EXTRA_USER_TYPE, USER_FOLLOWING)
                                .withString(EXTRA_USERNAME, mUserMe.getLogin())
                                .navigation();
                    }
                }).show();
    }

    @Override
    public void onBlockUser() {
        Snackbar.make(mCoordinatorLayout, R.string.blocked, Snackbar.LENGTH_SHORT)
                .setAction(R.string.view_my_block, v -> {
                    if (DiycodeUtils.checkToken(this)) {
                        ARouter.getInstance().build(USER_LIST)
                                .withInt(EXTRA_USER_TYPE, USER_BLOCK)
                                .withString(EXTRA_USERNAME, mUserMe.getLogin())
                                .navigation();
                    }
                }).show();
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
            DiycodeUtils.shareText(this, mUsername, "https://www.diycode.cc/" + mUsername);
        } else if (id == R.id.action_open_web) {
            DiycodeUtils.openWebActivity("https://www.diycode.cc/" + mUsername);
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.iv_avatar)
    void clickAvatar() {
        if (!TextUtils.isEmpty(mAvatarUrl)) {
            ARouter.getInstance().build(PUBLIC_PHOTO)
                    .withString(EXTRA_PHOTO_URL, mAvatarUrl)
                    .navigation();
        }
    }

    @OnClick(R.id.btn_follow)
    void clickFollow() {
        if (DiycodeUtils.checkToken(this)) {
            mPresenter.followUser(mUsername);
        }
    }

    @OnClick(R.id.btn_block)
    void clickBlock() {
        if (DiycodeUtils.checkToken(this)) {
            mPresenter.blockUser(mUsername);
        }
    }

    @OnClick(R.id.ll_topic)
    void clickTopic() {
        ARouter.getInstance().build(TOPIC_LIST)
                .withInt(EXTRA_TOPIC_TYPE, TOPIC_CREATE)
                .withString(EXTRA_TOPIC_USER, mUsername)
                .navigation();
    }

    @OnClick(R.id.ll_favorite)
    void clickFavorite() {
        ARouter.getInstance().build(TOPIC_LIST)
                .withInt(EXTRA_TOPIC_TYPE, TOPIC_FAVORITES)
                .withString(EXTRA_TOPIC_USER, mUsername)
                .navigation();
    }

    @OnClick(R.id.ll_reply)
    void clickReply() {
        ARouter.getInstance().build(REPLY_LIST)
                .withString(EXTRA_REPLY_USER, mUsername)
                .navigation();
    }

    @OnClick(R.id.ll_share)
    void clickShare() {
        DiycodeUtils.openWebActivity("https://www.diycode.cc/" + mUsername + "/hacknews");
    }

    @OnClick(R.id.ll_follower)
    void clickFollower() {
        ARouter.getInstance().build(USER_LIST)
                .withInt(EXTRA_USER_TYPE, USER_FOLLOWER)
                .withString(EXTRA_USERNAME, mUsername)
                .navigation();
    }

    @OnClick(R.id.ll_following)
    void clickFollowing() {
        ARouter.getInstance().build(USER_LIST)
                .withInt(EXTRA_USER_TYPE, USER_FOLLOWING)
                .withString(EXTRA_USERNAME, mUsername)
                .navigation();
    }

    @OnClick(R.id.ll_block)
    void clickBlocked() {
        ARouter.getInstance().build(USER_LIST)
                .withInt(EXTRA_USER_TYPE, USER_BLOCK)
                .withString(EXTRA_USERNAME, mUsername)
                .navigation();
    }
}
