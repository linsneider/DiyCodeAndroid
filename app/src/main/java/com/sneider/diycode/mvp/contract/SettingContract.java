package com.sneider.diycode.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.sneider.diycode.mvp.model.bean.Ok;

import io.reactivex.Observable;

public interface SettingContract {

    interface View extends IView {

    }

    interface Model extends IModel {

        Observable<Ok> logout(String platform, String token);
    }
}
