package com.sneider.diycode.mvp.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.di.component.DaggerSettingComponent;
import com.sneider.diycode.di.module.SettingModule;
import com.sneider.diycode.event.LogoutEvent;
import com.sneider.diycode.mvp.contract.SettingContract;
import com.sneider.diycode.mvp.presenter.SettingPresenter;
import com.sneider.diycode.utils.CacheDataUtils;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.PrefUtils;
import com.sneider.diycode.widget.SettingRowView;

import org.simple.eventbus.EventBus;

import java.text.MessageFormat;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;

import static com.sneider.diycode.app.ARouterPaths.PUBLIC_SETTING;

@Route(path = PUBLIC_SETTING)
public class SettingActivity extends BaseActivity<SettingPresenter> implements SettingContract.View {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.btn_clear_cache) SettingRowView mBtnClearCache;
    @BindView(R.id.btn_check_update) SettingRowView mBtnCheckUpdate;
    @BindView(R.id.btn_logout) LinearLayout mBtnLogout;

    @BindColor(R.color.color_4d4d4d) int color_4d4d4d;
    @BindColor(R.color.color_999999) int color_999999;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerSettingComponent.builder().appComponent(appComponent)
                .settingModule(new SettingModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_setting;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mToolbar.setTitle(R.string.setting);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(mToolbar);

        setupAppCache();
        setupAppVersion();

        if (DiycodeUtils.getUser(this) != null) {
            mBtnLogout.setVisibility(View.VISIBLE);
        }
    }

    private void setupAppCache() {
        mBtnClearCache.setSettingDescription(
                MessageFormat.format(getString(R.string.total_cache_description), CacheDataUtils.getTotalCacheSize(this)));
    }

    private void setupAppVersion() {
        mBtnCheckUpdate.setSettingDescription(
                MessageFormat.format(getString(R.string.current_version), AppUtils.getAppVersionName()));

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        PgyUpdateManager.unregister();
        super.onDestroy();
    }

    @OnClick(R.id.btn_clear_cache)
    void clearCache() {
        CacheDataUtils.clearAllCache(this);
        setupAppCache();
    }

    @OnClick(R.id.btn_check_update)
    void checkUpdate() {
        PgyUpdateManager.register(this, "com.sneider.diycode.fileprovider", new UpdateManagerListener() {
            @Override
            public void onNoUpdateAvailable() {
                ToastUtils.showShort(R.string.app_is_latest);
            }

            @Override
            public void onUpdateAvailable(String result) {
                AppBean appBean = getAppBeanFromString(result);
                new MaterialDialog.Builder(SettingActivity.this)
                        .title(R.string.find_new_version)
                        .content(appBean.getReleaseNote())
                        .contentColor(color_4d4d4d)
                        .positiveText(R.string.update)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                startDownloadTask(SettingActivity.this, appBean.getDownloadURL());
                            }
                        })
                        .negativeText(R.string.cancel)
                        .negativeColor(color_999999)
                        .show();
            }
        });
    }

    @OnClick(R.id.btn_feedback)
    void feedback() {
        DiycodeUtils.openWebActivity("https://github.com/linsneider/DiyCodeAndroid/issues");
    }

    @OnClick(R.id.btn_contact)
    void contact() {
        if (AppUtils.isInstallApp("com.tencent.mobileqq")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=827173000")));
        } else {
            ToastUtils.showShort(R.string.contact_hint);
        }
    }

    @OnClick(R.id.btn_logout)
    void clickLogout() {
        new MaterialDialog.Builder(this)
                .content(R.string.confirm_logout)
                .contentColor(color_4d4d4d)
                .positiveText(R.string.confirm)
                .onPositive((dialog, which) -> {
                    DiycodeUtils.setToken(this, null);
                    DiycodeUtils.setUser(this, null);
                    PrefUtils.getInstance(this).put("token", "");
                    PrefUtils.getInstance(this).put("user", "");
                    EventBus.getDefault().post(new LogoutEvent());
                    finish();
                })
                .negativeText(R.string.cancel)
                .negativeColor(color_999999)
                .show();
    }
}
