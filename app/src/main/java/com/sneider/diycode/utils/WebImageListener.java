package com.sneider.diycode.utils;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;

import static com.sneider.diycode.app.ARouterPaths.PUBLIC_PHOTO;
import static com.sneider.diycode.mvp.ui.activity.PhotoActivity.EXTRA_PHOTO_URL;

public class WebImageListener {

    private Context mContext;
    private Class mImageActivity;
    private ArrayList<String> mImages = new ArrayList<>();

    public WebImageListener(Context context, Class imageActivity) {
        mContext = context;
        mImageActivity = imageActivity;
    }

    @JavascriptInterface
    public void collectImage(String url) {
        if (!url.startsWith("http")
                || url.startsWith("https://mp.weixin.qq.com/")
                || url.startsWith("https://res.wx.qq.com/")
                ) {
            return;
        }
        if (!mImages.contains(url))
            mImages.add(url);
    }

    @JavascriptInterface
    public void onImageClicked(String url) {
//        Logger.e("clicked:" + url);
//        if (mImageActivity != null) {
//            Intent intent = new Intent(mContext, mImageActivity);
//            intent.putExtra(ImageActivity.CURRENT_IMAGE_URL, url);
//            intent.putExtra(ImageActivity.ALL_IMAGE_URLS, mImages);
//            mContext.startActivity(intent);
//        }
        ARouter.getInstance().build(PUBLIC_PHOTO)
                .withString(EXTRA_PHOTO_URL, url)
                .navigation();
    }
}
