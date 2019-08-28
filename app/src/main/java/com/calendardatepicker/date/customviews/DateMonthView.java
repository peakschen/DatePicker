package com.calendardatepicker.date.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.calendardatepicker.R;
import com.calendardatepicker.date.models.CalendarStyleAttr;
import com.calendardatepicker.date.models.DayContainer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


class DateMonthView extends LinearLayout {

    private static final String LOG_TAG = DateMonthView.class.getSimpleName();
    private LinearLayout llDaysContainer;
    private LinearLayout llTitleWeekContainer;

    private Calendar currentCalendarMonth;

    private CalendarStyleAttr calendarStyleAttr;

    private DateCalendarView.CalendarListener calendarListener;

    private DateManager dateManager;

    private final static PorterDuff.Mode FILTER_MODE = PorterDuff.Mode.SRC_IN;

    public void setCalendarListener(DateCalendarView.CalendarListener calendarListener) {
        this.calendarListener = calendarListener;
    }

    public DateMonthView(Context context) {
        super(context);
        initView(context, null);
    }

    public DateMonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public DateMonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DateMonthView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    /**
     * To initialize child views
     *
     * @param context      - App context
     * @param attributeSet - Attr set
     */
    private void initView(Context context, AttributeSet attributeSet) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        LinearLayout mainView = (LinearLayout) layoutInflater.inflate(R.layout.layout_calendar_month, this, true);
        llDaysContainer = mainView.findViewById(R.id.llDaysContainer);
        llTitleWeekContainer = mainView.findViewById(R.id.llTitleWeekContainer);

        if (isInEditMode()) {
            return;
        }

    }

    private OnClickListener dayClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            if (calendarStyleAttr.isEditable()) {
                int key = (int) view.getTag();
                final Calendar selectedCal = Calendar.getInstance();
                Date date = new Date();
                try {
                    date = DateManager.SIMPLE_DATE_FORMAT.parse(String.valueOf(key));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                selectedCal.setTime(date);

                Calendar minSelectedDate = dateManager.getMinSelectedDate();
                Calendar maxSelectedDate = dateManager.getMaxSelectedDate();

                if (minSelectedDate != null && maxSelectedDate == null) {
                    maxSelectedDate = selectedCal;

                    long t1 = minSelectedDate.getTimeInMillis();
                    long t2 = maxSelectedDate.getTimeInMillis();
                    long count = (Math.abs(t2 - t1)) / (24 * 60 * 60 * 1000);
                    Log.i("test", "=====" + t1 + "||t2:" + t2 + "||count:" + count);
                    if (count >= 31) {
//                        Toast.makeText(getContext(), "请选择时间间隔在31天以内", Toast.LENGTH_SHORT).show();
                        dateManager.setMaxSelectedDate(null);
                        return;
                    }

                    int startDateKey = DayContainer.GetContainerKey(minSelectedDate);
                    int lastDateKey = DayContainer.GetContainerKey(maxSelectedDate);

                    if (startDateKey == lastDateKey) {
                        minSelectedDate = maxSelectedDate;
                    } else if (startDateKey > lastDateKey) {
                        Calendar temp = (Calendar) minSelectedDate.clone();
                        minSelectedDate = maxSelectedDate;
                        maxSelectedDate = temp;
                    }
                } else if (maxSelectedDate == null) {
                    //This will call one time only
                    minSelectedDate = selectedCal;
                } else {
                    minSelectedDate = selectedCal;
                    maxSelectedDate = null;
                }

                dateManager.setMinSelectedDate(minSelectedDate);
                if (maxSelectedDate != null) {
                    long t2 = maxSelectedDate.getTimeInMillis();
                    if (t2 - System.currentTimeMillis() > 0) {
//                        Toast.makeText(getContext(), "请选择今天及以前时间", Toast.LENGTH_SHORT).show();
                        dateManager.setMaxSelectedDate(null);
                        return;
                    }
                }
                if (minSelectedDate != null) {
                    long t1 = minSelectedDate.getTimeInMillis();
                    long currentTime = System.currentTimeMillis();
                    if (t1 - currentTime > 0) {
//                        Toast.makeText(getContext(), "请选择今天及以前时间", Toast.LENGTH_SHORT).show();
                        dateManager.setMinSelectedDate(null);
                        return;
                    }

//                    if((currentTime - t1)/(24*60*60*1000)>31){
//                        Toast.makeText(getContext(), "请在今天及以前31天以内选择时间", Toast.LENGTH_SHORT).show();
//                        dateRangeCalendarManager.setMinSelectedDate(null);
//                        return;
//                    }
                }
                dateManager.setMaxSelectedDate(maxSelectedDate);
                drawCalendarForMonth(currentCalendarMonth);

                Log.i(LOG_TAG, "Time: " + selectedCal.getTime().toString());
                if (maxSelectedDate != null) {
                    calendarListener.onDateRangeSelected(minSelectedDate, maxSelectedDate);
                } else {
                    calendarListener.onFirstDateSelected(minSelectedDate);
                }
            }
        }
    };

    /**
     * To draw calendar for the given month. Here calendar object should start from date of 1st.
     *
     * @param calendarStyleAttr Calendar style attributes
     * @param month             Month to be drawn
     * @param dateManager       Calendar data manager
     */
    public void drawCalendarForMonth(CalendarStyleAttr calendarStyleAttr, Calendar month, DateManager dateManager) {
        this.calendarStyleAttr = calendarStyleAttr;
        this.currentCalendarMonth = (Calendar) month.clone();
        this.dateManager = dateManager;
        setConfigs();
        setWeekTitleColor(calendarStyleAttr.getWeekColor());
        drawCalendarForMonth(currentCalendarMonth);
    }

    /**
     * To draw calendar for the given month. Here calendar object should start from date of 1st.
     *
     * @param month Calendar month
     */
    private void drawCalendarForMonth(Calendar month) {

        currentCalendarMonth = (Calendar) month.clone();
        currentCalendarMonth.set(Calendar.DATE, 1);
        currentCalendarMonth.set(Calendar.HOUR, 0);
        currentCalendarMonth.set(Calendar.MINUTE, 0);
        currentCalendarMonth.set(Calendar.SECOND, 0);

        String[] weekTitle = getContext().getResources().getStringArray(R.array.week_sun_sat);

        //To set week day title as per offset
        for (int i = 0; i < 7; i++) {

            TextView textView = (TextView) llTitleWeekContainer.getChildAt(i);

            String weekStr = weekTitle[(i + calendarStyleAttr.getWeekOffset()) % 7];
            textView.setText(weekStr);

        }

        int startDay = month.get(Calendar.DAY_OF_WEEK) - calendarStyleAttr.getWeekOffset();

        //To ratate week day according to offset
        if (startDay < 1) {
            startDay = startDay + 7;
        }

        month.add(Calendar.DATE, -startDay + 1);

        for (int i = 0; i < llDaysContainer.getChildCount(); i++) {
            LinearLayout weekRow = (LinearLayout) llDaysContainer.getChildAt(i);

            for (int j = 0; j < 7; j++) {
                RelativeLayout rlDayContainer = (RelativeLayout) weekRow.getChildAt(j);

                DayContainer container = new DayContainer(rlDayContainer);

                container.tvDate.setText(String.valueOf(month.get(Calendar.DATE)));
                if (calendarStyleAttr.getFonts() != null) {
                    container.tvDate.setTypeface(calendarStyleAttr.getFonts());
                }
                drawDayContainer(container, month);
                month.add(Calendar.DATE, 1);
            }
        }
    }

    /**
     * To draw specific date container according to past date, today, selected or from range.
     *
     * @param container - Date container
     * @param calendar  - Calendar obj of specific date of the month.
     */
    private void drawDayContainer(DayContainer container, Calendar calendar) {

        Calendar today = Calendar.getInstance();

        int date = calendar.get(Calendar.DATE);

        if (currentCalendarMonth.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) {
            hideDayContainer(container);
        } else if (today.after(calendar) && (today.get(Calendar.DAY_OF_YEAR) != calendar.get(Calendar.DAY_OF_YEAR))
                && !calendarStyleAttr.isEnabledPastDates()) {
            disableDayContainer(container);
            container.tvDate.setText(String.valueOf(date));
        } else {
            @DateManager.RANGE_TYPE
            int type = dateManager.checkDateRange(calendar);
            if (type == DateManager.RANGE_TYPE.START_DATE || type == DateManager.RANGE_TYPE.LAST_DATE) {
                makeAsSelectedDate(container, type, calendar);
            } else if (type == DateManager.RANGE_TYPE.MIDDLE_DATE) {
//                makeAsRangeDate(container);
                makeAsSelectedDate(container, type, calendar);
            } else {
                enabledDayContainer(container, calendar);
            }

            container.tvDate.setText(String.valueOf(date));
            container.tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, calendarStyleAttr.getTextSizeDate());
        }

        container.rootView.setTag(DayContainer.GetContainerKey(calendar));
    }

    /**
     * To hide date if date is from previous month.
     *
     * @param container - Container
     */
    private void hideDayContainer(DayContainer container) {
        container.tvDate.setText("");
        container.tvDate.setBackgroundColor(Color.TRANSPARENT);
        container.strip.setBackgroundColor(Color.TRANSPARENT);
        container.rootView.setBackgroundColor(Color.TRANSPARENT);
        container.rootView.setVisibility(INVISIBLE);
        container.rootView.setOnClickListener(null);
    }

    /**
     * To disable past date. Click listener will be removed.
     *
     * @param container - Container
     */
    private void disableDayContainer(DayContainer container) {
        container.tvDate.setBackgroundColor(Color.TRANSPARENT);
        container.strip.setBackgroundColor(Color.TRANSPARENT);
        container.rootView.setBackgroundColor(Color.TRANSPARENT);
        container.tvDate.setTextColor(calendarStyleAttr.getDisableDateColor());
        container.rootView.setVisibility(VISIBLE);
        container.rootView.setOnClickListener(null);
    }

    /**
     * To enable date by enabling click listeners.
     *
     * @param container - Container
     */
    private void enabledDayContainer(DayContainer container, Calendar calendar) {

        Calendar minDate = dateManager.getMinSelectedDate();
        Calendar maxDate = dateManager.getMaxSelectedDate();

        container.tvDate.setBackgroundColor(Color.TRANSPARENT);
        container.strip.setBackgroundColor(Color.TRANSPARENT);
        container.rootView.setBackgroundColor(Color.TRANSPARENT);
        long t1 = Calendar.getInstance().getTimeInMillis();
        long t2 = calendar.getTimeInMillis();
        if (t2 > t1) {
            container.tvDate.setTextColor(Color.parseColor("#BDBDBD"));
        } else {
            container.tvDate.setTextColor(Color.parseColor("#3b4144"));
            if (minDate != null) {
                long mint = getCurrentDayTimeInMills(minDate.getTimeInMillis());
                long temp = getCurrentDayTimeInMills(calendar.getTimeInMillis());
                long count = (Math.abs(temp - mint)) / (24 * 60 * 60 * 1000);
//                Log.i("test", "t1111=====" + mint + "||t2:" + temp + "||count:" + count);
                if (count >= 31) {
//                    dateRangeCalendarManager.setMaxSelectedDate(null);
                    container.tvDate.setTextColor(Color.parseColor("#BDBDBD"));
                } else {
                    container.tvDate.setTextColor(Color.parseColor("#3b4144"));
                }
            }
        }
//        container.tvDate.setTextColor(calendarStyleAttr.getDefaultDateColor());
        container.rootView.setVisibility(VISIBLE);
        container.rootView.setOnClickListener(dayClickListener);
    }

    /**
     * To draw date container as selected as end selection or middle selection.
     *
     * @param container - Container
     * @param stripType - Right end date, Left end date or middle
     */
    private void makeAsSelectedDate(DayContainer container, @DateManager.RANGE_TYPE int stripType, Calendar calendar) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) container.strip.getLayoutParams();

        Calendar minDate = dateManager.getMinSelectedDate();
        Calendar maxDate = dateManager.getMaxSelectedDate();

        if (stripType == DateManager.RANGE_TYPE.START_DATE && maxDate != null &&
                minDate.compareTo(maxDate) != 0) {
            Drawable mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.range_bg_left);
            mDrawable.setColorFilter(new PorterDuffColorFilter(calendarStyleAttr.getRangeStripColor(), FILTER_MODE));

            container.strip.setBackground(mDrawable);
            layoutParams.setMargins(20, 0, 20, 0);
        } else if (stripType == DateManager.RANGE_TYPE.LAST_DATE) {
            Drawable mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.range_bg_right);
            mDrawable.setColorFilter(new PorterDuffColorFilter(calendarStyleAttr.getRangeStripColor(), FILTER_MODE));
            container.strip.setBackground(mDrawable);
            layoutParams.setMargins(20, 0, 20, 0);
        } else if (stripType == DateManager.RANGE_TYPE.MIDDLE_DATE) {
            Drawable mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.range_bg_right);
            mDrawable.setColorFilter(new PorterDuffColorFilter(calendarStyleAttr.getRangeStripColor(), FILTER_MODE));
            container.strip.setBackground(mDrawable);
            layoutParams.setMargins(20, 0, 20, 0);
        } else {
            container.strip.setBackgroundColor(Color.TRANSPARENT);
            layoutParams.setMargins(0, 0, 20, 0);
        }
        container.strip.setLayoutParams(layoutParams);
        Drawable mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.green_circle);
        mDrawable.setColorFilter(new PorterDuffColorFilter(calendarStyleAttr.getSelectedDateCircleColor(), FILTER_MODE));
        container.tvDate.setBackground(mDrawable);
        long t1 = getCurrentDayTimeInMills(Calendar.getInstance().getTimeInMillis());
        long t2 = getCurrentDayTimeInMills(calendar.getTimeInMillis());
        if (t2 > t1) {//不可选择
            container.tvDate.setTextColor(Color.parseColor("#BDBDBD"));
        } else {
            //可选
            container.tvDate.setTextColor(Color.parseColor("#3b4144"));
            if (minDate != null) {
                long mint = minDate.getTimeInMillis();
                long temp = getCurrentDayTimeInMills(calendar.getTimeInMillis());
                long count = (Math.abs(temp - mint)) / (24 * 60 * 60 * 1000);
//                Log.i("test", "t1111=====" + mint + "||t2:" + temp + "||count:" + count);
                if (count >= 31) {
//                    dateRangeCalendarManager.setMaxSelectedDate(null);
                    container.tvDate.setTextColor(Color.parseColor("#BDBDBD"));
                } else {
                    container.tvDate.setTextColor(Color.parseColor("#3b4144"));
                }
            }
        }
        container.rootView.setBackgroundColor(Color.TRANSPARENT);
        container.rootView.setVisibility(VISIBLE);
        container.rootView.setOnClickListener(dayClickListener);
    }

    /**
     * To set week title color
     *
     * @param color - resource color value
     */
    public void setWeekTitleColor(@ColorInt int color) {
        for (int i = 0; i < llTitleWeekContainer.getChildCount(); i++) {
            TextView textView = (TextView) llTitleWeekContainer.getChildAt(i);
            textView.setTextColor(color);
        }
    }

    /**
     * To apply configs to all the text views
     */
    private void setConfigs() {

        drawCalendarForMonth(currentCalendarMonth);

        for (int i = 0; i < llTitleWeekContainer.getChildCount(); i++) {

            TextView textView = (TextView) llTitleWeekContainer.getChildAt(i);
            textView.setTypeface(calendarStyleAttr.getFonts());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, calendarStyleAttr.getTextSizeWeek());
        }
    }

    /**
     * Modify to select date start time 00:00:00
     */
    private long getCurrentDayTimeInMills(long time) {
        long t = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String str = format.format(new Date(time));
        try {
            t = format.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return t;
    }
}
