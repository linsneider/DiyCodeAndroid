package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.UserListContract;
import com.sneider.diycode.mvp.model.api.service.UserService;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.User;
import com.sneider.diycode.utils.Constant;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

@ActivityScope
public class UserListModel extends BaseModel implements UserListContract.Model {

    @Inject
    public UserListModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<List<User>> getFollowers(String username, int offset, boolean update) {
        Observable<List<User>> users = mRepositoryManager.obtainRetrofitService(UserService.class)
                .getFollowers(username, offset, Constant.PAGE_SIZE);
        return users;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getFollowers(users, new DynamicKeyGroup(username, offset), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<User>>, ObservableSource<List<User>>>() {
//                    @Override
//                    public ObservableSource<List<User>> apply(@NonNull Reply<List<User>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }

    @Override
    public Observable<List<User>> getFollowings(String username, int offset, boolean update) {
        Observable<List<User>> users = mRepositoryManager.obtainRetrofitService(UserService.class)
                .getFollowings(username, offset, Constant.PAGE_SIZE);
        return users;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getFollowings(users, new DynamicKeyGroup(username, offset), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<User>>, ObservableSource<List<User>>>() {
//                    @Override
//                    public ObservableSource<List<User>> apply(@NonNull Reply<List<User>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }

    @Override
    public Observable<List<User>> getBlockedUsers(String username, int offset, boolean update) {
        Observable<List<User>> users = mRepositoryManager.obtainRetrofitService(UserService.class)
                .getBlockedUsers(username, offset, Constant.PAGE_SIZE);
        return users;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getBlockedUsers(users, new DynamicKeyGroup(username, offset), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<User>>, ObservableSource<List<User>>>() {
//                    @Override
//                    public ObservableSource<List<User>> apply(@NonNull Reply<List<User>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }

    @Override
    public Observable<Ok> unfollowUser(String username) {
        Observable<Ok> ok = mRepositoryManager.obtainRetrofitService(UserService.class)
                .unfollowUser(username);
        return ok;
    }

    @Override
    public Observable<Ok> unblockUser(String username) {
        Observable<Ok> ok = mRepositoryManager.obtainRetrofitService(UserService.class)
                .unblockUser(username);
        return ok;
    }
}
