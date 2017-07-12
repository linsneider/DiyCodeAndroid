package com.sneider.diycode.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static long stringToLong(String timeString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.CHINA);
        Date date = formatter.parse(timeString);
        return date.getTime();
    }

    public static String getIntervalTime(String timeString) {
        String result = "";
        try {
            long time = stringToLong(timeString);
            long currentTime = System.currentTimeMillis();
            long interval = (currentTime - time) / 1000;
            if (interval < 60) {
                result = "刚刚";
            } else if ((interval /= 60) < 60) {
                result = interval + "分钟前";
            } else if ((interval /= 60) < 24) {
                result = interval + "小时前";
            } else if ((interval /= 24) < 30) {
                result = interval + "天前";
            } else if ((interval /= 30) < 12) {
                result = interval + "月前";
            } else {
                interval /= 12;
                result = interval + "年前";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
