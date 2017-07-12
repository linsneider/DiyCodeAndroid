package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.NewsDetailContract;
import com.sneider.diycode.mvp.model.api.service.NewsService;
import com.sneider.diycode.utils.Constant;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

@ActivityScope
public class NewsDetailModel extends BaseModel implements NewsDetailContract.Model {

    @Inject
    public NewsDetailModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<List<com.sneider.diycode.mvp.model.bean.Reply>> getNewsReplies(int id, int offset, boolean update) {
        Observable<List<com.sneider.diycode.mvp.model.bean.Reply>> replies = mRepositoryManager.obtainRetrofitService(NewsService.class)
                .getNewsReplies(id, offset, Constant.PAGE_SIZE);
        return replies;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getNewsReplies(replies, new DynamicKeyGroup(id, offset), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<com.sneider.diycode.mvp.model.bean.Reply>>, ObservableSource<List<com.sneider.diycode.mvp.model.bean.Reply>>>() {
//                    @Override
//                    public ObservableSource<List<com.sneider.diycode.mvp.model.bean.Reply>> apply(@NonNull Reply<List<com.sneider.diycode.mvp.model.bean.Reply>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }
}
