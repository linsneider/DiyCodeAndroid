package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.TopicDetailContract;
import com.sneider.diycode.mvp.model.api.service.CommonService;
import com.sneider.diycode.mvp.model.api.service.TopicService;
import com.sneider.diycode.mvp.model.bean.Like;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.Topic;

import javax.inject.Inject;

import io.reactivex.Observable;

@ActivityScope
public class TopicDetailModel extends BaseModel implements TopicDetailContract.Model {

    @Inject
    public TopicDetailModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<Topic> getTopicDetail(int id, boolean update) {
        Observable<Topic> topic = mRepositoryManager.obtainRetrofitService(TopicService.class)
                .getTopicDetail(id);
        return topic;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getTopicDetail(topic, new DynamicKey(id), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<Topic>, ObservableSource<Topic>>() {
//                    @Override
//                    public ObservableSource<Topic> apply(@NonNull Reply<Topic> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }

    @Override
    public Observable<Like> likeTopic(int id) {
        Observable<Like> like = mRepositoryManager.obtainRetrofitService(CommonService.class)
                .like("topic", id);
        return like;
    }

    @Override
    public Observable<Like> unlikeTopic(int id) {
        Observable<Like> like = mRepositoryManager.obtainRetrofitService(CommonService.class)
                .unlike("topic", id);
        return like;
    }

    @Override
    public Observable<Ok> favoriteTopic(int id) {
        Observable<Ok> ok = mRepositoryManager.obtainRetrofitService(TopicService.class)
                .favoriteTopic(id);
        return ok;
    }

    @Override
    public Observable<Ok> unfavoriteTopic(int id) {
        Observable<Ok> ok = mRepositoryManager.obtainRetrofitService(TopicService.class)
                .unfavoriteTopic(id);
        return ok;
    }
}
