package com.sneider.diycode.mvp.contract;

import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.sneider.diycode.mvp.model.bean.Node;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.Section;
import com.sneider.diycode.mvp.model.bean.Topic;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;

public interface TopicListContract {

    interface View extends IView {

        void setAdapter(DefaultAdapter adapter);

        void setEmpty(boolean isEmpty);

        void onLoadMoreComplete();

        void onLoadMoreError();

        void onLoadMoreEnd();

        void onFavoriteSuccess();

        void onGetNodes(List<Section> sections);

        RxPermissions getRxPermissions();
    }

    interface Model extends IModel {

        Observable<List<Topic>> getTopics(int nodeId, int offset, boolean update);

        Observable<List<Topic>> getUserTopics(String username, int offset, boolean update);

        Observable<List<Topic>> getUserFavorites(String username, int offset, boolean update);

        Observable<Ok> favoriteTopic(int id);

        Observable<Ok> unfavoriteTopic(int id);

        Observable<List<Node>> getNodes(boolean update);
    }
}
