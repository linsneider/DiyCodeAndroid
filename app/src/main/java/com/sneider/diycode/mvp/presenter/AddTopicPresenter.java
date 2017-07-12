package com.sneider.diycode.mvp.presenter;

import android.app.Application;
import android.os.SystemClock;

import com.blankj.utilcode.util.ToastUtils;
import com.jess.arms.base.App;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.PermissionUtil;
import com.sneider.diycode.event.UpdateTopicEvent;
import com.sneider.diycode.mvp.contract.AddTopicContract;
import com.sneider.diycode.mvp.model.bean.ImageResult;
import com.sneider.diycode.mvp.model.bean.Node;
import com.sneider.diycode.mvp.model.bean.Section;
import com.sneider.diycode.mvp.model.bean.Topic;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.PrefUtils;
import com.sneider.diycode.utils.RxUtils;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@ActivityScope
public class AddTopicPresenter extends BasePresenter<AddTopicContract.Model, AddTopicContract.View> {

    private Application mApplication;
    private AppManager mAppManager;
    private RxErrorHandler mErrorHandler;
    private ArrayList<Long> uploadingPhotos = new ArrayList<>();

    @Inject
    public AddTopicPresenter(AddTopicContract.Model model, AddTopicContract.View rootView, RxErrorHandler handler, AppManager appManager, Application application) {
        super(model, rootView);
        this.mApplication = application;
        this.mAppManager = appManager;
        this.mErrorHandler = handler;
    }

    public void createTopic(String title, String body, int nodeId) {
        mModel.createTopic(title, body, nodeId)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> mRootView.hideLoading())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Topic>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort("发布失败");
                    }

                    @Override
                    public void onNext(@NonNull Topic topic) {
                        ToastUtils.showShort("发布成功");
                        EventBus.getDefault().post(new UpdateTopicEvent());
                        mRootView.killMyself();
                    }
                });
    }

    public void updateTopic(int id, String title, String body, int nodeId) {
        mModel.updateTopic(id, title, body, nodeId)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> mRootView.hideLoading())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Topic>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort("话题更新失败");
                    }

                    @Override
                    public void onNext(@NonNull Topic topic) {
                        ToastUtils.showShort("话题更新成功");
                        EventBus.getDefault().post(new UpdateTopicEvent());
                        mRootView.killMyself();
                    }
                });
    }

    public void getNodes(boolean isRefresh) {
        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
            }

            @Override
            public void onRequestPermissionFailure() {
            }
        }, mRootView.getRxPermissions(), mErrorHandler);
        boolean isEvictCache = isRefresh;
        mModel.getNodes(isEvictCache)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<List<Node>>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull List<Node> data) {
                        List<Section> sections = DiycodeUtils.processNode(data);
                        mRootView.onGetNodes(sections);
                        // 缓存
                        AppComponent appComponent = ((App) mApplication).getAppComponent();
                        PrefUtils.getInstance(mApplication).put("topic_nodes", appComponent.gson().toJson(sections));
                    }
                });
    }

    public void uploadPhoto(String path) {
        long time = SystemClock.currentThreadTimeMillis();
        uploadingPhotos.add(time);
//        File file = new File(DiycodeUtils.cacheImageFromContentResolver(mApplication, uri));
        File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part data = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        mModel.uploadPhoto(data)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> mRootView.showUploading())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    uploadingPhotos.remove(time);
                    if (uploadingPhotos.isEmpty() && mRootView != null) {
                        mRootView.hideUploading();
                    }
                })
//                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<ImageResult>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort("图片上传失败");
                    }

                    @Override
                    public void onNext(@NonNull ImageResult url) {
                        ToastUtils.showShort("图片上传成功");
                        if (mRootView != null) mRootView.onUploadPhoto(url.getImage_url());
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mErrorHandler = null;
        mAppManager = null;
        mApplication = null;
    }
}
