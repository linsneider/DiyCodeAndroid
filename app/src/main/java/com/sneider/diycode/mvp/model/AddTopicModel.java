package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.AddTopicContract;
import com.sneider.diycode.mvp.model.api.service.CommonService;
import com.sneider.diycode.mvp.model.api.service.TopicService;
import com.sneider.diycode.mvp.model.bean.ImageResult;
import com.sneider.diycode.mvp.model.bean.Node;
import com.sneider.diycode.mvp.model.bean.Topic;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.MultipartBody;

@ActivityScope
public class AddTopicModel extends BaseModel implements AddTopicContract.Model {

    @Inject
    public AddTopicModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<Topic> createTopic(String title, String body, int nodeId) {
        Observable<Topic> topic = mRepositoryManager.obtainRetrofitService(TopicService.class)
                .createTopic(title, body, nodeId);
        return topic;
    }

    @Override
    public Observable<Topic> updateTopic(int id, String title, String body, int nodeId) {
        Observable<Topic> topic = mRepositoryManager.obtainRetrofitService(TopicService.class)
                .updateTopic(id, title, body, nodeId);
        return topic;
    }

    @Override
    public Observable<ImageResult> uploadPhoto(MultipartBody.Part file) {
        Observable<ImageResult> url = mRepositoryManager.obtainRetrofitService(CommonService.class)
                .uploadPhoto(file);
        return url;
    }

    @Override
    public Observable<List<Node>> getNodes(boolean update) {
        Observable<List<Node>> nodes = mRepositoryManager.obtainRetrofitService(TopicService.class)
                .getNodes();
        return nodes;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getNodes(nodes, new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<Node>>, ObservableSource<List<Node>>>() {
//                    @Override
//                    public ObservableSource<List<Node>> apply(@NonNull Reply<List<Node>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }
}
