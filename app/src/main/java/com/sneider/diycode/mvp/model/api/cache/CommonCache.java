package com.sneider.diycode.mvp.model.api.cache;

import com.sneider.diycode.mvp.model.bean.News;
import com.sneider.diycode.mvp.model.bean.NewsNode;
import com.sneider.diycode.mvp.model.bean.Node;
import com.sneider.diycode.mvp.model.bean.Notification;
import com.sneider.diycode.mvp.model.bean.Project;
import com.sneider.diycode.mvp.model.bean.Sites;
import com.sneider.diycode.mvp.model.bean.Topic;
import com.sneider.diycode.mvp.model.bean.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.LifeCache;
import io.rx_cache2.Reply;

public interface CommonCache {

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<Topic>>> getTopics(Observable<List<Topic>> topics, DynamicKeyGroup nodeIdAndOffset, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<Topic>>> getUserTopics(Observable<List<Topic>> topics, DynamicKey offset, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<Topic>>> getUserFavorites(Observable<List<Topic>> topics, DynamicKey offset, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<Topic>> getTopicDetail(Observable<Topic> topic, DynamicKey id, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<User>> getUserInfo(Observable<User> user, DynamicKey id, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<News>>> getNews(Observable<List<News>> news, DynamicKeyGroup nodeIdAndOffset, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<Project>>> getProjects(Observable<List<Project>> projects, DynamicKey offset, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<Notification>>> getNotifications(Observable<List<Notification>> notifications, DynamicKey offset, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<Sites>>> getSites(Observable<List<Sites>> sites, DynamicKey offset, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<User>>> getFollowers(Observable<List<User>> users, DynamicKeyGroup usernameAndOffset, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<User>>> getFollowings(Observable<List<User>> users, DynamicKeyGroup usernameAndOffset, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<User>>> getBlockedUsers(Observable<List<User>> users, DynamicKeyGroup usernameAndOffset, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<Node>>> getNodes(Observable<List<Node>> nodes, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<NewsNode>>> getNewsNodes(Observable<List<NewsNode>> nodes, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<com.sneider.diycode.mvp.model.bean.Reply>>> getTopicReplies(Observable<List<com.sneider.diycode.mvp.model.bean.Reply>> replies, DynamicKeyGroup idAndOffset, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<com.sneider.diycode.mvp.model.bean.Reply>>> getNewsReplies(Observable<List<com.sneider.diycode.mvp.model.bean.Reply>> replies, DynamicKeyGroup idAndOffset, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<com.sneider.diycode.mvp.model.bean.Reply>>> getProjectReplies(Observable<List<com.sneider.diycode.mvp.model.bean.Reply>> replies, DynamicKeyGroup idAndOffset, EvictProvider evictProvider);

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<com.sneider.diycode.mvp.model.bean.Reply>>> getUserReplies(Observable<List<com.sneider.diycode.mvp.model.bean.Reply>> replies, DynamicKeyGroup usernameAndOffset, EvictProvider evictProvider);
}
