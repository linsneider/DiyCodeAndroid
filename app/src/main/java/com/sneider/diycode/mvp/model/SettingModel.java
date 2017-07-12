package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.SettingContract;
import com.sneider.diycode.mvp.model.api.service.CommonService;
import com.sneider.diycode.mvp.model.bean.Ok;

import javax.inject.Inject;

import io.reactivex.Observable;

@ActivityScope
public class SettingModel extends BaseModel implements SettingContract.Model {

    @Inject
    public SettingModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<Ok> logout(String platform, String token) {
        Observable<Ok> ok = mRepositoryManager.obtainRetrofitService(CommonService.class)
                .deletedevice(platform, token);
        return ok;
    }
}
