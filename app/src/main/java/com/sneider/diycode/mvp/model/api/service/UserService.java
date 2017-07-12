package com.sneider.diycode.mvp.model.api.service;

import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.Reply;
import com.sneider.diycode.mvp.model.bean.Topic;
import com.sneider.diycode.mvp.model.bean.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    /**
     * 获取用户信息
     */
    @GET("users/{login}.json")
    Observable<User> getUserInfo(@Path("login") String login);

    /**
     * 获取用户收藏的Topic列表
     */
    @GET("users/{login}/favorites.json")
    Observable<List<Topic>> getUserFavorites(@Path("login") String login,
                                             @Query("offset") Integer offset,
                                             @Query("limit") Integer limit);

    /**
     * 获取用户的回复列表
     */
    @GET("users/{login}/replies.json")
    Observable<List<Reply>> getUserReplies(@Path("login") String login,
                                           @Query("order") String order,
                                           @Query("offset") Integer offset,
                                           @Query("limit") Integer limit);

    /**
     * 获取用户创建的Topic列表
     */
    @GET("users/{login}/topics.json")
    Observable<List<Topic>> getUserTopics(@Path("login") String login,
                                          @Query("order") String order,
                                          @Query("offset") Integer offset,
                                          @Query("limit") Integer limit);

    /**
     * 获取用户的关注者列表
     */
    @GET("users/{login}/followers.json")
    Observable<List<User>> getFollowers(@Path("login") String login,
                                        @Query("offset") Integer offset,
                                        @Query("limit") Integer limit);

    /**
     * 获取用户关注的人列表
     */
    @GET("users/{login}/following.json")
    Observable<List<User>> getFollowings(@Path("login") String login,
                                         @Query("offset") Integer offset,
                                         @Query("limit") Integer limit);

    /**
     * 关注用户
     */
    @POST("users/{login}/follow.json")
    Observable<Ok> followUser(@Path("login") String login);

    /**
     * 取消关注用户
     */
    @POST("users/{login}/unfollow.json")
    Observable<Ok> unfollowUser(@Path("login") String login);

    /**
     * 获取用户屏蔽的人列表（只能获取自己的）
     */
    @GET("users/{login}/blocked.json")
    Observable<List<User>> getBlockedUsers(@Path("login") String login,
                                           @Query("offset") Integer offset,
                                           @Query("limit") Integer limit);

    /**
     * 屏蔽用户
     */
    @POST("users/{login}/block.json")
    Observable<Ok> blockUser(@Path("login") String login);

    /**
     * 取消屏蔽用户
     */
    @POST("users/{login}/unblock.json")
    Observable<Ok> unblockUser(@Path("login") String login);
}
