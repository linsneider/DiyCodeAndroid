package com.sneider.diycode.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.sneider.diycode.R;

public class MarkdownUtils {

    public static void addCode(@NonNull EditText editText, String category) {
        String source = editText.getText().toString();
        int selectionStart = editText.getSelectionStart();
        int selectionEnd = editText.getSelectionEnd();
        String substring = source.substring(selectionStart, selectionEnd);
        boolean newLine = hasNewLine(source, selectionStart);
        String result = (newLine ? "" : "\n")
                + String.format("```%s\n%s\n```\n", category, substring);
        editText.getText().replace(selectionStart, selectionEnd, result);
        editText.setSelection(selectionStart + result.length() - 5);
    }

    public static void addImage(@NonNull EditText editText, String desc, String url) {
        int selectionStart = editText.getSelectionStart();
        String result = String.format("![%s](%s)\n", desc, url);
        int length = selectionStart + result.length();
        editText.getText().insert(selectionStart, result);
        editText.setSelection(length);
    }

    public static void addLink(@NonNull EditText editText, String desc, String url) {
        int selectionStart = editText.getSelectionStart();
        String result = String.format("[%s](%s)\n", desc, url);
        int length = selectionStart + result.length();
        editText.getText().insert(selectionStart, result);
        editText.setSelection(length);
    }

    private static boolean hasNewLine(@NonNull String source, int selectionStart) {
        try {
            if (source.isEmpty()) {
                return true;
            }
            source = source.substring(0, selectionStart);
            return source.charAt(source.length() - 1) == 10;
        } catch (StringIndexOutOfBoundsException e) {
            return true;
        }
    }

    public static PopupMenu createCodePopupMenu(@NonNull Context context, @NonNull View anchor,
                                                @NonNull CategoryCallback callback) {
        PopupMenu popupMenu = new PopupMenu(context, anchor);
        popupMenu.setOnMenuItemClickListener(new CodePopupMenuClickListener(callback));
        popupMenu.inflate(R.menu.menu_popup_code_category);
        return popupMenu;
    }

    public interface CategoryCallback {

        void clickCategory(String codeCategory);
    }

    public static class CodePopupMenuClickListener implements PopupMenu.OnMenuItemClickListener {

        private final CategoryCallback mCallback;

        public CodePopupMenuClickListener(CategoryCallback callback) {
            mCallback = callback;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            String s;
            switch (item.getItemId()) {
                case R.id.action_ruby:
                    s = "ruby";
                    break;
                case R.id.action_html_erb:
                    s = "erb";
                    break;
                case R.id.action_css_scss:
                    s = "scss";
                    break;
                case R.id.action_javascript:
                    s = "js";
                    break;
                case R.id.action_yaml:
                    s = "yml";
                    break;
                case R.id.action_coffeescript:
                    s = "coffee";
                    break;
                case R.id.action_nginx_redis:
                    s = "conf";
                    break;
                case R.id.action_python:
                    s = "python";
                    break;
                case R.id.action_php:
                    s = "php";
                    break;
                case R.id.action_java:
                    s = "java";
                    break;
                case R.id.action_erlang:
                    s = "erlang";
                    break;
                case R.id.action_shell_bash:
                    s = "shell";
                    break;
                default:
                    s = "java";
                    break;
            }
            mCallback.clickCategory(s);
            return false;
        }
    }
}
