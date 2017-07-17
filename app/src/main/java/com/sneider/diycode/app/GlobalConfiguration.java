package com.sneider.diycode.app;

import android.app.Application;
import android.content.Context;
import android.net.ParseException;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.aitangba.swipeback.ActivityLifecycleHelper;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.jess.arms.base.App;
import com.jess.arms.base.delegate.AppDelegate;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.module.GlobalConfigModule;
import com.jess.arms.http.GlobalHttpHandler;
import com.jess.arms.integration.ConfigModule;
import com.jess.arms.integration.IRepositoryManager;
import com.sneider.diycode.BuildConfig;
import com.sneider.diycode.R;
import com.sneider.diycode.mvp.model.api.cache.CommonCache;
import com.sneider.diycode.mvp.model.api.service.CommonService;
import com.sneider.diycode.mvp.model.api.service.NewsService;
import com.sneider.diycode.mvp.model.api.service.NotificationService;
import com.sneider.diycode.mvp.model.api.service.ProjectService;
import com.sneider.diycode.mvp.model.api.service.TopicService;
import com.sneider.diycode.mvp.model.api.service.UserService;
import com.sneider.diycode.mvp.model.bean.Token;
import com.sneider.diycode.mvp.model.bean.User;
import com.sneider.diycode.utils.Constant;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.PrefUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.smtt.sdk.QbSdk;

import org.json.JSONException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.jessyan.progressmanager.ProgressManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.HttpException;
import timber.log.Timber;

public class GlobalConfiguration implements ConfigModule {
    @Override
    public void applyOptions(Context context, GlobalConfigModule.Builder builder) {
        builder.baseurl(Constant.BASE_URL)
                .globalHttpHandler(new GlobalHttpHandler() {// 这里可以提供一个全局处理Http请求和响应结果的处理类
                    // 这里可以比客户端提前一步拿到服务器返回的结果,可以做一些操作,比如token超时,重新获取
                    @Override
                    public Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response) {
                        /* 这里可以先客户端一步拿到每一次http请求的结果,可以解析成json,做一些操作,如检测到token过期后
                           重新请求token,并重新执行请求 */
                        /*try {
                            if (!TextUtils.isEmpty(httpResult) && RequestInterceptor.isJson(response.body().contentType())) {
                                JSONArray array = new JSONArray(httpResult);
                                JSONObject object = (JSONObject) array.get(0);
                                String login = object.getString("login");
                                String avatar_url = object.getString("avatar_url");
                                Timber.w("Result ------> " + login + "    ||   Avatar_url------> " + avatar_url);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return response;
                        }*/
                     /* 这里如果发现token过期,可以先请求最新的token,然后在拿新的token放入request里去重新请求
                        注意在这个回调之前已经调用过proceed,所以这里必须自己去建立网络请求,如使用okhttp使用新的request去请求
                        create a new request and modify it accordingly using the new token
                        Request newRequest = chain.request().newBuilder().header("token", newToken)
                                             .build();

                        retry the request

                        response.body().close();
                        如果使用okhttp将新的请求,请求成功后,将返回的response  return出去即可
                        如果不需要返回新的结果,则直接把response参数返回出去 */
                        return response;
                    }

                    // 这里可以在请求服务器之前可以拿到request,做一些操作比如给request统一添加token或者header以及参数加密等操作
                    @Override
                    public Request onHttpRequestBefore(Interceptor.Chain chain, Request request) {
                        // 如果需要再请求服务器之前做一些操作,则重新返回一个做过操作的的request如增加header,
                        // 不做操作则直接返回request参数
                        Token token = DiycodeUtils.getToken(context);
                        if (token != null) {
//                            Log.e("token", "Bearer " + token.getAccess_token());
                            return chain.request().newBuilder().header("Authorization", "Bearer " + token.getAccess_token()).build();
                        } else {
                            return request;
                        }
                    }
                })
                .responseErrorListener((context1, t) -> {
                    /* 用来提供处理所有错误的监听
                       rxjava必要要使用ErrorHandleSubscriber(默认实现Subscriber的onError方法),此监听才生效 */
                    Timber.tag("Catch-Error").w(t.getMessage());
                    //这里不光是只能打印错误,还可以根据不同的错误作出不同的逻辑处理
                    String msg = "未知错误";
                    if (t instanceof UnknownHostException) {
                        msg = "网络不可用";
                    } else if (t instanceof SocketTimeoutException) {
                        msg = "请求网络超时";
                    } else if (t instanceof HttpException) {
                        HttpException httpException = (HttpException) t;
                        msg = convertStatusCode(httpException);
                    } else if (t instanceof JsonParseException || t instanceof ParseException || t instanceof JSONException || t instanceof JsonIOException) {
                        msg = "数据解析错误";
                    }
//                    UiUtils.snackbarText(msg);
                })
                .gsonConfiguration((context1, gsonBuilder) -> {//这里可以自己自定义配置Gson的参数
                    gsonBuilder
                            .serializeNulls()//支持序列化null的参数
                            .enableComplexMapKeySerialization();//支持将序列化key为object的map,默认只能序列化key为string的map
                })
                .retrofitConfiguration((context1, retrofitBuilder) -> {//这里可以自己自定义配置Retrofit的参数,甚至你可以替换系统配置好的okhttp对象
//                    retrofitBuilder.addConverterFactory(FastJsonConverterFactory.create());//比如使用fastjson替代gson
                })
                .okhttpConfiguration((context1, okhttpBuilder) -> {//这里可以自己自定义配置Okhttp的参数
                    okhttpBuilder.connectTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(60, TimeUnit.SECONDS);
                    //开启使用一行代码监听 Retrofit／Okhttp 上传下载进度监听,以及 Glide 加载进度监听 详细使用方法查看 https://github.com/JessYanCoding/ProgressManager
                    ProgressManager.getInstance().with(okhttpBuilder);
                })
                .rxCacheConfiguration((context1, rxCacheBuilder) -> {//这里可以自己自定义配置RxCache的参数
                    rxCacheBuilder.useExpiredDataIfLoaderNotAvailable(true);
                });
    }

    @Override
    public void registerComponents(Context context, IRepositoryManager repositoryManager) {
        // 使用repositoryManager可以注入一些服务
        repositoryManager.injectRetrofitService(
                CommonService.class, UserService.class, NewsService.class,
                NotificationService.class, ProjectService.class, TopicService.class);// Retrofit需要的Service
        repositoryManager.injectCacheService(CommonCache.class);// RxCache需要的Service
    }

    @Override
    public void injectAppLifecycle(Context context, List<AppDelegate.Lifecycle> lifecycles) {
        // 向Application的生命周期中注入一些自定义逻辑
        // AppDelegate.Lifecycle 的所有方法都会在基类Application对应的生命周期中被调用,所以在对应的方法中可以扩展一些自己需要的逻辑
        lifecycles.add(new AppDelegate.Lifecycle() {

            @Override
            public void onCreate(Application application) {
                if (BuildConfig.LOG_DEBUG) {// Timber日志打印
                    Timber.plant(new Timber.DebugTree());
                }
                AppComponent appComponent = ((App) application).getAppComponent();
                // LeakCanary内存泄露检查
                if (LeakCanary.isInAnalyzerProcess(application)) {
                    // This process is dedicated to LeakCanary for heap analysis.
                    // You should not init your app in this process.
                    return;
                }
                appComponent.extras().put(RefWatcher.class.getName(),
                        BuildConfig.USE_CANARY ? LeakCanary.install(application) : RefWatcher.DISABLED);
                // Glide
                ViewTarget.setTagId(R.id.glide_tag);
                // 右滑关闭Activity
                application.registerActivityLifecycleCallbacks(ActivityLifecycleHelper.build());
                // 初始化工具类
                Utils.init(application);
                // X5内核初始化接口
                QbSdk.initX5Environment(application, null);
                // ARouter
                if (AppUtils.isAppDebug()) {
                    ARouter.openLog();     // 打印日志
                    ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行,必须开启调试模式!线上版本需要关闭,否则有安全风险)
                }
                ARouter.init(application); // 尽可能早,推荐在Application中初始化

//                try {
//                    KeyStoreHelper.createKeys(application, Constant.KEYSTORE_KEY_ALIAS);
//                    Log.e("KeyStoreHelper", "create keys success");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                String tokenJson = PrefUtils.getInstance(application).getString("token", "");
                if (!TextUtils.isEmpty(tokenJson)) {
                    try {
//                        Log.e("tokenJson", "tokenJson==========" + tokenJson);
//                        tokenJson = KeyStoreHelper.decrypt(Constant.KEYSTORE_KEY_ALIAS, tokenJson);
//                        Log.e("tokenJson", "解密后的tokenJson==========" + tokenJson);
                        Token token = appComponent.gson().fromJson(tokenJson, Token.class);
                        if (token != null) {
                            DiycodeUtils.setToken(application, token);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                String userJson = PrefUtils.getInstance(application).getString("user", "");
                if (!TextUtils.isEmpty(userJson)) {
                    try {
//                        userJson = KeyStoreHelper.decrypt(Constant.KEYSTORE_KEY_ALIAS, userJson);
                        User user = appComponent.gson().fromJson(userJson, User.class);
                        if (user != null) {
                            DiycodeUtils.setUser(application, user);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onTerminate(Application application) {
            }
        });
    }

    @Override
    public void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles) {
        // 向Activity的生命周期中注入一些自定义逻辑
    }

    @Override
    public void injectFragmentLifecycle(Context context, List<FragmentManager.FragmentLifecycleCallbacks> lifecycles) {
        // 向Fragment的生命周期中注入一些自定义逻辑
    }

    private String convertStatusCode(HttpException httpException) {
        String msg;
        if (httpException.code() == 500) {
            msg = "服务器发生错误";
        } else if (httpException.code() == 404) {
            msg = "请求地址不存在";
        } else if (httpException.code() == 403) {
            msg = "请求被服务器拒绝";
        } else if (httpException.code() == 307) {
            msg = "请求被重定向到其他页面";
        } else {
            msg = httpException.message();
        }
        return msg;
    }
}
