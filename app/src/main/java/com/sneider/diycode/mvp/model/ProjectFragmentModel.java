package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.ProjectFragmentContract;
import com.sneider.diycode.mvp.model.api.service.ProjectService;
import com.sneider.diycode.mvp.model.bean.Project;
import com.sneider.diycode.utils.Constant;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

@FragmentScope
public class ProjectFragmentModel extends BaseModel implements ProjectFragmentContract.Model {

    @Inject
    public ProjectFragmentModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<List<Project>> getProjects(int offset, boolean update) {
        Observable<List<Project>> projects = mRepositoryManager.obtainRetrofitService(ProjectService.class)
                .getProjects(null, offset, Constant.PAGE_SIZE);
        return projects;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getProjects(projects, new DynamicKey(offset), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<Project>>, ObservableSource<List<Project>>>() {
//                    @Override
//                    public ObservableSource<List<Project>> apply(@NonNull Reply<List<Project>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }
}
