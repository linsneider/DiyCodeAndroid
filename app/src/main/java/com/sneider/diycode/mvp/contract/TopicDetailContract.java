package com.sneider.diycode.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.sneider.diycode.mvp.model.bean.Like;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.Topic;

import io.reactivex.Observable;

public interface TopicDetailContract {

    interface View extends IView {

        void onGetTopicDetail(Topic topic);

        void onFavoriteTopic();

        void onLikeTopic(Like like);

        void setLayout(boolean isNormal);
    }

    interface Model extends IModel {

        Observable<Topic> getTopicDetail(int id, boolean update);

        Observable<Like> likeTopic(int id);

        Observable<Like> unlikeTopic(int id);

        Observable<Ok> favoriteTopic(int id);

        Observable<Ok> unfavoriteTopic(int id);
    }
}
