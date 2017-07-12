package com.sneider.diycode.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.sneider.diycode.mvp.model.bean.ImageResult;
import com.sneider.diycode.mvp.model.bean.Node;
import com.sneider.diycode.mvp.model.bean.Section;
import com.sneider.diycode.mvp.model.bean.Topic;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;

public interface AddTopicContract {

    interface View extends IView {

        void onGetNodes(List<Section> sections);

        void onUploadPhoto(String url);

        void showUploading();

        void hideUploading();

        RxPermissions getRxPermissions();
    }

    interface Model extends IModel {

        Observable<Topic> createTopic(String title, String body, int nodeId);

        Observable<Topic> updateTopic(int id, String title, String body, int nodeId);

        Observable<ImageResult> uploadPhoto(MultipartBody.Part file);

        Observable<List<Node>> getNodes(boolean update);
    }
}
