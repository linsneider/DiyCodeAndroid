package com.sneider.diycode.mvp.ui.adapter;

import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
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
import com.sneider.diycode.mvp.model.bean.Project;
import com.sneider.diycode.mvp.model.bean.Reply;
import com.sneider.diycode.mvp.ui.activity.ImageActivity;
import com.sneider.diycode.utils.DateUtils;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.GlideCircleTransform;
import com.sneider.diycode.utils.WebImageListener;
import com.sneider.diycode.utils.WebViewUtils;
import com.sneider.diycode.utils.html.HtmlUtils;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;

public class ProjectDetailAdapter extends DefaultAdapter {

    private static final int TYPE_PROJECT_DETAIL = 1;
    private static final int TYPE_REPLY = 2;

    public ProjectDetailAdapter(List infos) {
        super(infos);
    }

    @Override
    public BaseHolder getHolder(View v, int viewType) {
        return viewType == TYPE_PROJECT_DETAIL ? new NewsDetailViewHolder(v) : new ReplyViewHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return viewType == TYPE_PROJECT_DETAIL ? R.layout.item_project_detail : R.layout.item_reply;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_PROJECT_DETAIL : TYPE_REPLY;
    }

    private OnItemClickListener mOnItemClickListener;
    private HtmlUtils.Callback mCallback;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setCallback(HtmlUtils.Callback callback) {
        mCallback = callback;
    }

    class NewsDetailViewHolder extends BaseHolder<Project> {

        @BindView(R.id.iv_project_avatar) ImageView mIvProjectAvatar;
        @BindView(R.id.tv_project_name) TextView mTvProjectName;
        @BindView(R.id.tv_star) TextView mTvStar;
        @BindView(R.id.tv_category) TextView mTvCategory;
        @BindView(R.id.tv_sub_category) TextView mTvSubCategory;
        @BindView(R.id.tv_desc) TextView mTvDesc;
        @BindView(R.id.web_view) WebView mWebView;
        @BindView(R.id.tv_reply_count) TextView mTvReplyCount;

        private AppComponent mAppComponent;
        private ImageLoader mImageLoader;

        NewsDetailViewHolder(View itemView) {
            super(itemView);
            mAppComponent = ((App) itemView.getContext().getApplicationContext()).getAppComponent();
            mImageLoader = mAppComponent.imageLoader();
        }

        @Override
        public void setData(Project data, int position) {
            String avatarUrl = data.getProject_cover_url();
            mImageLoader.loadImage(mAppComponent.application(), GlideImageConfig.builder()
                    .transformation(new GlideCircleTransform(mAppComponent.application()))
                    .url(avatarUrl).imageView(mIvProjectAvatar).build());
            mTvProjectName.setText(data.getName());
            mTvStar.setText(String.valueOf(data.getStar()));
            mTvCategory.setText(data.getCategory().getName());
            mTvSubCategory.setText(data.getSub_category().getName());
            mTvDesc.setText(data.getDescription());
            WebSettings settings = mWebView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            WebImageListener listener = new WebImageListener(mAppComponent.application(), ImageActivity.class);
            mWebView.addJavascriptInterface(listener, "listener");
            mWebView.setVerticalScrollBarEnabled(false);
            mWebView.setHorizontalScrollBarEnabled(false);
            mWebView.setWebViewClient(new WebViewClient() {
                @SuppressWarnings("deprecation")
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    DiycodeUtils.openWebActivity(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    mWebView.loadUrl("javascript:parseMarkdown(\"" + data.getReadme().replace("\n", "\\n")
                            .replace("\"", "\\\"")
                            .replace("'", "\\'") + "\", " + true + ")");
                    WebViewUtils.addImageClickListener(mWebView);
                }
            });
            mWebView.loadUrl("file:///android_asset/markdown.html");
            mTvReplyCount.setText(mInfos.size() == 1 ? mAppComponent.application().getString(R.string.no_reply) :
                    MessageFormat.format(mAppComponent.application().getString(R.string.what_reply), mInfos.size() - 1));

            itemView.setOnClickListener(this);
            mTvProjectName.setTag(data.getGithub());
            mTvProjectName.setOnClickListener(this);
            mTvCategory.setTag(data.getCategory().getName());
            mTvCategory.setOnClickListener(this);
            mTvSubCategory.setTag(data.getSub_category().getId());
            mTvSubCategory.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                switch (v.getId()) {
                    case R.id.iv_project_avatar:
                    case R.id.tv_project_name:
                        mOnItemClickListener.onNameClick(v, (String) v.getTag());
                        break;
                    case R.id.tv_category:
                        mOnItemClickListener.onCategoryClick(v, (String) v.getTag());
                        break;
                    case R.id.tv_sub_category:
                        mOnItemClickListener.onSubCategoryClick(v, (int) v.getTag());
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        protected void onRelease() {
            mImageLoader.clear(mAppComponent.application(), GlideImageConfig.builder().imageViews(mIvProjectAvatar).build());
            if (mWebView != null) {
                mWebView.removeAllViews();
                // in android 5.1(sdk:21) we should invoke this to avoid memory leak
                // see (https://coolpers.github.io/webview/memory/leak/2015/07/16/android-5.1-webview-memory-leak.html)
                ((ViewGroup) mWebView.getParent()).removeView(mWebView);
                mWebView.setTag(null);
                mWebView.clearHistory();
                mWebView.destroy();
                mWebView = null;
            }
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

        void onNameClick(View view, String github);

        void onCategoryClick(View view, String categoryName);

        void onSubCategoryClick(View view, int subCategoryId);

        void onUserClick(View view, String username);

        void onEditReplyClick(View view, Reply reply);

        void onLikeReplyClick(View view, Reply reply);

        void onReplyClick(View view, Reply reply, int floor);
    }
}
