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
import com.sneider.diycode.mvp.model.bean.Project;
import com.sneider.diycode.utils.GlideCircleTransform;

import java.util.List;

import butterknife.BindView;

public class ProjectListAdapter extends DefaultAdapter<Project> {

    public ProjectListAdapter(List<Project> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<Project> getHolder(View v, int viewType) {
        return new ProjectItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_project;
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    class ProjectItemHolder extends BaseHolder<Project> {

        @BindView(R.id.iv_avatar) ImageView mIvAvatar;
        @BindView(R.id.tv_name) TextView mTvName;
        @BindView(R.id.tv_star) TextView mTvStar;
        @BindView(R.id.tv_category) TextView mTvCategory;
        @BindView(R.id.tv_sub_category) TextView mTvSubCategory;
        @BindView(R.id.tv_desc) TextView mTvDesc;

        private AppComponent mAppComponent;
        private ImageLoader mImageLoader;

        ProjectItemHolder(View itemView) {
            super(itemView);
            mAppComponent = ((App) itemView.getContext().getApplicationContext()).getAppComponent();
            mImageLoader = mAppComponent.imageLoader();
        }

        @Override
        public void setData(Project data, int position) {
            String avatarUrl = data.getProject_cover_url();
            mImageLoader.loadImage(mAppComponent.application(), GlideImageConfig.builder()
                    .transformation(new GlideCircleTransform(mAppComponent.application()))
                    .url(avatarUrl).imageView(mIvAvatar).build());
            mTvName.setText(data.getName());
            mTvStar.setText(data.getStar() + "");
            mTvCategory.setText(data.getCategory().getName());
            mTvSubCategory.setText(data.getSub_category().getName());
            mTvDesc.setText(data.getDescription());

            itemView.setTag(data);
            itemView.setOnClickListener(this);
            mIvAvatar.setTag(data.getGithub());
            mIvAvatar.setOnClickListener(this);
            mTvName.setTag(data.getGithub());
            mTvName.setOnClickListener(this);
            mTvCategory.setTag(data.getCategory().getName());
            mTvCategory.setOnClickListener(this);
            mTvSubCategory.setTag(data.getSub_category().getId());
            mTvSubCategory.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                switch (v.getId()) {
                    case R.id.iv_avatar:
                    case R.id.tv_name:
                        mOnItemClickListener.onNameClick(v, (String) v.getTag());
                        break;
                    case R.id.tv_category:
                        mOnItemClickListener.onCategoryClick(v, (String) v.getTag());
                        break;
                    case R.id.tv_sub_category:
                        mOnItemClickListener.onSubCategoryClick(v, (int) v.getTag());
                        break;
                    default:
                        mOnItemClickListener.onItemClick(v, (Project) v.getTag());
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

        void onItemClick(View view, Project project);

        void onNameClick(View view, String github);

        void onCategoryClick(View view, String categoryName);

        void onSubCategoryClick(View view, int subCategoryId);
    }
}
