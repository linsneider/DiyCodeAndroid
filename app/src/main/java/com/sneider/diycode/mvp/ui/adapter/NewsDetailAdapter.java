package com.sneider.diycode.mvp.ui.adapter;

import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jess.arms.base.App;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.widget.imageloader.ImageLoader;
import com.jess.arms.widget.imageloader.glide.GlideImageConfig;
import com.sneider.diycode.R;
import com.sneider.diycode.mvp.model.bean.News;
import com.sneider.diycode.mvp.model.bean.Reply;
import com.sneider.diycode.utils.DateUtils;
import com.sneider.diycode.utils.GlideCircleTransform;
import com.sneider.diycode.utils.html.HtmlUtils;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import okhttp3.HttpUrl;

public class NewsDetailAdapter extends DefaultAdapter {

    private static final int TYPE_NEWS_DETAIL = 1;
    private static final int TYPE_REPLY = 2;

    public NewsDetailAdapter(List infos) {
        super(infos);
    }

    @Override
    public BaseHolder getHolder(View v, int viewType) {
        return viewType == TYPE_NEWS_DETAIL ? new NewsDetailViewHolder(v) : new ReplyViewHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return viewType == TYPE_NEWS_DETAIL ? R.layout.item_news_detail : R.layout.item_reply;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_NEWS_DETAIL : TYPE_REPLY;
    }

    private OnItemClickListener mOnItemClickListener;
    private HtmlUtils.Callback mCallback;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setCallback(HtmlUtils.Callback callback) {
        mCallback = callback;
    }

    class NewsDetailViewHolder extends BaseHolder<News> {

        @BindView(R.id.iv_avatar) ImageView mIvAvatar;
        @BindView(R.id.tv_name) TextView mTvName;
        @BindView(R.id.tv_node_name) TextView mTvNodeName;
        @BindView(R.id.tv_time) TextView mTvTime;
        @BindView(R.id.tv_title) TextView mTvTitle;
        @BindView(R.id.tv_address) TextView mTvAddress;
        @BindView(R.id.tv_reply_count) TextView mTvReplyCount;

        private AppComponent mAppComponent;
        private ImageLoader mImageLoader;

        NewsDetailViewHolder(View itemView) {
            super(itemView);
            mAppComponent = ((App) itemView.getContext().getApplicationContext()).getAppComponent();
            mImageLoader = mAppComponent.imageLoader();
        }

        @Override
        public void setData(News data, int position) {
            String avatarUrl = data.getUser().getAvatar_url();
            if (avatarUrl.contains("diycode"))
                avatarUrl = avatarUrl.replace("large_avatar", "avatar");
            mImageLoader.loadImage(mAppComponent.application(), GlideImageConfig.builder()
                    .transformation(new GlideCircleTransform(mAppComponent.application()))
                    .url(avatarUrl).imageView(mIvAvatar).build());
            mTvName.setText(data.getUser().getLogin());
            mTvNodeName.setText(data.getNode_name());
            String intervalTime = DateUtils.getIntervalTime(data.getCreated_at());
            mTvTime.setText(MessageFormat.format(itemView.getResources().getString(R.string.publish_time), intervalTime));
            mTvTitle.setText(data.getTitle());
            mTvAddress.setText(HttpUrl.parse(data.getAddress()).host());
            mTvReplyCount.setText(data.getReplies_count() == 0 ? mAppComponent.application().getString(R.string.no_reply) :
                    MessageFormat.format(mAppComponent.application().getString(R.string.what_reply), data.getReplies_count()));

            itemView.setOnClickListener(this);
            mIvAvatar.setTag(data.getUser().getLogin());
            mIvAvatar.setOnClickListener(this);
            mTvName.setTag(data.getUser().getLogin());
            mTvName.setOnClickListener(this);
            mTvNodeName.setTag(R.id.tag_first, data.getNode_name());
            mTvNodeName.setTag(R.id.tag_second, data.getNode_id());
            mTvNodeName.setOnClickListener(this);
            mTvTitle.setTag(data.getAddress());
            mTvTitle.setOnClickListener(this);
            mTvAddress.setTag(data.getAddress());
            mTvAddress.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                switch (v.getId()) {
                    case R.id.iv_avatar:
                    case R.id.tv_name:
                        mOnItemClickListener.onUserClick(v, (String) v.getTag());
                        break;
                    case R.id.tv_title:
                    case R.id.tv_address:
                        mOnItemClickListener.onLinkClick(v, (String) v.getTag());
                        break;
                    case R.id.tv_node_name:
                        mOnItemClickListener.onNodeNameClick(v, (String) v.getTag(R.id.tag_first), (int) v.getTag(R.id.tag_second));
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        protected void onRelease() {
            mImageLoader.clear(mAppComponent.application(), GlideImageConfig.builder().imageViews(mIvAvatar).build());
        }
    }

    class ReplyViewHolder extends BaseHolder<Reply> {

        @BindView(R.id.layout) LinearLayout mLayout;
        @BindView(R.id.iv_avatar) ImageView mIvAvatar;
        @BindView(R.id.tv_name) TextView mTvName;
        @BindView(R.id.tv_floor) TextView mTvFloor;
        @BindView(R.id.tv_time) TextView mTvTime;
        @BindView(R.id.btn_edit_reply) ImageView mBtnEditReply;
        @BindView(R.id.btn_like_reply) ImageView mBtnLikeReply;
        @BindView(R.id.tv_like_count) TextView mTvLikeCount;
        @BindView(R.id.btn_reply) ImageView mBtnReply;
        @BindView(R.id.tv_content) TextView mTvContent;
        @BindView(R.id.tv_hint) TextView mTvHint;

        private AppComponent mAppComponent;
        private ImageLoader mImageLoader;

        ReplyViewHolder(View itemView) {
            super(itemView);
            mAppComponent = ((App) itemView.getContext().getApplicationContext()).getAppComponent();
            mImageLoader = mAppComponent.imageLoader();
        }

        @Override
        public void setData(Reply data, int position) {
            if (data.isDeleted()) {
                mLayout.setVisibility(View.GONE);
                mTvHint.setVisibility(View.VISIBLE);
                mTvHint.setText(MessageFormat.format(itemView.getResources().getString(R.string.floor_deleted), position));
                mTvHint.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                mTvHint.setVisibility(View.GONE);
                mLayout.setVisibility(View.VISIBLE);
                String avatarUrl = data.getUser().getAvatar_url();
                if (avatarUrl.contains("diycode"))
                    avatarUrl = avatarUrl.replace("large_avatar", "avatar");
                mImageLoader.loadImage(mAppComponent.application(), GlideImageConfig.builder()
                        .transformation(new GlideCircleTransform(mAppComponent.application()))
                        .url(avatarUrl).imageView(mIvAvatar).build());
                mTvName.setText(data.getUser().getLogin());
                mTvFloor.setText(MessageFormat.format(mAppComponent.application().getString(R.string.what_floor), position));
                String intervalTime = DateUtils.getIntervalTime(data.getCreated_at());
                mTvTime.setText(intervalTime);
                mBtnEditReply.setVisibility(data.getAbilities().isUpdate() ? View.VISIBLE : View.GONE);
                mTvLikeCount.setText(data.getLikes_count() > 0 ? String.valueOf(data.getLikes_count()) : "");
                HtmlUtils.parseHtmlAndSetText(itemView.getContext(), data.getBody_html(), mTvContent, mCallback);

                itemView.setOnClickListener(this);
                mIvAvatar.setTag(data.getUser().getLogin());
                mIvAvatar.setOnClickListener(this);
                mTvName.setTag(data.getUser().getLogin());
                mTvName.setOnClickListener(this);
                mBtnEditReply.setTag(data);
                mBtnEditReply.setOnClickListener(this);
                mBtnLikeReply.setTag(data);
                mBtnLikeReply.setOnClickListener(this);
                mBtnReply.setTag(R.id.tag_first, data);
                mBtnReply.setTag(R.id.tag_second, position);
                mBtnReply.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                switch (v.getId()) {
                    case R.id.iv_avatar:
                    case R.id.tv_name:
                        mOnItemClickListener.onUserClick(v, (String) v.getTag());
                        break;
                    case R.id.btn_edit_reply:
                        mOnItemClickListener.onEditReplyClick(v, (Reply) v.getTag());
                        break;
                    case R.id.btn_like_reply:
                        mOnItemClickListener.onLikeReplyClick(v, (Reply) v.getTag());
                        break;
                    case R.id.btn_reply:
                        mOnItemClickListener.onReplyClick(v, (Reply) v.getTag(R.id.tag_first), (int) v.getTag(R.id.tag_second));
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        protected void onRelease() {
            mImageLoader.clear(mAppComponent.application(), GlideImageConfig.builder().imageViews(mIvAvatar).build());
        }
    }

    public interface OnItemClickListener {

        void onUserClick(View view, String username);

        void onNodeNameClick(View view, String nodeName, int nodeId);

        void onLinkClick(View view, String link);

        void onEditReplyClick(View view, Reply reply);

        void onLikeReplyClick(View view, Reply reply);

        void onReplyClick(View view, Reply reply, int floor);
    }
}
