package com.sneider.diycode.utils.html;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.QuoteSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import com.sneider.diycode.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtils {

    public static String delHTMLTag(String htmlStr) {
        String regexHtml = "<[^>]+>"; //定义HTML标签的正则表达式

        Pattern pHtml = Pattern.compile(regexHtml, Pattern.CASE_INSENSITIVE);
        Matcher mHtml = pHtml.matcher(htmlStr);
        htmlStr = mHtml.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }

    public static String getSimpleHtmlText(String html) {
        String regex = "<img.*src=(.*?)[^>]*?>";
        html = html.replaceAll(regex, "[图片]");
        return delHTMLTag(html).replaceAll("\n", "");
    }

    public static CharSequence trimTrailingWhitespace(CharSequence source) {
        if (source == null) {
            return "";
        }

        int i = source.length();

        while (--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }

        long end = System.currentTimeMillis();

        return source.subSequence(0, i + 1);
    }

    public static CharSequence parseHtmlAndSetText(String source, @NonNull TextView textView,
                                                   int maxWidth) {
        long start = System.currentTimeMillis();
        if (TextUtils.isEmpty(source)) {
            return null;
        }

        Spanned spanned = Html.fromHtml(
                source, new GlideImageGetter(textView, maxWidth), new CodeTagHandler());

        int color = textView.getResources().getColor(R.color.color_999999);
        int background = textView.getResources().getColor(R.color.color_f0f0f0);
        int width = textView.getResources().getDimensionPixelOffset(R.dimen.spacing_xs);
        replaceQuoteSpans((Spannable) spanned, background, color, width);
        URLSpan[] uslSpans = spanned.getSpans(0, spanned.length(), URLSpan.class);
        ImageSpan[] imageSpans = spanned.getSpans(0, spanned.length(), ImageSpan.class);
        SpannableStringBuilder style = new SpannableStringBuilder(spanned);

        for (URLSpan urlSpan : uslSpans) {
            style.setSpan(new ClickableURLSpan(urlSpan.getURL(), null),
                    spanned.getSpanStart(urlSpan),
                    spanned.getSpanEnd(urlSpan),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.removeSpan(urlSpan);
        }

        for (ImageSpan imageSpan : imageSpans) {
            style.setSpan(new ClickableImageSpan(imageSpan.getSource(), null),
                    spanned.getSpanStart(imageSpan), spanned.getSpanEnd(imageSpan),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

//        if (callback != null) {
//            textView.setMovementMethod(LinkMovementMethod.getInstance());
//        }

        long end = System.currentTimeMillis();
        return trimTrailingWhitespace(style);
    }

    public static void parseHtmlAndSetText(String source, @NonNull TextView textView,
                                           Callback callback, int maxWidth) {
        long start = System.currentTimeMillis();
        if (TextUtils.isEmpty(source)) {
            return;
        }

        Spanned spanned = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(
                    source, Html.FROM_HTML_MODE_LEGACY, new GlideImageGetter(textView, maxWidth),
                    new CodeTagHandler());
        } else {
            spanned = Html.fromHtml(
                    source, new GlideImageGetter(textView, maxWidth),
                    new CodeTagHandler());
        }

        int color = textView.getResources().getColor(R.color.color_999999);
        int background = textView.getResources().getColor(R.color.color_f0f0f0);
        int width = textView.getResources().getDimensionPixelOffset(R.dimen.spacing_xs);
        replaceQuoteSpans((Spannable) spanned, background, color, width);
        URLSpan[] uslSpans = spanned.getSpans(0, spanned.length(), URLSpan.class);
        ImageSpan[] imageSpans = spanned.getSpans(0, spanned.length(), ImageSpan.class);
        SpannableStringBuilder style = new SpannableStringBuilder(spanned);

        for (URLSpan urlSpan : uslSpans) {
            style.setSpan(new ClickableURLSpan(urlSpan.getURL(), callback),
                    spanned.getSpanStart(urlSpan),
                    spanned.getSpanEnd(urlSpan),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.removeSpan(urlSpan);
        }

        for (ImageSpan imageSpan : imageSpans) {
            style.setSpan(new ClickableImageSpan(imageSpan.getSource(), callback),
                    spanned.getSpanStart(imageSpan), spanned.getSpanEnd(imageSpan),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (callback != null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        long end = System.currentTimeMillis();
        textView.setText(trimTrailingWhitespace(style));
    }

    public static void parseHtmlAndSetText(Context context, String source, @NonNull TextView textView, Callback callback) {
        if (TextUtils.isEmpty(source)) {
            return;
        }

        Spanned spanned;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(
                    source, Html.FROM_HTML_MODE_LEGACY, new GlideImageGetter1(context, textView),
                    new CodeTagHandler());
        } else {
            spanned = Html.fromHtml(
                    source, new GlideImageGetter1(context, textView),
                    new CodeTagHandler());
        }

        int color = textView.getResources().getColor(R.color.color_999999);
        int background = textView.getResources().getColor(R.color.color_f0f0f0);
        int width = textView.getResources().getDimensionPixelOffset(R.dimen.spacing_xs);
        replaceQuoteSpans((Spannable) spanned, background, color, width);
        URLSpan[] uslSpans = spanned.getSpans(0, spanned.length(), URLSpan.class);
        ImageSpan[] imageSpans = spanned.getSpans(0, spanned.length(), ImageSpan.class);
        SpannableStringBuilder style = new SpannableStringBuilder(spanned);

        for (URLSpan urlSpan : uslSpans) {
            style.setSpan(new ClickableURLSpan(urlSpan.getURL(), callback),
                    spanned.getSpanStart(urlSpan),
                    spanned.getSpanEnd(urlSpan),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.removeSpan(urlSpan);
        }

        for (ImageSpan imageSpan : imageSpans) {
            style.setSpan(new ClickableImageSpan(imageSpan.getSource(), callback),
                    spanned.getSpanStart(imageSpan), spanned.getSpanEnd(imageSpan),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (callback != null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        textView.setText(trimTrailingWhitespace(style));
    }

    private static void replaceQuoteSpans(Spannable spannable, int background, @ColorInt int color,
                                          int width) {
        QuoteSpan[] quoteSpans = spannable.getSpans(0, spannable.length(), QuoteSpan.class);
        for (QuoteSpan quoteSpan : quoteSpans) {
            int start = spannable.getSpanStart(quoteSpan);
            int end = spannable.getSpanEnd(quoteSpan);
            int flags = spannable.getSpanFlags(quoteSpan);
            spannable.removeSpan(quoteSpan);
            spannable.setSpan(
                    new CustomQuoteSpan(background, color, width, width), start, end, flags);
        }
    }

    public interface Callback {

        void clickUrl(String url);

        void clickImage(String source);
    }

    public static class CustomQuoteSpan implements LeadingMarginSpan, LineBackgroundSpan {

        private final int mBackgroundColor;
        private final int mStripeColor;
        private final float mStripeWidth;
        private final float mGap;

        public CustomQuoteSpan(int backgroundColor, int stripeColor, float stripeWidth, float gap) {
            this.mBackgroundColor = backgroundColor;
            this.mStripeColor = stripeColor;
            this.mStripeWidth = stripeWidth;
            this.mGap = gap;
        }

        @Override
        public int getLeadingMargin(boolean first) {
            return (int) (mStripeWidth + mGap);
        }

        @Override
        public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline,
                                      int bottom, CharSequence text,
                                      int start, int end, boolean first, Layout layout) {
            Paint.Style style = p.getStyle();
            int paintColor = p.getColor();
            p.setStyle(Paint.Style.FILL);
            p.setColor(mStripeColor);
            c.drawRect(x, top, x + dir * mStripeWidth, bottom, p);
            p.setStyle(style);
            p.setColor(paintColor);
        }

        @Override
        public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline,
                                   int bottom, CharSequence text, int start, int end, int lnum) {
            int paintColor = p.getColor();
            p.setColor(mBackgroundColor);
            c.drawRect(left, top, right, bottom, p);
            p.setColor(paintColor);
        }
    }

    @SuppressLint("ParcelCreator")
    private static class ClickableURLSpan extends URLSpan {

        private Callback mCallback;

        ClickableURLSpan(String url, Callback callback) {
            super(url);
            this.mCallback = callback;
        }

        @Override
        public void onClick(View widget) {
            if (mCallback != null) {
                mCallback.clickUrl(getURL());
            }
        }
    }

    private static class ClickableImageSpan extends ClickableSpan {

        private String mSource;
        private Callback mCallback;

        public ClickableImageSpan(String source, Callback callback) {
            this.mSource = source;
            this.mCallback = callback;
        }

        @Override
        public void onClick(View widget) {
            if (mCallback != null) {
                mCallback.clickImage(mSource);
            }
        }
    }
}
