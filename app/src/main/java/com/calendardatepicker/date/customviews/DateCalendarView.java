package com.calendardatepicker.date.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.calendardatepicker.R;
import com.calendardatepicker.date.models.CalendarStyleAttr;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DateCalendarView extends LinearLayout {

    public interface CalendarListener {
        void onFirstDateSelected(Calendar startDate);

        void onDateRangeSelected(Calendar startDate, Calendar endDate);

        void onUnSelected();
    }

    private TextView tvYearTitle;
    private AppCompatImageView imgVNavLeft, imgVNavRight;
    private List<Calendar> monthDataList = new ArrayList<>();

    private AdapterMonths adapterMonths;
    private Locale locale;

    private ViewPager vpCalendar;
    private CalendarStyleAttr calendarStyleAttr;
    private CalendarListener mCalendarListener;
    private TextView cancel, confirm;


    private final static int TOTAL_ALLOWED_MONTHS = 30;

    public DateCalendarView(Context context) {
        super(context);
        initViews(context, null);
    }

    public DateCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    public DateCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {

        locale = context.getResources().getConfiguration().locale;
        calendarStyleAttr = new CalendarStyleAttr(context, attrs);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.layout_calendar_container, this, true);

        RelativeLayout rlHeaderCalendar = findViewById(R.id.rlHeaderCalendar);
        rlHeaderCalendar.setBackground(calendarStyleAttr.getHeaderBg());

        tvYearTitle = findViewById(R.id.tvYearTitle);
        tvYearTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, calendarStyleAttr.getTextSizeTitle());

        imgVNavLeft = findViewById(R.id.imgVNavLeft);
        imgVNavRight = findViewById(R.id.imgVNavRight);

        vpCalendar = findViewById(R.id.vpCalendar);


        monthDataList.clear();
        Calendar today = (Calendar) Calendar.getInstance().clone();
        today.add(Calendar.MONTH, -TOTAL_ALLOWED_MONTHS);

        for (int i = 0; i < TOTAL_ALLOWED_MONTHS * 2; i++) {
            monthDataList.add((Calendar) today.clone());
            today.add(Calendar.MONTH, 1);
        }

        adapterMonths = new AdapterMonths(context, monthDataList, calendarStyleAttr);
        vpCalendar.setAdapter(adapterMonths);
        vpCalendar.setOffscreenPageLimit(0);
        vpCalendar.setCurrentItem(TOTAL_ALLOWED_MONTHS);
        setCalendarYearTitle(TOTAL_ALLOWED_MONTHS);

        cancel = findViewById(R.id.cancel);
        confirm = findViewById(R.id.confirm);
        setListeners();
    }

    private void setListeners() {

        vpCalendar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setCalendarYearTitle(position);
                setNavigationHeader(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        imgVNavLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int newPosition = vpCalendar.getCurrentItem() - 1;
                if (newPosition > -1) {
                    vpCalendar.setCurrentItem(newPosition);
                }
            }
        });
        imgVNavRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int newPosition = vpCalendar.getCurrentItem() + 1;
                if (newPosition < monthDataList.size()) {
                    vpCalendar.setCurrentItem(newPosition);
                }
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarListener.onUnSelected();
            }
        });
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getStartDate() == null) {
                    Toast.makeText(getContext(), "please select start time！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (getEndDate() == null) {
                    Toast.makeText(getContext(), "please select end time！", Toast.LENGTH_SHORT).show();
                    return;
                }
                mCalendarListener.onDateRangeSelected(getStartDate(), getEndDate());
            }
        });
    }


    /**
     * To set navigation header ( Left-Right button )
     *
     * @param position New page position
     */
    private void setNavigationHeader(int position) {
        imgVNavRight.setVisibility(VISIBLE);
        imgVNavLeft.setVisibility(VISIBLE);
        if (monthDataList.size() == 1) {
            imgVNavLeft.setVisibility(INVISIBLE);
            imgVNavRight.setVisibility(INVISIBLE);
        } else if (position == 0) {
            imgVNavLeft.setVisibility(INVISIBLE);
        } else if (position == monthDataList.size() - 1) {
            imgVNavRight.setVisibility(INVISIBLE);
        }
    }

    /**
     * To set calendar year title
     *
     * @param position data list position for getting date
     */
    private void setCalendarYearTitle(int position) {

        Calendar currentCalendarMonth = monthDataList.get(position);
        String dateText = new DateFormatSymbols(locale).getMonths()[currentCalendarMonth.get(Calendar.MONTH)];
        dateText = dateText.substring(0, 1).toUpperCase() + dateText.subSequence(1, dateText.length());

        String yearTitle = dateText + " " + currentCalendarMonth.get(Calendar.YEAR);

        tvYearTitle.setText(yearTitle);
        tvYearTitle.setTextColor(calendarStyleAttr.getTitleColor());

    }

    private Calendar startDate, endDate;

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    private CalendarListener calendarListener = new CalendarListener() {
        @Override
        public void onFirstDateSelected(Calendar startDate) {
            setStartDate(startDate);
        }

        @Override
        public void onDateRangeSelected(Calendar startDate, Calendar endDate) {
            setStartDate(startDate);
            setEndDate(endDate);
        }

        @Override
        public void onUnSelected() {

        }
    };

    /**
     * To set calendar listener
     *
     * @param listener
     */
    public void setCalendarListener(final CalendarListener listener) {
        this.mCalendarListener = listener;
        adapterMonths.setCalendarListener(calendarListener);
    }

    /**
     * To apply custom fonts to all the text views
     *
     * @param fonts - Typeface that you want to apply
     */
    public void setFonts(Typeface fonts) {
        tvYearTitle.setTypeface(fonts);
        calendarStyleAttr.setFonts(fonts);
        adapterMonths.invalidateCalendar();
    }

    /**
     * To remove all selection and redraw current calendar
     */
    public void resetAllSelectedViews() {
        adapterMonths.resetAllSelectedViews();
    }

    /**
     * To set week offset. To start week from any of the day. Default is 0 (Sunday).
     *
     * @param offset 0-Sun, 1-Mon, 2-Tue, 3-Wed, 4-Thu, 5-Fri, 6-Sat
     */
    public void setWeekOffset(int offset) {
        calendarStyleAttr.setWeekOffset(offset);
        adapterMonths.invalidateCalendar();
    }

    /**
     * To set left navigation ImageView drawable
     */
    public void setNavLeftImage(@NonNull Drawable leftDrawable) {
        imgVNavLeft.setImageDrawable(leftDrawable);
    }

    /**
     * To set right navigation ImageView drawable
     */
    public void setNavRightImage(@NonNull Drawable rightDrawable) {
        imgVNavRight.setImageDrawable(rightDrawable);
    }

    /**
     * Sets start and end date.<br>
     * <B>Note:</B><br>
     * You can not set null start date with valid end date.<br>
     * You can not set end date before start date.<br>
     * If you are setting custom month range than do not call this before calling (@method setVisibleMonthRange).<br>
     *
     * @param startDate Start date
     * @param endDate   End date
     */
    public void setSelectedDateRange(@Nullable Calendar startDate, @Nullable Calendar endDate) {
        if (startDate == null && endDate != null) {
            throw new RuntimeException("Start date can not be null if you are setting end date.");
        } else if (endDate != null && endDate.before(startDate)) {
            throw new RuntimeException("Start date can not be after end date.");
        }
        adapterMonths.setSelectedDate(startDate, endDate);

        long t1 = startDate.getTimeInMillis();
        long t2 = endDate.getTimeInMillis();
        Log.i("test", "t1=====" + t1 + "||t2:" + t2 + "||count:" + (t2 - t1) / (24 * 60 * 60 * 1000));
    }

    /**
     * To get start date.
     */
    public Calendar getStartDate() {
        return adapterMonths.getMinSelectedDate();
    }


    /**
     * To get end date.
     */
    public Calendar getEndDate() {
        return adapterMonths.getMaxSelectedDate();
    }


    /**
     * To set editable mode. Default value will be true.
     *
     * @param isEditable true if you want user to select date range else false
     */
    public void setEditable(boolean isEditable) {
        adapterMonths.setEditable(isEditable);
    }

    /**
     * To get editable mode.
     */
    public boolean isEditable() {
        return adapterMonths.isEditable();
    }


    /**
     * To provide month range to be shown to user. If start month is greater than end month than it will give .<br>
     * <b>Note:</b> Do not call this method after calling date selection method (@method setSelectedDateRange) as it will reset date selection.
     *
     * @param startMonth Start month of the calendar
     * @param endMonth   End month of the calendar
     */
    public void setVisibleMonthRange(Calendar startMonth, Calendar endMonth) {

        if (startMonth == null) {
            throw new IllegalArgumentException("Start month can not be null.");
        }
        startMonth.set(Calendar.DATE, 1);
        startMonth.set(Calendar.HOUR, 0);
        startMonth.set(Calendar.MINUTE, 0);
        startMonth.set(Calendar.SECOND, 0);
        startMonth.set(Calendar.MILLISECOND, 0);


        if (endMonth == null) {
            throw new IllegalArgumentException("End month can not be null.");
        }
        endMonth.set(Calendar.DATE, 1);
        endMonth.set(Calendar.HOUR, 0);
        endMonth.set(Calendar.MINUTE, 0);
        endMonth.set(Calendar.SECOND, 0);
        endMonth.set(Calendar.MILLISECOND, 0);

        if (startMonth.after(endMonth)) {
            throw new IllegalArgumentException("Start month can not be greater than end month.");
        }
        monthDataList.clear();

        while (!isDateSame(startMonth, endMonth)) {
            monthDataList.add((Calendar) startMonth.clone());
            startMonth.add(Calendar.MONTH, 1);
        }
        monthDataList.add((Calendar) startMonth.clone());

        adapterMonths = new AdapterMonths(getContext(), monthDataList, calendarStyleAttr);
        vpCalendar.setAdapter(adapterMonths);
        vpCalendar.setOffscreenPageLimit(0);
        vpCalendar.setCurrentItem(0);
        setCalendarYearTitle(0);
        setNavigationHeader(0);
        adapterMonths.setCalendarListener(calendarListener);

    }

    /**
     * To set current visible month.
     *
     * @param calendar Month to be set as current
     */
    public void setCurrentMonth(Calendar calendar) {

        if (calendar != null && monthDataList != null) {
            for (int i = 0; i < monthDataList.size(); i++) {
                Calendar month = monthDataList.get(i);
                if (month.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {

                    if (month.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                        vpCalendar.setCurrentItem(i);
                        break;
                    }
                }
            }
        }
    }

    private boolean isDateSame(@NonNull Calendar one, @NonNull Calendar second) {
        return one.get(Calendar.YEAR) == second.get(Calendar.YEAR)
                && one.get(Calendar.MONTH) == second.get(Calendar.MONTH)
                && one.get(Calendar.DATE) == second.get(Calendar.DATE);
    }
}
