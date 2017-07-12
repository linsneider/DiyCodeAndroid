package com.sneider.diycode.mvp.model.api.service;

import com.sneider.diycode.mvp.model.bean.Count;
import com.sneider.diycode.mvp.model.bean.Notification;
import com.sneider.diycode.mvp.model.bean.Ok;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationService {

    /**
     * 获取通知列表
     */
    @GET("notifications.json")
    Observable<List<Notification>> getNotifications(@Query("offset") Integer offset,
                                                    @Query("limit") Integer limit);

    /**
     * 获取未读通知数量
     */
    @GET("notifications/unread_count.json")
    Observable<Count> getUnreadCount();

    /**
     * 标记通知已读
     */
    @FormUrlEncoded
    @POST("notifications/read.json")
    Observable<Ok> readNotification(@Field("ids") int[] ids);

    /**
     * 删除通知
     */
    @DELETE("notifications/{id}.json")
    Observable<Ok> deleteNotification(@Path("id") Integer id);

    /**
     * 删除全部通知
     */
    @DELETE("notifications/all.json")
    Observable<Ok> deleteAllNotifications();
}
