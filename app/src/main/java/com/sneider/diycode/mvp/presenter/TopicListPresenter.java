package com.sneider.diycode.mvp.presenter;

import android.app.Application;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ToastUtils;
import com.jess.arms.base.App;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.PermissionUtil;
import com.sneider.diycode.app.ARouterPaths;
import com.sneider.diycode.mvp.contract.TopicListContract;
import com.sneider.diycode.mvp.model.bean.Node;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.mvp.model.bean.Section;
import com.sneider.diycode.mvp.model.bean.Topic;
import com.sneider.diycode.mvp.model.bean.User;
import com.sneider.diycode.mvp.ui.adapter.TopicListAdapter;
import com.sneider.diycode.utils.Constant;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.PrefUtils;
import com.sneider.diycode.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

import static com.sneider.diycode.app.ARouterPaths.TOPIC_DETAIL;
import static com.sneider.diycode.app.ARouterPaths.USER_DETAIL;
import static com.sneider.diycode.mvp.ui.activity.TopicDetailActivity.EXTRA_TOPIC;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.EXTRA_TOPIC_NODE_ID;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.EXTRA_TOPIC_NODE_NAME;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.EXTRA_TOPIC_TYPE;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.TOPIC_BY_NODE_ID;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.TOPIC_CREATE;
import static com.sneider.diycode.mvp.ui.activity.TopicListActivity.TOPIC_FAVORITES;
import static com.sneider.diycode.mvp.ui.activity.UserDetailActivity.EXTRA_USERNAME;

@ActivityScope
public class TopicListPresenter extends BasePresenter<TopicListContract.Model, TopicListContract.View> {

    private Application mApplication;
    private AppManager mAppManager;
    private RxErrorHandler mErrorHandler;
    private List<Topic> mList = new ArrayList<>();
    private TopicListAdapter mAdapter;
    private int offset = 0;
    private boolean isFirst = true;

    @Inject
    public TopicListPresenter(TopicListContract.Model model, TopicListContract.View rootView, RxErrorHandler handler, AppManager appManager, Application application) {
        super(model, rootView);
        mApplication = application;
        mAppManager = appManager;
        mErrorHandler = handler;
    }

    public void initAdapter(int topicType, String username) {
        if (mAdapter == null) {
            mAdapter = new TopicListAdapter(mList);
            mRootView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new TopicListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, Topic topic) {
                    ARouter.getInstance().build(TOPIC_DETAIL)
                            .withSerializable(EXTRA_TOPIC, topic)
                            .navigation();
                }

                @Override
                public void onNameClick(View view, String username) {
                    ARouter.getInstance().build(USER_DETAIL)
                            .withString(EXTRA_USERNAME, username)
                            .navigation();
                }

                @Override
                public void onNodeNameClick(View view, String nodeName, int nodeId) {
                    if (topicType != TOPIC_BY_NODE_ID) {
                        ARouter.getInstance().build(ARouterPaths.TOPIC_LIST)
                                .withInt(EXTRA_TOPIC_TYPE, TOPIC_BY_NODE_ID)
                                .withString(EXTRA_TOPIC_NODE_NAME, nodeName)
                                .withInt(EXTRA_TOPIC_NODE_ID, nodeId)
                                .navigation();
                    }
                }
            });
            mAdapter.setOnItemLongClickListener((view, topic) -> {
                switch (topicType) {
                    case TOPIC_CREATE:
                    case TOPIC_BY_NODE_ID:
                        new MaterialDialog.Builder(mAppManager.getCurrentActivity()).items("收藏")
                                .itemsCallback((dialog, itemView, position, text) -> {
                                    if (DiycodeUtils.checkToken(mApplication)) {
                                        favoriteTopic(topic.getId());
                                    }
                                }).show();
                        break;
                    case TOPIC_FAVORITES:
                        User user = DiycodeUtils.getUser(mApplication);
                        if (user != null && username.equals(user.getLogin())) {
                            new MaterialDialog.Builder(mAppManager.getCurrentActivity()).items("取消收藏")
                                    .itemsCallback((dialog, itemView, position, text) -> {
                                        if (DiycodeUtils.checkToken(mApplication)) {
                                            unfavoriteTopic(topic);
                                        }
                                    }).show();
                        }
                        break;
                    default:
                        break;
                }
            });
        }
    }

    public void getTopics(int topicType, String username, int nodeId, boolean isRefresh) {
        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
            }

            @Override
            public void onRequestPermissionFailure() {
            }
        }, mRootView.getRxPermissions(), mErrorHandler);
        if (isRefresh) offset = 0;
        boolean isEvictCache = true;
//        if (isRefresh && isFirst) {
//            isFirst = false;
//            isEvictCache = false;
//        }
        Observable<List<Topic>> observable;
        switch (topicType) {
            case TOPIC_CREATE:
                observable = mModel.getUserTopics(username, offset, isEvictCache);
                break;
            case TOPIC_FAVORITES:
                observable = mModel.getUserFavorites(username, offset, isEvictCache);
                break;
            default:
                observable = mModel.getTopics(nodeId, offset, isEvictCache);
                break;
        }
        observable.subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> {
                    if (isRefresh) mRootView.showLoading();
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    if (isRefresh) mRootView.hideLoading();
                })
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<List<Topic>>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        mRootView.onLoadMoreError();
                    }

                    @Override
                    public void onNext(@NonNull List<Topic> data) {
                        mRootView.onLoadMoreComplete();
                        if (offset == 0) mList.clear();
                        mList.addAll(data);
                        mAdapter.notifyDataSetChanged();
                        offset = mAdapter.getItemCount();
                        if (data.size() < Constant.PAGE_SIZE) mRootView.onLoadMoreEnd();
                        mRootView.setEmpty(mList.isEmpty());
                    }
                });
    }

    public void favoriteTopic(int id) {
        mModel.favoriteTopic(id)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Ok>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort("收藏失败");
                    }

                    @Override
                    public void onNext(@NonNull Ok ok) {
                        mRootView.onFavoriteSuccess();
                    }
                });
    }

    private void unfavoriteTopic(Topic topic) {
        mModel.unfavoriteTopic(topic.getId())
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<Ok>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        ToastUtils.showShort("取消收藏失败");
                    }

                    @Override
                    public void onNext(@NonNull Ok ok) {
                        mList.remove(topic);
                        mAdapter.notifyDataSetChanged();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter = null;
        mList = null;
        mErrorHandler = null;
        mAppManager = null;
        mApplication = null;
    }
}
