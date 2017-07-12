package com.sneider.diycode.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.sneider.diycode.mvp.model.bean.Count;

import io.reactivex.Observable;

public interface MainContract {

    interface View extends IView {

    }

    interface Model extends IModel {

        Observable<Count> getUnreadCount();
    }
}
