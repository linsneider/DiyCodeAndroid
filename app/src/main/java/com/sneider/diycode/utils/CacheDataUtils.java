package com.sneider.diycode.utils;


import android.content.Context;
import android.text.format.Formatter;

import java.io.File;

public class CacheDataUtils {

    public static String getTotalCacheSize(Context context) {
        long cacheSize = getFolderSize(context.getCacheDir()) + getFolderSize(context.getExternalCacheDir());
        return Formatter.formatFileSize(context, cacheSize);
    }

    public static long getFolderSize(File file) {
        long size = 0;
        File[] fileList = file.listFiles();
        for (File aFileList : fileList) {
            // 如果下面还有文件
            if (aFileList.isDirectory()) {
                size = size + getFolderSize(aFileList);
            } else {
                size = size + aFileList.length();
            }
        }
        return size;
    }

    public static boolean clearAllCache(Context context) {
        return deleteDir(context.getCacheDir()) & deleteDir(context.getExternalCacheDir());
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
