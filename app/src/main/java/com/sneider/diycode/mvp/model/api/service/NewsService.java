package com.sneider.diycode.mvp.model.api.service;

import com.sneider.diycode.mvp.model.bean.News;
import com.sneider.diycode.mvp.model.bean.NewsNode;
import com.sneider.diycode.mvp.model.bean.Reply;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsService {

    /**
     * 获取News列表
     */
    @GET("news.json")
    Observable<List<News>> getNews(@Query("node_id") Integer node_id,
                                   @Query("offset") Integer offset,
                                   @Query("limit") Integer limit);

    /**
     * 获取News的回复列表
     */
    @GET("news/{id}/replies.json")
    Observable<List<Reply>> getNewsReplies(@Path("id") Integer id,
                                           @Query("offset") Integer offset,
                                           @Query("limit") Integer limit);

    /**
     * 获取News的节点列表
     */
    @GET("news/nodes.json")
    Observable<List<NewsNode>> getNewsNodes();

    /**
     * 创建News
     */
    @FormUrlEncoded
    @POST("/news.json")
    Observable<News> createNews(@Field("title") String title,
                                @Field("address") String address,
                                @Field("node_id") Integer node_id);
}
