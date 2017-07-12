package com.sneider.diycode.mvp.model.api.service;

import com.sneider.diycode.mvp.model.bean.ImageResult;
import com.sneider.diycode.mvp.model.bean.Like;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.Sites;
import com.sneider.diycode.mvp.model.bean.Token;
import com.sneider.diycode.utils.Constant;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface CommonService {

    /**
     * 获取token
     */
    @FormUrlEncoded
    @POST(Constant.OAUTH_URL)
    Observable<Token> getToken(@Field("client_id") String client_id,
                               @Field("client_secret") String client_secret,
                               @Field("grant_type") String grant_type,
                               @Field("username") String username,
                               @Field("password") String password);

    /**
     * 更新设备
     */
    @FormUrlEncoded
    @POST("devices.json")
    Observable<Ok> updatedevice(@Field("platform") String platform,
                                @Field("token") String token);

    /**
     * 删除设备
     */
    @DELETE("devices.json")
    Observable<Ok> deletedevice(@Query("platform") String platform,
                                @Query("token") String token);

    /**
     * 点赞
     */
    @FormUrlEncoded
    @POST("likes.json")
    Observable<Like> like(@Field("obj_type") String obj_type,
                          @Field("obj_id") Integer obj_id);

    /**
     * 取消点赞
     */
    @DELETE("likes.json")
    Observable<Like> unlike(@Query("obj_type") String obj_type,
                            @Query("obj_id") Integer obj_id);

    /**
     * 获取Site列表
     */
    @GET("sites.json")
    Observable<List<Sites>> getSites();

    /**
     * 上传图片
     */
    @Multipart
    @POST("photos.json")
    Observable<ImageResult> uploadPhoto(@Part MultipartBody.Part file);
}
