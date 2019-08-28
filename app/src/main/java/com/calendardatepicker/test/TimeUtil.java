package com.calendardatepicker.test;

import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by peaks on 2019-08-01
 * Description:
 */
public class TimeUtil {
    /**
     * default 30 days
     */
    public static String getDefaultTime() {
        long currentTime = System.currentTimeMillis();

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String startTime = format.format(currentTime - 30 * 24 * 60 * 60 * 1000L);
        String endTime = format.format(currentTime);
        return startTime + "-" + endTime;
    }

    /**
     * default 7 days
     */
    public static String getDefaultTime1() {
        long currentTime = System.currentTimeMillis();

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String startTime = format.format(currentTime - 7 * 24 * 60 * 60 * 1000L);
        String endTime = format.format(currentTime);
        return startTime + "-" + endTime;
    }

    /**
     * for yesterday
     */
    public static String getDayDefaultTime() {
        long currentTime = System.currentTimeMillis();

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String startTime = format.format(currentTime - 24 * 60 * 60 * 1000L);
        return startTime + "-" + startTime;
    }

    /**
     * default current month
     */
    public static String getMonthDefaultTime() {
        long currentTime = System.currentTimeMillis();

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM");
        String startTime = format.format(currentTime);
        return startTime + "-" + startTime;
    }

    /**
     * millis to date
     *
     * @param time millis
     */
    public static String ms2Date(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * millis to date
     *
     * @param time millis
     */
    public static String ms2DateOnlyDay(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date);
    }

    /**
     * date to millis
     *
     * @param time date
     */
    public static long Date2ms(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            Date date = format.parse(time);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * millis to month
     *
     * @param time
     */
    public static String ms2DateOnlyMonth(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return format.format(date);
    }

    /**
     * for 2016/06/07-2017/09/11 and 2016/07-2017/08
     *
     * @param time
     */
    public static TimeSeparate getTime(String time) {
        TimeSeparate timeSeparate = new TimeSeparate();
        if (TextUtils.isEmpty(time) || !time.contains("-")) {
            return timeSeparate;
        }
        String[] times = time.split("-");
        if (times.length != 2) {
            return timeSeparate;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/ddHH:mm:ss");
        try {
            long startTime = 0l;
            long endTime = 0l;
            if (times[0].split("/").length == 2) {//月份
                startTime = dateFormat.parse(times[0] + "/01" + "00:00:00").getTime();
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM");
                int days = getDaysOfMonth(new Date(format.parse(times[1]).getTime()));
                endTime = dateFormat.parse(times[1] + "/" + days + "23:59:59").getTime();
            } else {
                startTime = dateFormat.parse(times[0] + "00:00:00").getTime();
                endTime = dateFormat.parse(times[1] + "23:59:59").getTime();
            }
            timeSeparate.setStartTime(startTime + "");
            timeSeparate.setEndTime(endTime + "");
            Log.i("cgf", "starttime=====" + startTime + "======" + endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeSeparate;
    }

    /**
     * example for 2016/06/07-2017/09/11 style
     *
     * @param time
     */
    public static String changeMonthToDay(String time) {
        TimeSeparate timeSeparate = new TimeSeparate();
        if (TextUtils.isEmpty(time) || !time.contains("-")) {
            return time;
        }
        String[] times = time.split("-");
        if (times.length != 2) {
            return time;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/ddHH:mm:ss");
        try {
            long startTime = dateFormat.parse(times[0] + "00:00:00").getTime();
            timeSeparate.setStartTime(startTime + "");
            long endTime = dateFormat.parse(times[1] + "23:59:59").getTime();
            if (endTime - startTime > 31 * 24 * 60 * 60 * 1000l) {
                startTime = endTime - 31 * 24 * 60 * 60 * 1000l + 1l;
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            times[0] = format.format(new Date(startTime));
            return times[0] + "-" + times[1];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * days in month
     */
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}
