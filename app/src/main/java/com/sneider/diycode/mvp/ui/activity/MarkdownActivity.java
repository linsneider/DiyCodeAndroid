package com.sneider.diycode.mvp.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jess.arms.di.component.AppComponent;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.WebImageListener;
import com.sneider.diycode.utils.WebViewUtils;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;

import static com.sneider.diycode.app.ARouterPaths.PUBLIC_MARKDOWN;

@Route(path = PUBLIC_MARKDOWN)
public class MarkdownActivity extends BaseActivity {

    public static final String EXTRA_CONTENT = "EXTRA_CONTENT";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.web_view) WebView mWebView;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_web;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mToolbar.setTitle(R.string.preview);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setOnClickListener(v -> mWebView.pageUp(true));
        setSupportActionBar(mToolbar);

        String content = getIntent().getStringExtra(EXTRA_CONTENT);

//        if (Build.VERSION.SDK_INT >= 21) {
//            WebView.enableSlowWholeDocumentDraw();
//        }
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        WebImageListener listener = new WebImageListener(this, ImageActivity.class);
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
                mWebView.loadUrl("javascript:parseMarkdown(\"" + content.replace("\n", "\\n")
                        .replace("\"", "\\\"")
                        .replace("'", "\\'") + "\", " + true + ")");
                WebViewUtils.addImageClickListener(mWebView);
            }
        });
        mWebView.loadUrl("file:///android_asset/markdown.html");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
}
