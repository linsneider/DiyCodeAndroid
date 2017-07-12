package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.TopicListContract;
import com.sneider.diycode.mvp.model.api.service.TopicService;
import com.sneider.diycode.mvp.model.api.service.UserService;
import com.sneider.diycode.mvp.model.bean.Node;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.Topic;
import com.sneider.diycode.utils.Constant;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

@ActivityScope
public class TopicListModel extends BaseModel implements TopicListContract.Model {

    @Inject
    public TopicListModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<List<Topic>> getTopics(int nodeId, int offset, boolean update) {
        Observable<List<Topic>> topics = mRepositoryManager.obtainRetrofitService(TopicService.class)
                .getTopics(null, nodeId, offset, Constant.PAGE_SIZE);
        return topics;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getTopics(topics, new DynamicKeyGroup(nodeId, offset), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<Topic>>, ObservableSource<List<Topic>>>() {
//                    @Override
//                    public ObservableSource<List<Topic>> apply(@NonNull Reply<List<Topic>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }

    @Override
    public Observable<List<Topic>> getUserTopics(String username, int offset, boolean update) {
        Observable<List<Topic>> topics = mRepositoryManager.obtainRetrofitService(UserService.class)
                .getUserTopics(username, Constant.ORDER, offset, Constant.PAGE_SIZE);
        return topics;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getUserTopics(topics, new DynamicKey(offset), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<Topic>>, ObservableSource<List<Topic>>>() {
//                    @Override
//                    public ObservableSource<List<Topic>> apply(@NonNull Reply<List<Topic>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }

    @Override
    public Observable<List<Topic>> getUserFavorites(String username, int offset, boolean update) {
        Observable<List<Topic>> topics = mRepositoryManager.obtainRetrofitService(UserService.class)
                .getUserFavorites(username, offset, Constant.PAGE_SIZE);
        return topics;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getUserFavorites(topics, new DynamicKey(offset), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<Topic>>, ObservableSource<List<Topic>>>() {
//                    @Override
//                    public ObservableSource<List<Topic>> apply(@NonNull Reply<List<Topic>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
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
