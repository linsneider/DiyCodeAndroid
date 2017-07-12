package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.ReplyListContract;
import com.sneider.diycode.mvp.model.api.service.UserService;
import com.sneider.diycode.utils.Constant;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

@ActivityScope
public class ReplyListModel extends BaseModel implements ReplyListContract.Model {

    @Inject
    public ReplyListModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<List<com.sneider.diycode.mvp.model.bean.Reply>> getUserReplies(String username, int offset, boolean update) {
        Observable<List<com.sneider.diycode.mvp.model.bean.Reply>> replies = mRepositoryManager.obtainRetrofitService(UserService.class)
                .getUserReplies(username, Constant.ORDER, offset, Constant.PAGE_SIZE);
        return replies;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getUserReplies(replies, new DynamicKeyGroup(username, offset), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<com.sneider.diycode.mvp.model.bean.Reply>>, ObservableSource<List<com.sneider.diycode.mvp.model.bean.Reply>>>() {
//                    @Override
//                    public ObservableSource<List<com.sneider.diycode.mvp.model.bean.Reply>> apply(@NonNull Reply<List<com.sneider.diycode.mvp.model.bean.Reply>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }
}
