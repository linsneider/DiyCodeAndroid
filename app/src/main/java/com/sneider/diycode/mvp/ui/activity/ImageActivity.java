package com.sneider.diycode.mvp.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;
import com.sneider.diycode.R;
import com.sneider.diycode.utils.DiycodeUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageActivity extends AppCompatActivity {

    public static final String ALL_IMAGE_URLS = "all_image_urls";
    public static final String CURRENT_IMAGE_URL = "current_image_url";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.view_pager) ViewPager mViewPager;

    private ArrayList<String> mImages = new ArrayList<>();    // 所有图片
    private String mCurrentImageUrl;                          // 当前图片
    private int mCurrentImagePosition;                        // 当前图片位置

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);

        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(mToolbar);

        mCurrentImageUrl = getIntent().getStringExtra(CURRENT_IMAGE_URL);
        mImages = getIntent().getStringArrayListExtra(ALL_IMAGE_URLS);
        mCurrentImagePosition = mImages.indexOf(mCurrentImageUrl);
        mToolbar.setTitle((mCurrentImagePosition + 1) + " / " + mImages.size());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mToolbar.setTitle((position + 1) + " / " + mImages.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mImages.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                PhotoView photoView = (PhotoView) getLayoutInflater().inflate(R.layout.item_image, container, false);
                Glide.with(ImageActivity.this).load(mImages.get(position)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(photoView);
                container.addView(photoView);
                return photoView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        mViewPager.setCurrentItem(mCurrentImagePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_image_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_share) {
            DiycodeUtils.shareImage(this, mCurrentImageUrl);
        } else if (id == R.id.action_save) {
        }
        return super.onOptionsItemSelected(item);
    }
}
