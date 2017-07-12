package com.sneider.diycode.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.sneider.diycode.mvp.model.bean.ImageResult;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.Reply;

import io.reactivex.Observable;
import okhttp3.MultipartBody;

public interface AddReplyContract {

    interface View extends IView {

        void onGetReply(String reply);

        void onUploadPhoto(String url);

        void showUploading();

        void hideUploading();
    }

    interface Model extends IModel {

        Observable<Reply> createTopicReply(int topicId, String body);

        Observable<Reply> updateTopicReply(int id, String body);

        Observable<Reply> getTopicReply(int topicId);

        Observable<Ok> deleteTopicReply(int id);

        Observable<Reply> updateProjectReply(int id, String body);

        Observable<Reply> getProjectReply(int projectId);

        Observable<Ok> deleteProjectReply(int id);

        Observable<ImageResult> uploadPhoto(MultipartBody.Part file);
    }
}
