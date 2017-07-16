package com.sneider.diycode.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.jess.arms.base.AdapterViewPager;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.jess.arms.widget.imageloader.glide.GlideImageConfig;
import com.sneider.diycode.R;
import com.sneider.diycode.di.component.DaggerMainComponent;
import com.sneider.diycode.di.module.MainModule;
import com.sneider.diycode.event.GetUnreadCountEvent;
import com.sneider.diycode.event.LoginEvent;
import com.sneider.diycode.event.LogoutEvent;
import com.sneider.diycode.mvp.contract.MainContract;
import com.sneider.diycode.mvp.model.bean.User;
import com.sneider.diycode.mvp.presenter.MainPresenter;
import com.sneider.diycode.mvp.ui.fragment.NewsFragment;
import com.sneider.diycode.mvp.ui.fragment.ProjectFragment;
import com.sneider.diycode.mvp.ui.fragment.TopicFragment;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.GlideCircleTransform;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sneider.diycode.app.ARouterPaths.NEWS_ADD;
import static com.sneider.diycode.app.ARouterPaths.PUBLIC_SETTING;
import static com.sneider.diycode.app.ARouterPaths.PUBLIC_SITES;
import static com.sneider.diycode.app.ARouterPaths.REPLY_LIST;
import static com.sneider.diycode.app.ARouterPaths.TOPIC_ADD;
import static com.sneider.diycode.app.ARouterPaths.TOPIC_LIST;
import static com.sneider.diycode.app.ARouterPaths.USER_DETAIL;
import static com.sneider.diycode.app.ARouterPaths.USER_NOTIFICATION;
import static com.sneider.diycode.mvp.ui.activity.ReplyListActivity.EXTRA_REPLY_USER;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.EXTRA_TOPIC_TYPE;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.EXTRA_TOPIC_USER;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.TOPIC_CREATE;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.TOPIC_FAVORITES;
import static com.sneider.diycode.mvp.ui.activity.UserDetailActivity.EXTRA_USERNAME;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.nav_view) NavigationView mNavigationView;

    SearchView mSearchView;
    ImageView mIvAvatar;
    TextView mTvUsername;

    private AppComponent mAppComponent;
    private User mUser;
    private boolean mHasNotification;
    private long mExitTime;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        mAppComponent = appComponent;
        DaggerMainComponent.builder().appComponent(appComponent)
                .mainModule(new MainModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mToolbar.setLogo(R.drawable.logo_small);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new TopicFragment());
        fragments.add(new ProjectFragment());
        fragments.add(new NewsFragment());
        String[] titles = new String[]{getString(R.string.topics), getString(R.string.projects), getString(R.string.news)};
        mViewPager.setAdapter(new AdapterViewPager(getSupportFragmentManager(), fragments, titles));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mFab.setVisibility(position == 1 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout.setupWithViewPager(mViewPager);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = mNavigationView.getHeaderView(0);
        mIvAvatar = ButterKnife.findById(headerView, R.id.iv_avatar);
        mTvUsername = ButterKnife.findById(headerView, R.id.tv_username);
        mIvAvatar.setOnClickListener(v -> {
            if (DiycodeUtils.checkToken(MainActivity.this)) {
                mUser = DiycodeUtils.getUser(this);
                if (mUser != null) {
                    ARouter.getInstance().build(USER_DETAIL)
                            .withString(EXTRA_USERNAME, mUser.getLogin())
                            .navigation();
                }
            }
        });
        mUser = DiycodeUtils.getUser(this);
        if (mUser != null) {
            String avatarUrl = mUser.getAvatar_url();
            if (avatarUrl.contains("diycode"))
                avatarUrl = avatarUrl.replace("large_avatar", "avatar");
            mAppComponent.imageLoader().loadImage(mAppComponent.application(), GlideImageConfig.builder()
                    .transformation(new GlideCircleTransform(mAppComponent.application()))
                    .url(avatarUrl).imageView(mIvAvatar).build());
            mTvUsername.setText(mUser.getLogin());

            mPresenter.getUnreadCount();
        } else {
            Glide.with(mAppComponent.application()).load(R.mipmap.ic_launcher)
                    .transform(new GlideCircleTransform(mAppComponent.application())).into(mIvAvatar);
            mTvUsername.setText(R.string.app_name);
        }

        mNavigationView.setNavigationItemSelectedListener(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_main_activity, menu);
        if (mHasNotification) {
            menu.findItem(R.id.action_notification).setIcon(R.drawable.ic_notification_red);
        }
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setQueryHint(getString(R.string.search_content));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                DiycodeUtils.openWebActivity("https://www.diycode.cc/search?q=" + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_notification) {
            if (DiycodeUtils.checkToken(this)) {
                ARouter.getInstance().build(USER_NOTIFICATION).navigation();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_my_topic) {
            if (DiycodeUtils.checkToken(this)) {
                ARouter.getInstance().build(TOPIC_LIST)
                        .withInt(EXTRA_TOPIC_TYPE, TOPIC_CREATE)
                        .withString(EXTRA_TOPIC_USER, DiycodeUtils.getUser(this).getLogin())
                        .navigation();
            }
        } else if (id == R.id.nav_my_favorite) {
            if (DiycodeUtils.checkToken(this)) {
                ARouter.getInstance().build(TOPIC_LIST)
                        .withInt(EXTRA_TOPIC_TYPE, TOPIC_FAVORITES)
                        .withString(EXTRA_TOPIC_USER, DiycodeUtils.getUser(this).getLogin())
                        .navigation();
            }
        } else if (id == R.id.nav_my_reply) {
            if (DiycodeUtils.checkToken(this)) {
                ARouter.getInstance().build(REPLY_LIST)
                        .withString(EXTRA_REPLY_USER, DiycodeUtils.getUser(this).getLogin())
                        .navigation();
            }
        } else if (id == R.id.nav_my_share) {
            if (DiycodeUtils.checkToken(this)) {
                DiycodeUtils.openWebActivity("https://www.diycode.cc/" + DiycodeUtils.getUser(this).getLogin() + "/hacknews");
            }
        } else if (id == R.id.nav_github_ranking) {
            DiycodeUtils.openWebActivity("https://www.diycode.cc/trends");
        } else if (id == R.id.nav_wiki) {
            DiycodeUtils.openWebActivity("https://www.diycode.cc/wiki");
        } else if (id == R.id.nav_sites) {
            ARouter.getInstance().build(PUBLIC_SITES).navigation();
        } else if (id == R.id.nav_setting) {
            ARouter.getInstance().build(PUBLIC_SETTING).navigation();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if ((System.currentTimeMillis() - mExitTime) > 2 * 1000) {
                Toast.makeText(getApplicationContext(), getString(R.string.exit) + getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }

    @OnClick(R.id.fab)
    void clickFab() {
        if (DiycodeUtils.checkToken(this)) {
            switch (mViewPager.getCurrentItem()) {
                case 0:
                    ARouter.getInstance().build(TOPIC_ADD).navigation();
                    break;
                case 2:
                    ARouter.getInstance().build(NEWS_ADD).navigation();
                    break;
                default:
                    break;
            }
        }
    }

    @Subscriber
    private void onLoginSuccess(LoginEvent event) {
        mUser = DiycodeUtils.getUser(this);
        if (mUser != null) {
            String avatarUrl = mUser.getAvatar_url();
            if (avatarUrl.contains("diycode"))
                avatarUrl = avatarUrl.replace("large_avatar", "avatar");
            mAppComponent.imageLoader().loadImage(mAppComponent.application(), GlideImageConfig.builder()
                    .transformation(new GlideCircleTransform(mAppComponent.application()))
                    .url(avatarUrl).imageView(mIvAvatar).build());
            mTvUsername.setText(mUser.getLogin());
        }

        mPresenter.getUnreadCount();
    }

    @Subscriber
    private void onLogoutSuccess(LogoutEvent event) {
        Glide.with(mAppComponent.application()).load(R.mipmap.ic_launcher)
                .transform(new GlideCircleTransform(mAppComponent.application())).into(mIvAvatar);
        mTvUsername.setText(R.string.app_name);

        mHasNotification = false;
        invalidateOptionsMenu();
    }

    @Subscriber
    private void onGetUnread(GetUnreadCountEvent event) {
        mHasNotification = event.hasUnread;
        invalidateOptionsMenu();
    }
}
