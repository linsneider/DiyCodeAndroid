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
import com.sneider.diycode.mvp.model.bean.Reply;
import com.sneider.diycode.utils.DateUtils;
import com.sneider.diycode.utils.GlideCircleTransform;
import com.sneider.diycode.utils.html.HtmlUtils;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;

public class TopicReplyAdapter extends DefaultAdapter<Reply> {

    public TopicReplyAdapter(List<Reply> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<Reply> getHolder(View v, int viewType) {
        return new ReplyItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_reply;
    }

    private OnItemClickListener mOnItemClickListener;
    private HtmlUtils.Callback mCallback;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setCallback(HtmlUtils.Callback callback) {
        mCallback = callback;
    }

    class ReplyItemHolder extends BaseHolder<Reply> {

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

        ReplyItemHolder(View itemView) {
            super(itemView);
            mAppComponent = ((App) itemView.getContext().getApplicationContext()).getAppComponent();
            mImageLoader = mAppComponent.imageLoader();
        }

        @Override
        public void setData(Reply data, int position) {
            if (data.isDeleted()) {
                mLayout.setVisibility(View.GONE);
                mTvHint.setVisibility(View.VISIBLE);
                mTvHint.setText(MessageFormat.format(itemView.getResources().getString(R.string.floor_deleted), position + 1));
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
                mTvFloor.setText(MessageFormat.format(itemView.getResources().getString(R.string.floor), position + 1));
                String intervalTime = DateUtils.getIntervalTime(data.getCreated_at());
                mTvTime.setText(intervalTime);
                mBtnEditReply.setVisibility(data.getAbilities().isUpdate() ? View.VISIBLE : View.GONE);
                mTvLikeCount.setText(data.getLikes_count() > 0 ? String.valueOf(data.getLikes_count()) : "");
//                int max = ScreenUtils.getScreenWidth() - itemView.getPaddingLeft()
//                        - itemView.getPaddingRight() - SizeUtils.dp2px(32);
//                HtmlUtils.parseHtmlAndSetText(data.getBody_html(), mTvContent, mCallback, max);
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
                mBtnReply.setTag(R.id.tag_second, position + 1);
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

        void onEditReplyClick(View view, Reply reply);

        void onLikeReplyClick(View view, Reply reply);

        void onReplyClick(View view, Reply reply, int floor);
    }
}
