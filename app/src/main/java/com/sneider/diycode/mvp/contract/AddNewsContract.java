package com.sneider.diycode.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.sneider.diycode.mvp.model.bean.News;
import com.sneider.diycode.mvp.model.bean.NewsNode;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;

public interface AddNewsContract {

    interface View extends IView {

        void onGetNodes(List<NewsNode> nodes);

        RxPermissions getRxPermissions();
    }

    interface Model extends IModel {

        Observable<News> createNews(String title, String link, int nodeId);

        Observable<List<NewsNode>> getNewsNodes(boolean update);
    }
}
