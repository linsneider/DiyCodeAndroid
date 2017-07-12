package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.ProjectDetailContract;
import com.sneider.diycode.mvp.model.api.service.ProjectService;
import com.sneider.diycode.utils.Constant;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

@ActivityScope
public class ProjectDetailModel extends BaseModel implements ProjectDetailContract.Model {

    @Inject
    public ProjectDetailModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<List<com.sneider.diycode.mvp.model.bean.Reply>> getProjectReplies(int id, int offset, boolean update) {
        Observable<List<com.sneider.diycode.mvp.model.bean.Reply>> replies = mRepositoryManager.obtainRetrofitService(ProjectService.class)
                .getProjectReplies(id, offset, Constant.PAGE_SIZE);
        return replies;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getProjectReplies(replies, new DynamicKeyGroup(id, offset), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<com.sneider.diycode.mvp.model.bean.Reply>>, ObservableSource<List<com.sneider.diycode.mvp.model.bean.Reply>>>() {
//                    @Override
//                    public ObservableSource<List<com.sneider.diycode.mvp.model.bean.Reply>> apply(@NonNull Reply<List<com.sneider.diycode.mvp.model.bean.Reply>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }
}
