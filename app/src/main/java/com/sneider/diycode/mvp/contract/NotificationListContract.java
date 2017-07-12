package com.sneider.diycode.mvp.contract;

import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.sneider.diycode.mvp.model.bean.Count;
import com.sneider.diycode.mvp.model.bean.Notification;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;

public interface NotificationListContract {

    interface View extends IView {

        void setAdapter(DefaultAdapter adapter);

        void setEmpty(boolean isEmpty);

        void onLoadMoreComplete();

        void onLoadMoreError();

        void onLoadMoreEnd();

        void setSubtitle(String subtitle);

        RxPermissions getRxPermissions();
    }

    interface Model extends IModel {

        Observable<List<Notification>> getNotifications(int offset, boolean update);

        Observable<Ok> deleteNotification(int id);

        Observable<Ok> deleteAllNotifications();

        Observable<Count> getUnreadCount();

        Observable<Ok> readNotification(int[] ids);
    }
}
