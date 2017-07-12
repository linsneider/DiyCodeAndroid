package com.sneider.diycode.mvp.model.api.service;

import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.Project;
import com.sneider.diycode.mvp.model.bean.Reply;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProjectService {

    /**
     * 获取Project列表
     */
    @GET("projects.json")
    Observable<List<Project>> getProjects(@Query("node_id") Integer node_id,
                                          @Query("offset") Integer offset,
                                          @Query("limit") Integer limit);

    /**
     * 获取Project的回复列表
     */
    @GET("projects/{id}/replies.json")
    Observable<List<Reply>> getProjectReplies(@Path("id") Integer id,
                                              @Query("offset") Integer offset,
                                              @Query("limit") Integer limit);

    /**
     * 获取Project的回复详情
     */
    @GET("project_replies/{id}.json")
    Observable<Reply> getProjectReply(@Path("id") Integer id);

    /**
     * 更新Project的回复
     */
    @FormUrlEncoded
    @POST("project_replies/{id}.json")
    Observable<Reply> updateProjectReply(@Path("id") Integer id,
                                         @Field("body") String body);

    /**
     * 删除Project的回复
     */
    @DELETE("project_replies/{id}.json")
    Observable<Ok> deleteProjectReply(@Path("id") Integer id);
}
