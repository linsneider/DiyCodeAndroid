package com.sneider.diycode.mvp.contract;

import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.User;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;

public interface UserListContract {

    interface View extends IView {

        void setAdapter(DefaultAdapter adapter);

        void setEmpty(boolean isEmpty);

        void onLoadMoreComplete();

        void onLoadMoreError();

        void onLoadMoreEnd();

        RxPermissions getRxPermissions();
    }

    interface Model extends IModel {

        Observable<List<User>> getFollowers(String username, int offset, boolean update);

        Observable<List<User>> getFollowings(String username, int offset, boolean update);

        Observable<List<User>> getBlockedUsers(String username, int offset, boolean update);

        Observable<Ok> unfollowUser(String username);

        Observable<Ok> unblockUser(String username);
    }
}
