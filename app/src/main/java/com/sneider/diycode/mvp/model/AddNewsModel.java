package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.AddNewsContract;
import com.sneider.diycode.mvp.model.api.service.NewsService;
import com.sneider.diycode.mvp.model.bean.News;
import com.sneider.diycode.mvp.model.bean.NewsNode;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

@ActivityScope
public class AddNewsModel extends BaseModel implements AddNewsContract.Model {

    @Inject
    public AddNewsModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<News> createNews(String title, String link, int nodeId) {
        Observable<News> news = mRepositoryManager.obtainRetrofitService(NewsService.class)
                .createNews(title, link, nodeId);
        return news;
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
