package com.sneider.diycode.utils;

import android.text.TextUtils;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

public class WebViewUtils {

    public static void initWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        // 1、设置WebView是否支持使用屏幕控件或手势进行缩放，默认是true，支持缩放。
        settings.setSupportZoom(false);
        // 2、设置WebView是否通过手势触发播放媒体，默认是true，需要手势触发。
        settings.setMediaPlaybackRequiresUserGesture(true);
        // 3、设置WebView是否使用其内置的变焦机制，该机制集合屏幕缩放控件使用，默认是false，不使用内置变焦机制。
        settings.setBuiltInZoomControls(false);
        // 4、设置WebView使用内置缩放机制时，是否展现在屏幕缩放控件上，默认true，展现在控件上。
        settings.setDisplayZoomControls(false);
        // 5、设置在WebView内部是否允许访问文件，默认允许访问。
        settings.setAllowFileAccess(true);
        // 6、是否允许在WebView中访问内容URL(Content Url)，默认允许。内容Url访问允许WebView从安装在系统中的内容提供者载入内容。
        settings.setAllowContentAccess(true);
        // 7、设置WebView是否使用预览模式加载界面。
        settings.setLoadWithOverviewMode(true);
        // 8、设置WebView是否保存表单数据，默认true，保存数据。
        settings.setSaveFormData(true);
        // 9、设置WebView中加载页面字体变焦百分比，默认100，整型数。
        settings.setTextZoom(100);
        // 10、设置WebView访问第三方Cookies策略，参考CookieManager提供的方法：setShouldAcceptThirdPartyCookies。
        // settings.setAcceptThirdPartyCookies(false);
        // 11、设置WebView是否使用viewport，当该属性被设置为false时，加载页面的宽度总是适应WebView控件宽度；
        // 当被设置为true，当前页面包含viewport属性标签，在标签中指定宽度值生效，如果页面不包含viewport标签，
        // 无法提供一个宽度值，这个时候该方法将被使用。
        settings.setUseWideViewPort(false);
        // 12、设置WebView是否支持多屏窗口，参考WebChromeClient#onCreateWindow，默认false，不支持。
        settings.setSupportMultipleWindows(false);
        // 13、设置WebView底层的布局算法，参考LayoutAlgorithm#NARROW_COLUMNS，将会重新生成WebView布局。
        // settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 14、设置WebView标准字体库字体，默认字体“sans-serif”。
        settings.setStandardFontFamily("sans-serif");
        // 15、设置WebView固定的字体库字体，默认“monospace”。
        settings.setFixedFontFamily("monospace");
        // 16、设置WebView Sans SeriFontFamily字体库字体，默认“sans-serif”。
        settings.setSansSerifFontFamily("sans-serif");
        // 17、设置WebView seri FontFamily字体库字体，默认“sans-serif”。
        settings.setSerifFontFamily("sans-serif");
        // 18、设置WebView字体库字体，默认“cursive”。
        settings.setCursiveFontFamily("cursive");
        // 19、设置WebView字体库字体，默认“fantasy”。
        settings.setFantasyFontFamily("fantasy");
        // 20、设置WebView字体最小值，默认值8，取值1到72。
        settings.setMinimumFontSize(8);
        // 21、设置WebView逻辑上最小字体值，默认值8，取值1到72。
        settings.setMinimumLogicalFontSize(8);
        // 22、设置WebView默认值字体值，默认值16，取值1到72。
        settings.setDefaultFontSize(16);
        // 23、设置WebView默认固定的字体值，默认值16，取值1到72。
        settings.setDefaultFixedFontSize(16);
        // 24、设置WebView是否加载图片资源，默认true，自动加载图片。
        settings.setLoadsImagesAutomatically(true);
        // 25、设置WebView是否以http、https方式访问从网络加载图片资源，默认false。
        settings.setBlockNetworkImage(false);
        // 26、设置WebView是否从网络加载资源，Application需要设置访问网络权限，否则报异常。
        settings.setBlockNetworkLoads(false);
        // 27、设置WebView是否允许执行JavaScript脚本，默认false，不允许。
        settings.setJavaScriptEnabled(true);
        // 28、设置WebView运行中的脚本可以是否访问任何原始起点内容，默认true。
        settings.setAllowUniversalAccessFromFileURLs(true);
        // 29、设置WebView运行中的一个文件方案被允许访问其他文件方案中的内容，默认值true。
        settings.setAllowFileAccessFromFileURLs(true);
        // 30、设置WebView保存地理位置信息数据路径，指定的路径Application具备写入权限。
        // settings.setGeolocationDatabasePath(String path);
        // 31、设置Application缓存API是否开启，默认false，设置有效的缓存路径参考setAppCachePath(String path)方法。
        settings.setAppCacheEnabled(true);
        // 32、设置当前Application缓存文件路径，Application Cache API能够开启需要指定Application具备写入权限的路径。
        // settings.setAppCachePath(String appCachePath);
        // 33、设置是否开启数据库存储API权限，默认false，未开启，可以参考setDatabasePath(String path)。
        settings.setDatabaseEnabled(true);
        // 34、设置是否开启DOM存储API权限，默认false，未开启，设置为true，WebView能够使用DOM storage API。
        settings.setDomStorageEnabled(true);
        // 35、设置是否开启定位功能，默认true，开启定位。
        settings.setGeolocationEnabled(true);
        // 36、设置脚本是否允许自动打开弹窗，默认false，不允许。
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 37、设置WebView加载页面文本内容的编码，默认“UTF-8”。
        settings.setDefaultTextEncodingName("UTF-8");
        // 38、设置WebView代理字符串，如果String为null或为空，将使用系统默认值。
        // settings.setUserAgentString(String ua);
        // 39、设置WebView是否需要设置一个节点获取焦点当被回调的时候，默认true。
        settings.setNeedInitialFocus(true);
        // 40、重写缓存被使用到的方法，该方法基于Navigation Type，加载普通的页面，将会检查缓存同时重新验证是否需要加载，
        // 如果不需要重新加载，将直接从缓存读取数据，
        // 允许客户端通过指定LOAD_DEFAULT、LOAD_CACHE_ELSE_NETWORK、LOAD_NO_CACHE、LOAD_CACHE_ONLY其中之一重写该行为方法，
        // 默认值LOAD_DEFAULT。
        // settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 41、设置当一个安全站点企图加载来自一个不安全站点资源时WebView的行为，
        // android.os.Build.VERSION_CODES.KITKAT默认为MIXED_CONTENT_ALWAYS_ALLOW，
        // android.os.Build.VERSION_CODES#LOLLIPOP默认为MIXED_CONTENT_NEVER_ALLOW，
        // 取值其中之一：MIXED_CONTENT_NEVER_ALLOW、MIXED_CONTENT_ALWAYS_ALLOW、MIXED_CONTENT_COMPATIBILITY_MODE。
        // settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
    }

    public static void addImageClickListener(WebView webView) {
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{" +
                "  window.listener.collectImage(objs[i].src); " +
                "  objs[i].onclick=function()  " +
                "  {  " +
                "    window.listener.onImageClicked(this.src);  " +
                "  }  " +
                "}" +
                "})()");
    }

    public static String convertTopicContent(String content) {
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(content.trim())) return "";

        // 过滤掉 img标签的width,height属性
        content = content.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
        content = content.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");

        // 添加点击查看大图
        content = content.replaceAll("<img[^>]+src=\"([^\"\'\\s]+)\"[^>c]*>(?!((?!</?a\\b).)*</a>)",
                "<img src=\"$1\" onClick=\"javascript:listener.onImageClicked('$1')\"/>");

        // 过滤table的内部属性
        content = content.replaceAll("(<table[^>]*?)\\s+border\\s*=\\s*\\S+", "$1");
        content = content.replaceAll("(<table[^>]*?)\\s+cellspacing\\s*=\\s*\\S+", "$1");
        content = content.replaceAll("(<table[^>]*?)\\s+cellpadding\\s*=\\s*\\S+", "$1");

        return String.format("<!DOCTYPE html>"
                + "<html><head>"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/html/css/markdown.css\">"
                + "<link rel=\"stylesheet\" href=\"file:///android_asset/html/css/monokai.css\"/>"
                + "<script type=\"text/javascript\" src=\"file:///android_asset/html/js/highlight.pack.js\"></script>"
                + "<script>hljs.initHighlightingOnLoad();</script>"
                + "</head>"
                + "<body>"
                + "<div class=\"markdown\">"
                + "%s"
                + "</div>"
                + "</body></html>", content);
    }
}
