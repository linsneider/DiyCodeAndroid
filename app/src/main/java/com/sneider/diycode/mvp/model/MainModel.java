package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.MainContract;
import com.sneider.diycode.mvp.model.api.service.NotificationService;
import com.sneider.diycode.mvp.model.bean.Count;

import javax.inject.Inject;

import io.reactivex.Observable;

@ActivityScope
public class MainModel extends BaseModel implements MainContract.Model {

    @Inject
    public MainModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<Count> getUnreadCount() {
        Observable<Count> count = mRepositoryManager.obtainRetrofitService(NotificationService.class)
                .getUnreadCount();
        return count;
    }
}
