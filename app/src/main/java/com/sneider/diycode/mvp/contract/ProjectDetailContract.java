package com.sneider.diycode.mvp.contract;

import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.sneider.diycode.mvp.model.bean.Reply;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;

public interface ProjectDetailContract {

    interface View extends IView {

        void setAdapter(DefaultAdapter adapter);

        void onLoadMoreComplete();

        void onLoadMoreError();

        void onLoadMoreEnd();

        void smoothToPosition(int position);

        RxPermissions getRxPermissions();
    }

    interface Model extends IModel {

        Observable<List<Reply>> getProjectReplies(int id, int offset, boolean update);
    }
}
