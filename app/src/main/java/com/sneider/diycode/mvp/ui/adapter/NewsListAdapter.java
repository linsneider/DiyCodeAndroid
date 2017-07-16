package com.sneider.diycode.mvp.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.base.App;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.widget.imageloader.ImageLoader;
import com.jess.arms.widget.imageloader.glide.GlideImageConfig;
import com.sneider.diycode.R;
import com.sneider.diycode.mvp.model.bean.News;
import com.sneider.diycode.utils.DateUtils;
import com.sneider.diycode.utils.GlideCircleTransform;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import okhttp3.HttpUrl;

public class NewsListAdapter extends DefaultAdapter<News> {

    public NewsListAdapter(List<News> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<News> getHolder(View v, int viewType) {
        return new NewsItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_news;
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    class NewsItemHolder extends BaseHolder<News> {

        @BindView(R.id.iv_avatar) ImageView mIvAvatar;
        @BindView(R.id.tv_name) TextView mTvName;
        @BindView(R.id.tv_node_name) TextView mTvNodeName;
        @BindView(R.id.tv_time) TextView mTvTime;
        @BindView(R.id.tv_title) TextView mTvTitle;
        @BindView(R.id.tv_address) TextView mTvAddress;

        private AppComponent mAppComponent;
        private ImageLoader mImageLoader;

        NewsItemHolder(View itemView) {
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
            String lastReplyUserLogin = data.getLast_reply_user_login();
            if (TextUtils.isEmpty(lastReplyUserLogin)) {
                String intervalTime = DateUtils.getIntervalTime(data.getCreated_at());
                mTvTime.setText(MessageFormat.format(itemView.getResources().getString(R.string.publish_time), intervalTime));
            } else {
                String intervalTime = DateUtils.getIntervalTime(data.getReplied_at());
                mTvTime.setText(MessageFormat.format(mAppComponent.application().getString(R.string.what_who_reply),
                        data.getReplies_count(), lastReplyUserLogin, intervalTime));
            }
            mTvTitle.setText(data.getTitle());
            mTvAddress.setText(HttpUrl.parse(data.getAddress()).host());

            itemView.setTag(data);
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
                        mOnItemClickListener.onNameClick(v, (String) v.getTag());
                        break;
                    case R.id.tv_node_name:
                        mOnItemClickListener.onNodeNameClick(v, (String) v.getTag(R.id.tag_first), (int) v.getTag(R.id.tag_second));
                        break;
                    case R.id.tv_title:
                    case R.id.tv_address:
                        mOnItemClickListener.onNewsClick(v, (String) v.getTag());
                        break;
                    default:
                        mOnItemClickListener.onItemClick(v, (News) v.getTag());
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

        void onItemClick(View view, News news);

        void onNameClick(View view, String username);

        void onNodeNameClick(View view, String nodeName, int nodeId);

        void onNewsClick(View view, String url);
    }
}
