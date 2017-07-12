package com.sneider.diycode.mvp.model.api.service;

import com.sneider.diycode.mvp.model.bean.Node;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.Reply;
import com.sneider.diycode.mvp.model.bean.Topic;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TopicService {

    /**
     * 获取Topic列表
     */
    @GET("topics.json")
    Observable<List<Topic>> getTopics(@Query("type") String type,
                                      @Query("node_id") Integer node_id,
                                      @Query("offset") Integer offset,
                                      @Query("limit") Integer limit);

    /**
     * 获取Topic详情
     */
    @GET("topics/{id}.json")
    Observable<Topic> getTopicDetail(@Path("id") Integer id);

    /**
     * 创建Topic
     */
    @FormUrlEncoded
    @POST("topics.json")
    Observable<Topic> createTopic(@Field("title") String title,
                                  @Field("body") String body,
                                  @Field("node_id") Integer node_id);

    /**
     * 更新Topic
     */
    @FormUrlEncoded
    @POST("topics/{id}.json")
    Observable<Topic> updateTopic(@Path("id") Integer id,
                                  @Field("title") String title,
                                  @Field("body") String body,
                                  @Field("node_id") Integer node_id);

    /**
     * 收藏Topic
     */
    @POST("topics/{id}/favorite.json")
    Observable<Ok> favoriteTopic(@Path("id") Integer id);

    /**
     * 取消收藏Topic
     */
    @POST("topics/{id}/unfavorite.json")
    Observable<Ok> unfavoriteTopic(@Path("id") Integer id);

    /**
     * 获取Topic的节点列表
     */
    @GET("nodes.json")
    Observable<List<Node>> getNodes();

    /**
     * 获取Topic的回复列表
     */
    @GET("topics/{id}/replies.json")
    Observable<List<Reply>> getTopicReplies(@Path("id") Integer id,
                                            @Query("offset") Integer offset,
                                            @Query("limit") Integer limit);

    /**
     * 创建Topic回复
     */
    @FormUrlEncoded
    @POST("topics/{id}/replies.json")
    Observable<Reply> createTopicReply(@Path("id") Integer id,
                                       @Field("body") String body);

    /**
     * 获取Topic回复
     */
    @GET("replies/{id}.json")
    Observable<Reply> getTopicReply(@Path("id") Integer id);

    /**
     * 更新Topic回复
     */
    @FormUrlEncoded
    @POST("replies/{id}.json")
    Observable<Reply> updateTopicReply(@Path("id") Integer id,
                                       @Field("body") String body);

    /**
     * 删除Topic回复
     */
    @DELETE("replies/{id}.json")
    Observable<Ok> deleteTopicReply(@Path("id") Integer id);
}
