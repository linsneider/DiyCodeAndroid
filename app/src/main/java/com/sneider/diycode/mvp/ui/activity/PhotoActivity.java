package com.sneider.diycode.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.github.chrisbanes.photoview.PhotoView;
import com.jess.arms.di.component.AppComponent;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

import static com.sneider.diycode.app.ARouterPaths.PUBLIC_PHOTO;

@Route(path = PUBLIC_PHOTO)
public class PhotoActivity extends BaseActivity {

    public static final String EXTRA_PHOTO_URL = "EXTRA_PHOTO_URL";

    @BindView(R.id.photo_view) PhotoView mPhotoView;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.tv_progress) TextView mTvProgress;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_photo;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        // 不显示系统的标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String photoUrl = getIntent().getStringExtra(EXTRA_PHOTO_URL);
        if (photoUrl.contains("diycode"))
            photoUrl = photoUrl.replace("large_avatar", "avatar");

        Glide.with(this).load(photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new GlideDrawableImageViewTarget(mPhotoView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    @OnClick(R.id.photo_view)
    void toggle() {
        finish();
    }
}
