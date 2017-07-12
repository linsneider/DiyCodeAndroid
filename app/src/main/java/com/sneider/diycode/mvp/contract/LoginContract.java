package com.sneider.diycode.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.sneider.diycode.mvp.model.bean.Token;
import com.sneider.diycode.mvp.model.bean.User;

import io.reactivex.Observable;

public interface LoginContract {

    interface View extends IView {

        void setUsernameError();

        void setPasswordError();

        void resetError();

        void loginSuccess(String username);

        void loginFailed();
    }

    interface Model extends IModel {

        Observable<Token> login(String username, String password);

        Observable<User> getUserInfo(String username);
    }
}
