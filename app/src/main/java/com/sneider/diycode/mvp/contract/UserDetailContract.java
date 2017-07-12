package com.sneider.diycode.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.User;

import io.reactivex.Observable;

public interface UserDetailContract {

    interface View extends IView {

        void onGetUserInfo(User user);

        void onFollowUser();

        void onBlockUser();
    }

    interface Model extends IModel {

        Observable<User> getUserInfo(String username, boolean update);

        Observable<Ok> followUser(String username);

        Observable<Ok> blockUser(String username);
    }
}
