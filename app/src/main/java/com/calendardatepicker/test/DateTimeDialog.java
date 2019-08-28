package com.calendardatepicker.test;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.calendardatepicker.R;
import com.calendardatepicker.date.customviews.DateCalendarView;
import com.calendardatepicker.month.MonthPicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeDialog extends Dialog {

    private static DateTimeDialog timeDialog;
    private SelectTimeUtil.SelectDateTimeListener listener;
    private boolean isShowMonth;

    public DateTimeDialog(Context context, SelectTimeUtil.SelectDateTimeListener listener, boolean isShowMonth) {
        super(context, R.style.MyDialog);
        this.listener = listener;
        this.isShowMonth = isShowMonth;
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_datetime);
        //select month
        MonthPicker monthPicker = findViewById(R.id.monthPicker);
        monthPicker.setOnMonthSelectEventListener(new MonthPicker.OnMonthSelectEventListener() {
            @Override
            public void onMonthSelected(long startTime, long endTime) {
//                Toast.makeText(context, year + "年" + month + "月", Toast.LENGTH_SHORT).show();
                listener.result(TimeUtil.ms2DateOnlyMonth(startTime), TimeUtil.ms2DateOnlyMonth(endTime), timeDialog);
                dismiss();
            }

            @Override
            public void onCancel() {
                dismiss();
            }
        });
        //set dateline
        monthPicker.setDateLine(2010);

        //select date
        DateCalendarView calendar = findViewById(R.id.calendar);
        if (isShowMonth) {
            monthPicker.setVisibility(View.VISIBLE);
            calendar.setVisibility(View.GONE);
        } else {
            monthPicker.setVisibility(View.GONE);
            calendar.setVisibility(View.VISIBLE);
        }

        calendar.setCalendarListener(new DateCalendarView.CalendarListener() {
            @Override
            public void onFirstDateSelected(Calendar startDate) {
//                Toast.makeText(context, "Start Date: " + startDate.getTime().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDateRangeSelected(Calendar startDate, Calendar endDate) {
//                Toast.makeText(context, "Start Date: " + startDate.getTime().toString() + " End date: " + endDate.getTime().toString(), Toast.LENGTH_SHORT).show();
                listener.result(TimeUtil.ms2DateOnlyDay(startDate.getTimeInMillis()), TimeUtil.ms2DateOnlyDay(endDate.getTimeInMillis()), timeDialog);
                dismiss();
            }

            @Override
            public void onUnSelected() {
                dismiss();
            }
        });

        Calendar date = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        int days = TimeUtil.getDaysOfMonth(date.getTime());
        String str = sdf.format(date.getTime());
        Log.i("test", str + "========days=" + days);

        Calendar now = Calendar.getInstance();
        Calendar later = (Calendar) now.clone();
        //set 24 months
        if (days == 31 && str.equals("31")) {
            now.add(Calendar.MONTH, -23);
            later.add(Calendar.MONTH, 0);
        } else {
            now.add(Calendar.MONTH, -24);
            later.add(Calendar.MONTH, 0);
        }

        calendar.setVisibleMonthRange(now, later);

        Calendar startSelectionDate = Calendar.getInstance();
        startSelectionDate.add(Calendar.DATE, 0);
        Calendar endSelectionDate = (Calendar) startSelectionDate.clone();
        endSelectionDate.add(Calendar.DATE, 0);

        Calendar current = Calendar.getInstance();
        calendar.setCurrentMonth(current);
    }

    public static DateTimeDialog show(Context context, SelectTimeUtil.SelectDateTimeListener listener, boolean isShowMonth) {
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return null;
            }
        }
        if (timeDialog != null && timeDialog.isShowing()) {
            return timeDialog;
        }
        timeDialog = new DateTimeDialog(context, listener, isShowMonth);
        //set dialog width and height
        WindowManager.LayoutParams attrs = timeDialog.getWindow().getAttributes();
        attrs.width = dp2px(context, 380);
        attrs.height = dp2px(context,530);
        timeDialog.getWindow().setAttributes(attrs);
        timeDialog.show();
        return timeDialog;
    }

    public static void dismiss(Context context) {
        try {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing()) {
                    timeDialog = null;
                    return;
                }
            }
            if (timeDialog != null && timeDialog.isShowing()) {
                Context loadContext = timeDialog.getContext();
                if (loadContext instanceof Activity) {
                    if (((Activity) loadContext).isFinishing()) {
                        timeDialog = null;
                        return;
                    }
                }
                timeDialog.dismiss();
                timeDialog = null;
            }
        } catch (Exception e) {
        } finally {
            timeDialog = null;
        }
    }

    private static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}