package com.sneider.diycode.mvp.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jess.arms.base.App;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.widget.imageloader.ImageLoader;
import com.jess.arms.widget.imageloader.glide.GlideImageConfig;
import com.sneider.diycode.R;
import com.sneider.diycode.mvp.model.bean.Notification;
import com.sneider.diycode.mvp.model.bean.User;
import com.sneider.diycode.utils.DateUtils;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.GlideCircleTransform;
import com.sneider.diycode.utils.html.HtmlUtils;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;

import static com.sneider.diycode.app.ARouterPaths.PUBLIC_PHOTO;
import static com.sneider.diycode.app.ARouterPaths.TOPIC_DETAIL;
import static com.sneider.diycode.app.ARouterPaths.USER_DETAIL;
import static com.sneider.diycode.mvp.ui.activity.PhotoActivity.EXTRA_PHOTO_URL;
import static com.sneider.diycode.mvp.ui.activity.TopicDetailActivity.EXTRA_TOPIC_ID;
import static com.sneider.diycode.mvp.ui.activity.UserDetailActivity.EXTRA_USERNAME;

public class NotificationListAdapter extends DefaultAdapter<Notification> {

    public NotificationListAdapter(List<Notification> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<Notification> getHolder(View v, int viewType) {
        return new NotificationItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_notification;
    }

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    class NotificationItemHolder extends BaseHolder<Notification> implements View.OnLongClickListener, HtmlUtils.Callback {

        @BindView(R.id.layout) LinearLayout mLayout;
        @BindView(R.id.iv_avatar) ImageView mIvAvatar;
        @BindView(R.id.tv_name) TextView mTvName;
        @BindView(R.id.tv_time) TextView mTvTime;
        @BindView(R.id.tv_unread) TextView mTvUnread;
        @BindView(R.id.tv_title) TextView mTvTitle;
        @BindView(R.id.tv_content) TextView mTvContent;
        @BindView(R.id.tv_hint) TextView mTvHint;

        private AppComponent mAppComponent;
        private ImageLoader mImageLoader;

        NotificationItemHolder(View itemView) {
            super(itemView);
            mAppComponent = ((App) itemView.getContext().getApplicationContext()).getAppComponent();
            mImageLoader = mAppComponent.imageLoader();
        }

        @Override
        public void setData(Notification data, int position) {
            mTvUnread.setVisibility(data.isRead() ? View.GONE : View.VISIBLE);
            if (data.getActor() == null && data.getMention_type() == null && data.getMention() == null
                    && data.getTopic() == null && data.getReply() == null && data.getNode() == null) {
                mLayout.setVisibility(View.GONE);
                mTvHint.setVisibility(View.VISIBLE);
                mTvHint.setText(R.string.info_deleted);
            } else {
                mTvHint.setVisibility(View.GONE);
                mLayout.setVisibility(View.VISIBLE);
                if (data.getActor() != null) {
                    String avatarUrl = data.getActor().getAvatar_url();
                    if (avatarUrl.contains("diycode"))
                        avatarUrl = avatarUrl.replace("large_avatar", "avatar");
                    mImageLoader.loadImage(mAppComponent.application(), GlideImageConfig.builder()
                            .transformation(new GlideCircleTransform(mAppComponent.application()))
                            .url(avatarUrl).imageView(mIvAvatar).build());
                    mTvName.setText(data.getActor().getLogin());
                } else {
                    User user = DiycodeUtils.getUser(mAppComponent.application());
                    if (user != null) {
                        String avatarUrl = user.getAvatar_url();
                        if (avatarUrl.contains("diycode"))
                            avatarUrl = avatarUrl.replace("large_avatar", "avatar");
                        mImageLoader.loadImage(mAppComponent.application(), GlideImageConfig.builder()
                                .transformation(new GlideCircleTransform(mAppComponent.application()))
                                .url(avatarUrl).imageView(mIvAvatar).build());
                        mTvName.setText(user.getLogin());
                    }
                }
                String intervalTime = DateUtils.getIntervalTime(data.getCreated_at());
                mTvTime.setText(intervalTime);
                String content = null;
                switch (data.getType()) {
                    case "TopicReply":
                        mTvTitle.setText(MessageFormat.format(mAppComponent.application().getString(R.string.reply_at),
                                data.getReply().getTopic_title()));
                        content = data.getReply().getBody_html();
                        break;
                    case "Mention":
                        mTvTitle.setText(R.string.mention_you);
                        content = data.getMention().getBody_html();
                        break;
                    case "Topic":
                        mTvTitle.setText(R.string.create_topic);
                        content = data.getTopic().getTitle();
                        break;
                    case "NodeChanged":
                        mTvTitle.setText(MessageFormat.format(mAppComponent.application().getString(R.string.what_create_topic),
                                data.getTopic().getTitle()));
                        content = MessageFormat.format(mAppComponent.application().getString(R.string.what_node_moved),
                                data.getTopic().getNode_name());
                        break;
                    case "Hacknews":
                        mTvTitle.setText(R.string.your_share);
                        content = mAppComponent.application().getString(R.string.no_content);
                        break;
                    default:
                        break;
                }
                if (!TextUtils.isEmpty(content)) {
                    HtmlUtils.parseHtmlAndSetText(itemView.getContext(), content, mTvContent, this);
                }
            }

            itemView.setTag(data);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            if ("NodeChanged".equals(data.getType())) {
                mIvAvatar.setTag(DiycodeUtils.getUser(mAppComponent.application()));
                mTvName.setTag(DiycodeUtils.getUser(mAppComponent.application()));
            } else {
                mIvAvatar.setTag(data.getActor().getLogin());
                mTvName.setTag(data.getActor().getLogin());
            }
            mIvAvatar.setOnClickListener(this);
            mTvName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                switch (v.getId()) {
                    case R.id.iv_avatar:
                    case R.id.tv_name:
                        mOnItemClickListener.onNameClick(v, (String) v.getTag());
                        break;
                    default:
                        mOnItemClickListener.onItemClick(v, (Notification) v.getTag());
                        break;
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onItemLongClick(v, (Notification) v.getTag());
            }
            return true;
        }

        @Override
        public void clickUrl(String url) {
            if (url.contains("http")) {
                if (url.startsWith("https://www.diycode.cc/topics/")) {
                    ARouter.getInstance().build(TOPIC_DETAIL)
                            .withInt(EXTRA_TOPIC_ID, Integer.valueOf(url.substring(30)))
                            .navigation();
                    return;
                }
                DiycodeUtils.openWebActivity(url);
            } else if (url.startsWith("/")) {
                ARouter.getInstance().build(USER_DETAIL)
                        .withString(EXTRA_USERNAME, url.substring(1))
                        .navigation();
            }
        }

        @Override
        public void clickImage(String source) {
            ARouter.getInstance().build(PUBLIC_PHOTO)
                    .withString(EXTRA_PHOTO_URL, source)
                    .navigation();
        }

        @Override
        protected void onRelease() {
            mImageLoader.clear(mAppComponent.application(), GlideImageConfig.builder().imageViews(mIvAvatar).build());
        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, Notification notification);

        void onNameClick(View view, String username);
    }

    public interface OnItemLongClickListener {

        void onItemLongClick(View view, Notification notification);
    }
}
