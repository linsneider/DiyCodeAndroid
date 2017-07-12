package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.NewsFragmentContract;
import com.sneider.diycode.mvp.model.api.service.NewsService;
import com.sneider.diycode.mvp.model.bean.News;
import com.sneider.diycode.utils.Constant;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

@FragmentScope
public class NewsFragmentModel extends BaseModel implements NewsFragmentContract.Model {

    @Inject
    public NewsFragmentModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<List<News>> getNews(int offset, boolean update) {
        Observable<List<News>> news = mRepositoryManager.obtainRetrofitService(NewsService.class)
                .getNews(null, offset, Constant.PAGE_SIZE);
        return news;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getNews(news, new DynamicKeyGroup(0, offset), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<News>>, ObservableSource<List<News>>>() {
//                    @Override
//                    public ObservableSource<List<News>> apply(@NonNull Reply<List<News>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }
}
