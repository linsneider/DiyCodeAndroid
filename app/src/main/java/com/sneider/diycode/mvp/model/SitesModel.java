package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.SitesContract;
import com.sneider.diycode.mvp.model.api.service.CommonService;
import com.sneider.diycode.mvp.model.bean.Sites;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

@ActivityScope
public class SitesModel extends BaseModel implements SitesContract.Model {

    @Inject
    public SitesModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<List<Sites>> getSites(int offset, boolean update) {
        Observable<List<Sites>> sites = mRepositoryManager.obtainRetrofitService(CommonService.class)
                .getSites();
        return sites;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getSites(sites, new DynamicKey(offset), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<Sites>>, ObservableSource<List<Sites>>>() {
//                    @Override
//                    public ObservableSource<List<Sites>> apply(@NonNull Reply<List<Sites>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }
}
