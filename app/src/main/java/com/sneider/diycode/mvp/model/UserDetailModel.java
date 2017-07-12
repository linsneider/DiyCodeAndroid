package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.UserDetailContract;
import com.sneider.diycode.mvp.model.api.service.UserService;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.User;

import javax.inject.Inject;

import io.reactivex.Observable;

@ActivityScope
public class UserDetailModel extends BaseModel implements UserDetailContract.Model {

    @Inject
    public UserDetailModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<User> getUserInfo(String username, boolean update) {
        Observable<User> user = mRepositoryManager.obtainRetrofitService(UserService.class)
                .getUserInfo(username);
        return user;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getUserInfo(user, new DynamicKey(username), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<User>, ObservableSource<User>>() {
//                    @Override
//                    public ObservableSource<User> apply(@NonNull Reply<User> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }

    @Override
    public Observable<Ok> followUser(String username) {
        Observable<Ok> ok = mRepositoryManager.obtainRetrofitService(UserService.class)
                .followUser(username);
        return ok;
    }

    @Override
    public Observable<Ok> blockUser(String username) {
        Observable<Ok> ok = mRepositoryManager.obtainRetrofitService(UserService.class)
                .blockUser(username);
        return ok;
    }
}
