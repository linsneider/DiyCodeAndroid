package com.sneider.diycode.utils.html;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.sneider.diycode.R;

import java.util.HashSet;
import java.util.Set;

public final class GlideImageGetter implements Html.ImageGetter, Drawable.Callback {


    private final TextView mTextView;
    private final int mMaxWidth;
    private final Set<ImageGetterViewTarget> mTargets;

    public static GlideImageGetter get(View view) {
        return (GlideImageGetter) view.getTag(R.id.drawable_callback_tag);
    }

    public void clear() {
        GlideImageGetter prev = get(mTextView);
        if (prev == null) {
            return;
        }

        //prev.mTargets.forEach(Glide::clear);
        for (ImageGetterViewTarget target : prev.mTargets) {
            Glide.clear(target);
        }
    }

    public GlideImageGetter(TextView textView, int maxWidth) {
        mTextView = textView;
        mMaxWidth = maxWidth;

        clear();
        mTargets = new HashSet<>();
        mTextView.setTag(R.id.drawable_callback_tag, this);
    }

    @Override
    public Drawable getDrawable(String url) {
        final UrlDrawable urlDrawable = new UrlDrawable(url);

        Glide.with(mTextView.getContext())
                .load(url)
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new ImageGetterViewTarget(mTextView, urlDrawable, mMaxWidth));

        return urlDrawable;

    }

    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        mTextView.invalidate();
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {

    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {

    }

    private class ImageGetterViewTarget extends ViewTarget<TextView, GlideDrawable> {

        private final UrlDrawable mDrawable;
        private final int mWidth;

        private ImageGetterViewTarget(TextView view, UrlDrawable drawable, int maxWidth) {
            super(view);
            mTargets.add(this);
            this.mDrawable = drawable;
            mWidth = maxWidth;
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            super.onLoadStarted(placeholder);
            final double aspectRatio =
                    (1.0 * placeholder.getIntrinsicWidth()) / placeholder.getIntrinsicHeight();
            if (mDrawable.getSource()
                    .startsWith("https://diycode.b0.upaiyun.com/assets/emojis/")) {
                return;
            }
            final int width = Math.min(placeholder.getIntrinsicWidth(), mWidth);
            final int height = (int) (width / aspectRatio);
            Rect rect = new Rect(0, 0, width, height);
            placeholder.setBounds(rect);
            mDrawable.setBounds(rect);
            mDrawable.setDrawable(placeholder);
            getView().setText(getView().getText());
            getView().invalidate();
        }


        @Override
        public void onResourceReady(GlideDrawable resource,
                                    GlideAnimation<? super GlideDrawable> glideAnimation) {
            final double aspectRatio =
                    (1.0 * resource.getIntrinsicWidth()) / resource.getIntrinsicHeight();
            boolean isEmoji = mDrawable.getSource()
                    .startsWith("https://diycode.b0.upaiyun.com/assets/emojis/");
            final int width = isEmoji ? resource.getIntrinsicWidth() : mWidth;
            final int height = (int) (width / aspectRatio);
            Rect rect = new Rect(0, 0, width, height);
            resource.setBounds(rect);
            mDrawable.setBounds(rect);
            mDrawable.setDrawable(resource);

            if (resource.isAnimated()) {
                mDrawable.setCallback(get(getView()));
                resource.setLoopCount(GlideDrawable.LOOP_FOREVER);
                resource.start();
            }

            getView().setText(getView().getText());
            getView().invalidate();
        }

        private Request mRequest;

        @Override
        public Request getRequest() {
            return mRequest;
        }

        @Override
        public void setRequest(Request request) {
            this.mRequest = request;
        }
    }
}