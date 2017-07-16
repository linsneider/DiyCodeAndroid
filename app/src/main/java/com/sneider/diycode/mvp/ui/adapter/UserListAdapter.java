package com.sneider.diycode.mvp.ui.adapter;

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
import com.sneider.diycode.mvp.model.bean.User;
import com.sneider.diycode.utils.GlideCircleTransform;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;

public class UserListAdapter extends DefaultAdapter<User> {

    public UserListAdapter(List<User> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<User> getHolder(View v, int viewType) {
        return new UserItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_user;
    }

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    class UserItemHolder extends BaseHolder<User> implements View.OnLongClickListener {

        @BindView(R.id.iv_avatar) ImageView mIvAvatar;
        @BindView(R.id.tv_username) TextView mTvUsername;
        @BindView(R.id.tv_number) TextView mTvNumber;

        private AppComponent mAppComponent;
        private ImageLoader mImageLoader;

        UserItemHolder(View itemView) {
            super(itemView);
            mAppComponent = ((App) itemView.getContext().getApplicationContext()).getAppComponent();
            mImageLoader = mAppComponent.imageLoader();
        }

        @Override
        public void setData(User data, int position) {
            String avatarUrl = data.getAvatar_url();
            if (avatarUrl.contains("diycode"))
                avatarUrl = avatarUrl.replace("large_avatar", "avatar");
            mImageLoader.loadImage(mAppComponent.application(), GlideImageConfig.builder()
                    .transformation(new GlideCircleTransform(mAppComponent.application()))
                    .url(avatarUrl).imageView(mIvAvatar).build());
            mTvUsername.setText(data.getLogin() + "(" + data.getName() + ")");
            mTvNumber.setText(MessageFormat.format(mAppComponent.application().getString(R.string.what_number), data.getId()));

            itemView.setTag(data);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                switch (v.getId()) {
                    default:
                        mOnItemClickListener.onItemClick(v, (User) v.getTag());
                        break;
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onItemLongClick(v, (User) v.getTag());
            }
            return true;
        }

        @Override
        protected void onRelease() {
            mImageLoader.clear(mAppComponent.application(), GlideImageConfig.builder().imageViews(mIvAvatar).build());
        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, User user);
    }

    public interface OnItemLongClickListener {

        void onItemLongClick(View view, User user);
    }
}
