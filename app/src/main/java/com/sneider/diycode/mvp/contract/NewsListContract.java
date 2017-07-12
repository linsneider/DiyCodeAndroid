package com.sneider.diycode.mvp.contract;

import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.sneider.diycode.mvp.model.bean.News;
import com.sneider.diycode.mvp.model.bean.NewsNode;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;

public interface NewsListContract {

    interface View extends IView {

        void setAdapter(DefaultAdapter adapter);

        void setEmpty(boolean isEmpty);

        void onLoadMoreComplete();

        void onLoadMoreError();

        void onLoadMoreEnd();

        void onGetNodes(List<NewsNode> nodes);

        RxPermissions getRxPermissions();
    }

    interface Model extends IModel {

        Observable<List<News>> getNews(int nodeId, int offset, boolean update);

        Observable<List<NewsNode>> getNewsNodes(boolean update);
    }
}
