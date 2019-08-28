package com.calendardatepicker.date.models;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DayContainer {

    public RelativeLayout rootView;
    public TextView tvDate;
    public View strip;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

    public DayContainer(RelativeLayout rootView) {
        this.rootView = rootView;
        strip = rootView.getChildAt(0);
        tvDate = (TextView) rootView.getChildAt(1);
    }

    public static int GetContainerKey(Calendar cal) {

        String str = simpleDateFormat.format(cal.getTime());
        int key = Integer.valueOf(str);
        return key;
    }

}
