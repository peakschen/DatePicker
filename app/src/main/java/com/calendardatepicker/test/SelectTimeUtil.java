package com.calendardatepicker.test;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by peaks on 2019-08-01
 * Description:select time dialog
 */
public class SelectTimeUtil {

    public static void showTimeDialog(Activity activity, SelectTimeListener listener) {

        DateTimeDialog.show(activity, new SelectDateTimeListener() {
            @Override
            public void result(String startTime, String endTime, DateTimeDialog dateTimeDialog) {
                if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)) {
                    startTime = startTime.replace("-", "/");
                    endTime = endTime.replace("-", "/");
                    String time = startTime + "-" + endTime;
                    listener.result(time);
                    if (dateTimeDialog != null && dateTimeDialog.isShowing()) {
                        dateTimeDialog.dismiss();
                    }
                    Log.i("test", "time=====" + time);
                }
            }
        }, false);
    }

    public static void showTimeDialog(Activity activity, SelectTimeListener listener, boolean isShowMonth) {
        DateTimeDialog.show(activity, new SelectDateTimeListener() {
            @Override
            public void result(String startTime, String endTime, DateTimeDialog dateTimeDialog) {
                if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)) {
                    startTime = startTime.replace("-", "/");
                    endTime = endTime.replace("-", "/");
                    String time = startTime + "-" + endTime;
                    listener.result(time);
                    if (dateTimeDialog != null && dateTimeDialog.isShowing()) {
                        dateTimeDialog.dismiss();
                    }
                }
            }
        }, isShowMonth);
    }


    public interface SelectTimeListener {
        void result(String time);
    }

    public interface SelectDateTimeListener {
        void result(String startTime, String endTime, DateTimeDialog dateTimeDialog);
    }
}
