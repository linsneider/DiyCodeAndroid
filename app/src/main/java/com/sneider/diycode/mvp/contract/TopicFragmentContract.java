package com.sneider.diycode.mvp.contract;

import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.Topic;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;

public interface TopicFragmentContract {

    interface View extends IView {

        void setAdapter(DefaultAdapter adapter);

        void onLoadMoreComplete();

        void onLoadMoreError();

        void onLoadMoreEnd();

        void onFavoriteSuccess();

        RxPermissions getRxPermissions();
    }

    interface Model extends IModel {

        Observable<List<Topic>> getTopics(int offset, boolean update);

        Observable<Ok> favoriteTopic(int id);
    }
}
