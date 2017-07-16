package com.sneider.diycode.mvp.ui.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.NetworkUtils;
import com.jess.arms.di.component.AppComponent;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.WebImageListener;
import com.sneider.diycode.utils.WebViewUtils;
import com.sneider.diycode.widget.WebViewProgressBar;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;
import butterknife.OnClick;

import static com.sneider.diycode.app.ARouterPaths.PUBLIC_WEB;
import static com.sneider.diycode.app.ARouterPaths.TOPIC_DETAIL;
import static com.sneider.diycode.mvp.ui.activity.TopicDetailActivity.EXTRA_TOPIC_ID;

@Route(path = PUBLIC_WEB)
public class WebActivity extends BaseActivity {

    public static final String EXTRA_URL = "EXTRA_URL";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.web_view) WebView mWebView;
    @BindView(R.id.progress_bar) WebViewProgressBar mProgressBar;
    @BindView(R.id.progress_bar1) ProgressBar mProgressBar1;
    @BindView(R.id.tv_hint) TextView mTvHint;

    private boolean isContinue;
    private String mUrl;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_web;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_close);
        mToolbar.setOnClickListener(v -> mWebView.scrollTo(0, 0));
        setSupportActionBar(mToolbar);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        WebImageListener listener = new WebImageListener(this, ImageActivity.class);
        mWebView.addJavascriptInterface(listener, "listener");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                WebViewUtils.addImageClickListener(mWebView);
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("https://www.diycode.cc/topics/")) {
                    ARouter.getInstance().build(TOPIC_DETAIL)
                            .withInt(EXTRA_TOPIC_ID, Integer.valueOf(url.substring(30)))
                            .navigation();
                    return true;
                }
                view.loadUrl(url);
                return true;
            }

            // https的处理方式
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            // 错误页面的逻辑处理
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //errorOperation();
                mWebView.setVisibility(View.INVISIBLE);
                mTvHint.setVisibility(View.VISIBLE);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //loadingOperation(newProgress);
                if (newProgress == 100) {
                    hideProgressWithAnim(mProgressBar1);
                } else {
                    mProgressBar1.setVisibility(View.VISIBLE);
                }
                mProgressBar1.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                mToolbar.setTitle(title);
            }
        });

        mUrl = getIntent().getStringExtra(EXTRA_URL);
        if (!TextUtils.isEmpty(mUrl)) {
            mWebView.loadUrl(mUrl);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_web_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_share) {
            DiycodeUtils.shareText(this, mWebView.getTitle(), mWebView.getUrl());
        } else if (id == R.id.action_refresh) {
            mWebView.reload();
        } else if (id == R.id.action_open_browser) {
            DiycodeUtils.openBrowser(this, mUrl);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        clearWebViewResource();
        super.onDestroy();
    }

    private void clearWebViewResource() {
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

    @OnClick(R.id.tv_hint)
    void clickHint() {
        mTvHint.setVisibility(View.INVISIBLE);
        mWebView.setVisibility(View.VISIBLE);
        mWebView.reload();
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    private void loadingOperation(int newProgress) {
        // 如果没有网络直接跳出方法
        if (!NetworkUtils.isAvailableByPing()) {
            return;
        }
        // 如果进度条隐藏则让它显示
        if (View.INVISIBLE == mProgressBar.getVisibility()) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        // 大于80的进度的时候，放慢速度加载，否则交给自己加载
        if (newProgress >= 80) {
            // 拦截webView自己的处理方式
            if (isContinue) {
                return;
            }
            mProgressBar.setCurProgress(100, 3000, () -> {
                finishOperation(true);
                isContinue = false;
            });
            isContinue = true;
        } else {
            mProgressBar.setNormalProgress(newProgress);
        }
    }

    private void errorOperation() {
        // 隐藏webview
        mWebView.setVisibility(View.INVISIBLE);
        if (View.INVISIBLE == mProgressBar.getVisibility()) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        //3.5s 加载 0->80 进度的加载 为了实现，特意调节长了事件
        mProgressBar.setCurProgress(80, 3500, () -> {
            //3.5s 加载 80->100 进度的加载
            mProgressBar.setCurProgress(100, 3500, () -> finishOperation(false));
        });
    }

    private void finishOperation(boolean flag) {
        // 最后加载设置100进度
        mProgressBar.setNormalProgress(100);
        // 显示网络异常布局
        mTvHint.setVisibility(flag ? View.INVISIBLE : View.VISIBLE);
        hideProgressWithAnim(mProgressBar);
    }

    private void hideProgressWithAnim(final View progressBar) {
        AnimationSet animation = new AnimationSet(this, null);
        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
        alpha.setDuration(1000);
        animation.addAnimation(alpha);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        progressBar.startAnimation(animation);
    }
}
