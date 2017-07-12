package com.sneider.diycode.utils.html;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;

import org.xml.sax.XMLReader;

public class CodeTagHandler implements Html.TagHandler {

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (tag.equalsIgnoreCase("code")) {
            if (opening) {
                output.setSpan(new CodeTypefaceSpan(), output.length(), output.length(), Spannable.SPAN_MARK_MARK);
            } else {
                CodeTypefaceSpan obj = getLast(output, CodeTypefaceSpan.class);
                int where = output.getSpanStart(obj);
                output.setSpan(new CodeTypefaceSpan(), where, output.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                output.setSpan(new StyleSpan(Typeface.NORMAL), where, output.length(),
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private <T> T getLast(Editable text, Class<T> kind) {
        T[] objs = text.getSpans(0, text.length(), kind);
        if (objs.length == 0) {
            return null;
        } else {
            for (int i = objs.length; i > 0; i--) {
                if (text.getSpanFlags(objs[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return objs[i - 1];
                }
            }
            return null;
        }
    }

    class CodeTypefaceSpan extends TypefaceSpan {

        public CodeTypefaceSpan() {
            super("monospace");
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(Color.parseColor("#999999"));
        }
    }
}
