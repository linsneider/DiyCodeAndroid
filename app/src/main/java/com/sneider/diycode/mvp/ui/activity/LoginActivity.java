package com.sneider.diycode.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ToastUtils;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.di.component.DaggerLoginComponent;
import com.sneider.diycode.di.module.LoginModule;
import com.sneider.diycode.event.LoginEvent;
import com.sneider.diycode.mvp.contract.LoginContract;
import com.sneider.diycode.mvp.presenter.LoginPresenter;
import com.sneider.diycode.utils.DiycodeUtils;

import org.simple.eventbus.EventBus;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.OnClick;

import static com.sneider.diycode.app.ARouterPaths.USER_LOGIN;

@Route(path = USER_LOGIN)
public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.View {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.username) TextInputLayout mUsername;
    @BindView(R.id.et_username) EditText mEtUsername;
    @BindView(R.id.password) TextInputLayout mPassword;
    @BindView(R.id.et_password) EditText mEtPassword;

    private MaterialDialog mDialog;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerLoginComponent.builder().appComponent(appComponent)
                .loginModule(new LoginModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_login;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mDialog = new MaterialDialog.Builder(this).content(R.string.login_ing).progress(true, 0).build();
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setTitle(R.string.login);
        setSupportActionBar(mToolbar);
    }

    @Override
    public void showLoading() {
        mDialog.show();
    }

    @Override
    public void hideLoading() {
        mDialog.cancel();
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
    public void setUsernameError() {
        mUsername.setError(getString(R.string.please_input_username));
    }

    @Override
    public void setPasswordError() {
        mUsername.setErrorEnabled(false);
        mPassword.setError(getString(R.string.please_input_password));
    }

    @Override
    public void resetError() {
        mUsername.setErrorEnabled(false);
        mPassword.setErrorEnabled(false);
    }

    @Override
    public void loginSuccess(String username) {
        ToastUtils.showShort(MessageFormat.format(getString(R.string.login_success), username));
        EventBus.getDefault().post(new LoginEvent());
        finish();
    }

    @Override
    public void loginFailed() {
        ToastUtils.showShort(R.string.login_failed);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_login)
    void clickLogin() {
        String username = mEtUsername.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        mPresenter.validateCredentials(username, password);
    }

    @OnClick(R.id.btn_register)
    void clickRegister() {
        DiycodeUtils.openWebActivity("https://www.diycode.cc/account/sign_up");
    }

    @OnClick(R.id.btn_find_password)
    void clickFindPassword() {
        DiycodeUtils.openWebActivity("https://www.diycode.cc/account/password/new");
    }
}
