package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.AddReplyContract;
import com.sneider.diycode.mvp.model.api.service.CommonService;
import com.sneider.diycode.mvp.model.api.service.ProjectService;
import com.sneider.diycode.mvp.model.api.service.TopicService;
import com.sneider.diycode.mvp.model.bean.ImageResult;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.Reply;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.MultipartBody;

@ActivityScope
public class AddReplyModel extends BaseModel implements AddReplyContract.Model {

    @Inject
    public AddReplyModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<Reply> createTopicReply(int topicId, String body) {
        Observable<Reply> reply = mRepositoryManager.obtainRetrofitService(TopicService.class)
                .createTopicReply(topicId, body);
        return reply;
    }

    @Override
    public Observable<Reply> updateTopicReply(int topicId, String body) {
        Observable<Reply> reply = mRepositoryManager.obtainRetrofitService(TopicService.class)
                .updateTopicReply(topicId, body);
        return reply;
    }

    @Override
    public Observable<Reply> getTopicReply(int topicId) {
        Observable<Reply> reply = mRepositoryManager.obtainRetrofitService(TopicService.class)
                .getTopicReply(topicId);
        return reply;
    }

    @Override
    public Observable<Ok> deleteTopicReply(int id) {
        Observable<Ok> ok = mRepositoryManager.obtainRetrofitService(TopicService.class)
                .deleteTopicReply(id);
        return ok;
    }

    @Override
    public Observable<Reply> updateProjectReply(int id, String body) {
        Observable<Reply> reply = mRepositoryManager.obtainRetrofitService(ProjectService.class)
                .updateProjectReply(id, body);
        return reply;
    }

    @Override
    public Observable<Reply> getProjectReply(int projectId) {
        Observable<Reply> reply = mRepositoryManager.obtainRetrofitService(ProjectService.class)
                .getProjectReply(projectId);
        return reply;
    }

    @Override
    public Observable<Ok> deleteProjectReply(int id) {
        Observable<Ok> ok = mRepositoryManager.obtainRetrofitService(ProjectService.class)
                .deleteProjectReply(id);
        return ok;
    }

    @Override
    public Observable<ImageResult> uploadPhoto(MultipartBody.Part file) {
        Observable<ImageResult> url = mRepositoryManager.obtainRetrofitService(CommonService.class)
                .uploadPhoto(file);
        return url;
    }
}
