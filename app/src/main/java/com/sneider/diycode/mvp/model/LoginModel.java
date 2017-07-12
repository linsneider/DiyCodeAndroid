package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.LoginContract;
import com.sneider.diycode.mvp.model.api.service.CommonService;
import com.sneider.diycode.mvp.model.api.service.UserService;
import com.sneider.diycode.mvp.model.bean.Token;
import com.sneider.diycode.mvp.model.bean.User;
import com.sneider.diycode.utils.Constant;
import com.sneider.diycode.utils.DiycodeUtils;

import javax.inject.Inject;

import io.reactivex.Observable;

@ActivityScope
public class LoginModel extends BaseModel implements LoginContract.Model {

    @Inject
    public LoginModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<Token> login(String username, String password) {
        Observable<Token> token = mRepositoryManager.obtainRetrofitService(CommonService.class)
                .getToken(DiycodeUtils.getClientId(), DiycodeUtils.getClientSecret(),
                        Constant.GRANT_TYPE_PASSWORD, username, password);
        return token;
    }

    @Override
    public Observable<User> getUserInfo(String username) {
        Observable<User> user = mRepositoryManager.obtainRetrofitService(UserService.class)
                .getUserInfo(username);
        return user;
    }
}
