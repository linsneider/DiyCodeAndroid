package com.sneider.diycode.mvp.presenter;

import android.app.Application;
import android.os.SystemClock;

import com.blankj.utilcode.util.ToastUtils;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.sneider.diycode.R;
import com.sneider.diycode.event.ReplyEvent;
import com.sneider.diycode.mvp.contract.AddReplyContract;
import com.sneider.diycode.mvp.model.bean.ImageResult;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.Reply;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.RxUtils;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

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
public class AddReplyPresenter extends BasePresenter<AddReplyContract.Model, AddReplyContract.View> {

    private Application mApplication;
    private AppManager mAppManager;
    private RxErrorHandler mErrorHandler;
    private ArrayList<Long> uploadingPhotos = new ArrayList<>();

    @Inject
    public AddReplyPresenter(AddReplyContract.Model model, AddReplyContract.View rootView, RxErrorHandler handler, AppManager appManager, Application application) {
        super(model, rootView);
        this.mApplication = application;
        this.mAppManager = appManager;
        this.mErrorHandler = handler;
    }

    public void createTopicReply(int topicId, String body) {
        mModel.createTopicReply(topicId, body)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> mRootView.hideLoading())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Reply>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort(R.string.reply_failed);
                    }

                    @Override
                    public void onNext(@NonNull Reply reply) {
                        ToastUtils.showShort(R.string.reply_success);
                        EventBus.getDefault().post(new ReplyEvent());
                        mRootView.killMyself();
                    }
                });
    }

    public void getTopicReply(int id) {
        mModel.getTopicReply(id)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> mRootView.hideLoading())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Reply>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort("获取评论内容失败");
                    }

                    @Override
                    public void onNext(@NonNull Reply reply) {
                        mRootView.onGetReply(reply.getBody());
                    }
                });
    }

    public void updateTopicReply(int id, String body) {
        mModel.updateTopicReply(id, body)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> mRootView.hideLoading())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Reply>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort(R.string.reply_failed);
                    }

                    @Override
                    public void onNext(@NonNull Reply reply) {
                        ToastUtils.showShort(R.string.reply_success);
                        EventBus.getDefault().post(new ReplyEvent());
                        mRootView.killMyself();
                    }
                });
    }

    public void deleteTopicReply(int id) {
        mModel.deleteTopicReply(id)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> mRootView.hideLoading())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Ok>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort(R.string.delete_failed);
                    }

                    @Override
                    public void onNext(@NonNull Ok ok) {
                        ToastUtils.showShort(R.string.delete_success);
                        EventBus.getDefault().post(new ReplyEvent());
                        mRootView.killMyself();
                    }
                });
    }

    public void getProjectReply(int id) {
        mModel.getProjectReply(id)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> mRootView.hideLoading())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Reply>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort("获取评论内容失败");
                    }

                    @Override
                    public void onNext(@NonNull Reply reply) {
                        mRootView.onGetReply(DiycodeUtils.removeP(reply.getBody_html()));
                    }
                });
    }

    public void updateProjectReply(int id, String body) {
        mModel.updateProjectReply(id, body)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> mRootView.hideLoading())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Reply>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort(R.string.reply_failed);
                    }

                    @Override
                    public void onNext(@NonNull Reply reply) {
                        ToastUtils.showShort(R.string.reply_success);
                        EventBus.getDefault().post(new ReplyEvent());
                        mRootView.killMyself();
                    }
                });
    }

    public void deleteProjectReply(int id) {
        mModel.deleteProjectReply(id)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> mRootView.hideLoading())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Ok>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort(R.string.delete_failed);
                    }

                    @Override
                    public void onNext(@NonNull Ok ok) {
                        ToastUtils.showShort(R.string.delete_success);
                        EventBus.getDefault().post(new ReplyEvent());
                        mRootView.killMyself();
                    }
                });
    }

    public void uploadPhoto(String path) {
        long time = SystemClock.currentThreadTimeMillis();
        uploadingPhotos.add(time);
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
