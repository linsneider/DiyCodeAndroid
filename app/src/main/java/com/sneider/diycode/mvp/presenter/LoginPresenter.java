package com.sneider.diycode.mvp.presenter;

import android.app.Application;
import android.text.TextUtils;

import com.jess.arms.base.App;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.sneider.diycode.mvp.contract.LoginContract;
import com.sneider.diycode.mvp.model.bean.Token;
import com.sneider.diycode.mvp.model.bean.User;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.PrefUtils;
import com.sneider.diycode.utils.RxUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

@ActivityScope
public class LoginPresenter extends BasePresenter<LoginContract.Model, LoginContract.View> {

    private Application mApplication;
    private AppManager mAppManager;
    private RxErrorHandler mErrorHandler;

    @Inject
    public LoginPresenter(LoginContract.Model model, LoginContract.View rootView, RxErrorHandler handler, AppManager appManager, Application application) {
        super(model, rootView);
        mApplication = application;
        mAppManager = appManager;
        mErrorHandler = handler;
    }

    public void validateCredentials(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            mRootView.setUsernameError();
        } else if (TextUtils.isEmpty(password)) {
            mRootView.setPasswordError();
        } else {
            mRootView.resetError();
            login(username, password);
        }
    }

    private void login(String username, String password) {
        mModel.login(username, password)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Token>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        mRootView.hideLoading();
                        mRootView.loginFailed();
                    }

                    @Override
                    public void onNext(@NonNull Token token) {
                        DiycodeUtils.setToken(mApplication, token);
                        String tokenJson = ((App) mApplication).getAppComponent().gson().toJson(token);
                        try {
//                            tokenJson = KeyStoreHelper.encrypt(Constant.KEYSTORE_KEY_ALIAS, tokenJson);
//                            Log.e(TAG, "tokenJson=====" + tokenJson);
                            PrefUtils.getInstance(mApplication).put("token", tokenJson);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getUserInfo(username);
                    }
                });
    }

    private void getUserInfo(String username) {
        mModel.getUserInfo(username)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> mRootView.hideLoading())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<User>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        mRootView.loginFailed();
                    }

                    @Override
                    public void onNext(@NonNull User user) {
                        DiycodeUtils.setUser(mApplication, user);
                        String userJson = ((App) mApplication).getAppComponent().gson().toJson(user);
                        try {
//                            userJson = KeyStoreHelper.encrypt(Constant.KEYSTORE_KEY_ALIAS, userJson);
                            PrefUtils.getInstance(mApplication).put("user", userJson);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mRootView.loginSuccess(user.getLogin());
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mErrorHandler = null;
        mAppManager = null;
        mApplication = null;
    }
}
