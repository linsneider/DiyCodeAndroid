package com.sneider.diycode.mvp.model;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.sneider.diycode.mvp.contract.NotificationListContract;
import com.sneider.diycode.mvp.model.api.service.NotificationService;
import com.sneider.diycode.mvp.model.bean.Count;
import com.sneider.diycode.mvp.model.bean.Notification;
import com.sneider.diycode.mvp.model.bean.Ok;
import com.sneider.diycode.utils.Constant;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

@ActivityScope
public class NotificationListModel extends BaseModel implements NotificationListContract.Model {

    @Inject
    public NotificationListModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<List<Notification>> getNotifications(int offset, boolean update) {
        Observable<List<Notification>> notifications = mRepositoryManager.obtainRetrofitService(NotificationService.class)
                .getNotifications(offset, Constant.PAGE_SIZE);
        return notifications;
//        return mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getNotifications(notifications, new DynamicKey(offset), new EvictDynamicKey(update))
//                .flatMap(new Function<Reply<List<Notification>>, ObservableSource<List<Notification>>>() {
//                    @Override
//                    public ObservableSource<List<Notification>> apply(@NonNull Reply<List<Notification>> reply) throws Exception {
//                        return Observable.just(reply.getData());
//                    }
//                });
    }

    @Override
    public Observable<Ok> deleteNotification(int id) {
        Observable<Ok> ok = mRepositoryManager.obtainRetrofitService(NotificationService.class)
                .deleteNotification(id);
        return ok;
    }

    @Override
    public Observable<Ok> deleteAllNotifications() {
        Observable<Ok> ok = mRepositoryManager.obtainRetrofitService(NotificationService.class)
                .deleteAllNotifications();
        return ok;
    }

    @Override
    public Observable<Count> getUnreadCount() {
        Observable<Count> count = mRepositoryManager.obtainRetrofitService(NotificationService.class)
                .getUnreadCount();
        return count;
    }

    @Override
    public Observable<Ok> readNotification(int[] ids) {
        Observable<Ok> ok = mRepositoryManager.obtainRetrofitService(NotificationService.class)
                .readNotification(ids);
        return ok;
    }
}
