package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.TopicFragmentContract;
import com.sneider.diycode.mvp.model.api.service.TopicService;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.Topic;
import com.sneider.diycode.utils.Constant;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

@FragmentScope
public class TopicFragmentModel extends BaseModel implements TopicFragmentContract.Model {

    @Inject
    public TopicFragmentModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<List<Topic>> getTopics(int offset, boolean update) {
        Observable<List<Topic>> topics = mRepositoryManager.obtainRetrofitService(TopicService.class)
                .getTopics(null, null, offset, Constant.PAGE_SIZE);
        return topics;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getTopics(topics, new DynamicKeyGroup(0, offset), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<Topic>>, ObservableSource<List<Topic>>>() {
//                    @Override
//                    public ObservableSource<List<Topic>> apply(@NonNull Reply<List<Topic>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }

    @Override
    public Observable<Ok> favoriteTopic(int id) {
        return mRepositoryManager.obtainRetrofitService(TopicService.class)
                .favoriteTopic(id);
    }
}
