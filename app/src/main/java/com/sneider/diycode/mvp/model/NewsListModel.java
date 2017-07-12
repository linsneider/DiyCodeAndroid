package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.NewsListContract;
import com.sneider.diycode.mvp.model.api.service.NewsService;
import com.sneider.diycode.mvp.model.bean.News;
import com.sneider.diycode.mvp.model.bean.NewsNode;
import com.sneider.diycode.utils.Constant;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

@ActivityScope
public class NewsListModel extends BaseModel implements NewsListContract.Model {

    @Inject
    public NewsListModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<List<News>> getNews(int nodeId, int offset, boolean update) {
        Observable<List<News>> news = mRepositoryManager.obtainRetrofitService(NewsService.class)
                .getNews(nodeId, offset, Constant.PAGE_SIZE);
        return news;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getNews(news, new DynamicKeyGroup(nodeId, offset), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<News>>, ObservableSource<List<News>>>() {
//                    @Override
//                    public ObservableSource<List<News>> apply(@NonNull Reply<List<News>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }

    @Override
    public Observable<List<NewsNode>> getNewsNodes(boolean update) {
        Observable<List<NewsNode>> nodes = mRepositoryManager.obtainRetrofitService(NewsService.class)
                .getNewsNodes();
        return nodes;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getNewsNodes(nodes, new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<NewsNode>>, ObservableSource<List<NewsNode>>>() {
//                    @Override
//                    public ObservableSource<List<NewsNode>> apply(@NonNull Reply<List<NewsNode>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }
}
