package com.sneider.diycode.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ToastUtils;
import com.jess.arms.base.App;
import com.sneider.diycode.R;
import com.sneider.diycode.mvp.model.bean.Node;
import com.sneider.diycode.mvp.model.bean.Section;
import com.sneider.diycode.mvp.model.bean.Token;
import com.sneider.diycode.mvp.model.bean.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sneider.diycode.app.ARouterPaths.PUBLIC_WEB;
import static com.sneider.diycode.app.ARouterPaths.USER_LOGIN;
import static com.sneider.diycode.mvp.ui.activity.WebActivity.EXTRA_URL;

public class DiycodeUtils {

    static {
        System.loadLibrary("security");
    }

    public static native String getClientId();

    public static native String getClientSecret();

    public static void openWebActivity(String url) {
        ARouter.getInstance().build(PUBLIC_WEB)
                .withString(EXTRA_URL, url)
                .navigation();
    }

    public static boolean checkToken(Context context) {
        if (getToken(context) != null) {
            return true;
        } else {
            ToastUtils.showShort(R.string.please_login);
            ARouter.getInstance().build(USER_LOGIN).navigation();
            return false;
        }
    }

    public static Token getToken(Context context) {
        return (Token) ((App) context.getApplicationContext()).getAppComponent().extras().get("token");
    }

    public static void setToken(Context context, Token token) {
        ((App) context.getApplicationContext()).getAppComponent().extras().put("token", token);
    }

    public static User getUser(Context context) {
        return (User) ((App) context.getApplicationContext()).getAppComponent().extras().get("user");
    }

    public static void setUser(Context context, User user) {
        ((App) context.getApplicationContext()).getAppComponent().extras().put("user", user);
    }

    public static void shareText(Context context, String title, String content) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        shareIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_to)));
    }

    public static void shareImage(Context context, String url) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_to)));
    }

    public static void openBrowser(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    public static void selectImage(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.select_photo)), requestCode);
    }

    public static List<Section> processNode(List<Node> data) {
        HashMap<Integer, Section> sections = new HashMap<>();
        for (Node node : data) {
            int sectionId = node.getSection_id();
            Section section;
            if (!sections.containsKey(sectionId)) {
                section = new Section(sectionId, node.getSection_name());
                sections.put(sectionId, section);
            } else {
                section = sections.get(sectionId);
            }
            section.getNodes().add(node);
        }
        List<Section> list = new ArrayList<>();
        for (HashMap.Entry<Integer, Section> entry : sections.entrySet()) {
            list.add(entry.getValue());
        }
        return list;
    }

    public static String cacheImageFromContentResolver(Context context, Uri uri) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            String path = context.getExternalCacheDir().getPath()
                    + File.separator
                    + System.currentTimeMillis() + ".png";
            File bkFile = new File(path);
            if (!bkFile.exists()) {
                bkFile.createNewFile();
                FileOutputStream out = new FileOutputStream(bkFile);
                byte[] b = new byte[1024 * 4];// 5KB
                int len;
                while ((len = is.read(b)) != -1) {
                    out.write(b, 0, len);
                }
                out.flush();
                is.close();
                out.close();
                return (bkFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String removeP(String html) {
        String result = html;
        if (result.contains("<p>") && result.contains("</p>")) {
            result = result.replace("<p>", "");
            result = result.replace("</p>", "<br>");
            if (result.endsWith("<br>")) {
                result = result.substring(0, result.length() - 4);
            }
        }
        return result;
    }
}
