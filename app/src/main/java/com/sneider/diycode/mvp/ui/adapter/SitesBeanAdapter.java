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
import com.sneider.diycode.mvp.model.bean.Sites;

import java.util.List;

import butterknife.BindView;

class SitesBeanAdapter extends DefaultAdapter<Sites.SitesBean> {

    SitesBeanAdapter(List<Sites.SitesBean> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<Sites.SitesBean> getHolder(View v, int viewType) {
        return new SitesBeanItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_sitesbean;
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    class SitesBeanItemHolder extends BaseHolder<Sites.SitesBean> implements View.OnClickListener {

        @BindView(R.id.iv_avatar) ImageView mIvAvatar;
        @BindView(R.id.tv_name) TextView mTvName;

        private AppComponent mAppComponent;
        private ImageLoader mImageLoader;

        SitesBeanItemHolder(View itemView) {
            super(itemView);
            mAppComponent = ((App) itemView.getContext().getApplicationContext()).getAppComponent();
            mImageLoader = mAppComponent.imageLoader();
        }

        @Override
        public void setData(Sites.SitesBean data, int position) {
            mImageLoader.loadImage(mAppComponent.application(), GlideImageConfig.builder()
                    .url(data.getAvatar_url()).imageView(mIvAvatar).build());
            mTvName.setText(data.getName());

            itemView.setTag(data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, (Sites.SitesBean) v.getTag());
            }
        }

        @Override
        protected void onRelease() {
            mImageLoader.clear(mAppComponent.application(), GlideImageConfig.builder().imageViews(mIvAvatar).build());
        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, Sites.SitesBean sitesBean);
    }
}
