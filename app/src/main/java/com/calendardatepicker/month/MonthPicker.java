package com.calendardatepicker.month;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.calendardatepicker.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * select month
 */

public class MonthPicker extends LinearLayout implements View.OnClickListener {

    private RecyclerView recyclerView;
    private TextView tvYear;
    private ImageView sub;
    private ImageView add;
    private Calendar calendar;
    private List<DateItem> mList = new ArrayList<>();

    private OnMonthSelectEventListener listener = null;
    private MonthAdapter adapter;
    //change years
    private int cYear;
    private String startTime;
    private String endTime;
    private int selectMonthid;
    //current year
    private int currentYear;
    //current month
    private int currentMonth;
    private TextView cancel;
    private TextView confirm;
    private int dateLine;

    public void setDateLine(int dateLine) {
        this.dateLine = dateLine;
    }

    public MonthPicker(Context context) {
        super(context);
    }

    public MonthPicker(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_month_picker, this);
        recyclerView = findViewById(R.id.recyclerView);
        tvYear = findViewById(R.id.tv_year);
        sub = findViewById(R.id.iv_year_sub);
        add = findViewById(R.id.iv_year_add);
        cancel = findViewById(R.id.cancel);
        confirm = findViewById(R.id.confirm);
        sub.setOnClickListener(this);
        add.setOnClickListener(this);
        final GridLayoutManager layoutManage = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(layoutManage);
        calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        cYear = calendar.get(Calendar.YEAR);
        tvYear.setText(currentYear + "");
        add.setVisibility(GONE);
        currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel();
            }
        });
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(startTime)) {
                    Toast.makeText(context, "please select start month！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(endTime)) {
                    Toast.makeText(context, "please select end month！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (null != listener && !TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM");
                    try {
                        long start = format.parse(startTime).getTime();
                        long end = format.parse(endTime).getTime();
                        int days = getDaysOfMonth(new Date(end));
                        Log.i("test", "======days====" + days);
                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
                        long end1 = format1.parse(endTime + "/" + days).getTime();
                        if (end1 > System.currentTimeMillis()) {
                            end1 = System.currentTimeMillis();
                        }
                        listener.onMonthSelected(start, end1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        adapter = new MonthAdapter(context, getAllMonth());
        recyclerView.setAdapter(adapter);
        adapter.setOnMonthClickListener(new MonthAdapter.OnMonthClickListener() {
            @Override
            public void onItemSelect(int position) {
                String month = mList.get(position).getName();
                selectMonthid = mList.get(position).getId();

                if (tvYear.getText().toString().trim().equals(currentYear + "")) {
                    if (selectMonthid > currentMonth) {
//                        Toast.makeText(getContext(), "请选择当月及以前月份", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Log.i("test", "selectMonthid====" + selectMonthid + "=====||====month==" + currentMonth);
                if (endTime != null && startTime != null) {
                    endTime = null;
                    startTime = null;
                }
                flushSelectStatus(true);
            }
        });
    }

    private static int getDaysOfMonth(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * flush select
     */
    private void flushSelectStatus(boolean isSelectMonth) {
        //unselect
        if (selectMonthid == 0) {
            getAllMonth();
            adapter.notifyDataSetChanged();
            return;
        }
        String year = tvYear.getText().toString();
        if (isSelectMonth) {
            String selectTime = year + "/" + selectMonthid;
            if (TextUtils.isEmpty(startTime)) {
                startTime = selectTime;
            } else {
                endTime = selectTime;
            }
        }

        for (int i = 0; i < mList.size(); i++) {
            if (isSelectMonth) {
                if ((selectMonthid - 1) == i) {
                    mList.get(i).setSelected(true);
                } else {
                    mList.get(i).setSelected(false);
                }
                setSelect(i);
            } else {
                if (!TextUtils.isEmpty(endTime) && !TextUtils.isEmpty(startTime)) {
                    setSelect(i);
                } else {
                    if (startTime.equals(year + "/" + (i + 1))) {
                        mList.get(i).setSelected(true);
                    } else {
                        mList.get(i).setSelected(false);
                    }
                }
            }
            //if can select
            if (year.equals(currentYear + "")) {
                if (i + 1 > currentMonth) {
                    mList.get(i).setCanSelect(false);
                } else {
                    mList.get(i).setCanSelect(true);
                }
            } else {
                mList.get(i).setCanSelect(true);
            }
        }
        if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            if (!recyclerView.isComputingLayout()) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * set select
     */
    private void setSelect(int i) {
        if (!TextUtils.isEmpty(endTime) && !TextUtils.isEmpty(startTime)) {
            changeTime();
            String st[] = startTime.split("/");
            String et[] = endTime.split("/");
            if (cYear >= Integer.parseInt(st[0]) && cYear <= Integer.parseInt(et[0])) {
                if (cYear == Integer.parseInt(st[0]) && cYear == Integer.parseInt(et[0])) {
                    if (Integer.parseInt(st[1]) <= i + 1 && Integer.parseInt(et[1]) >= i + 1) {
                        mList.get(i).setSelected(true);
                    } else {
                        mList.get(i).setSelected(false);
                    }
                } else if (cYear == Integer.parseInt(st[0]) && cYear < Integer.parseInt(et[0])) {
                    if (Integer.parseInt(st[1]) <= i + 1) {
                        mList.get(i).setSelected(true);
                    } else {
                        mList.get(i).setSelected(false);
                    }
                } else if (cYear > Integer.parseInt(st[0]) && cYear == Integer.parseInt(et[0])) {
                    if (Integer.parseInt(et[1]) >= i + 1) {
                        mList.get(i).setSelected(true);
                    } else {
                        mList.get(i).setSelected(false);
                    }
                } else {
                    mList.get(i).setSelected(true);
                }
            } else {
                mList.get(i).setSelected(false);
            }
        }
    }

    /**
     * compare time
     */
    private void changeTime() {
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM");
        try {
            long start = format.parse(startTime).getTime();
            long end = format.parse(endTime).getTime();
            if (start > end) {
                String tempTime = startTime;
                startTime = endTime;
                endTime = tempTime;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public MonthPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MonthPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private List<DateItem> getAllMonth() {
        mList.clear();

        for (int i = 1; i <= 12; i++) {
            DateItem dateItem = new DateItem();
            dateItem.setId(i);
            dateItem.setName(i + "月");

            if (tvYear.getText().toString().equals(currentYear + "")) {
                if (i > currentMonth) {
                    dateItem.setCanSelect(false);
                } else {
                    dateItem.setCanSelect(true);
                }
            } else {
                dateItem.setCanSelect(true);
            }
            mList.add(dateItem);
        }
        return mList;
    }

    public void setOnMonthSelectEventListener(OnMonthSelectEventListener onMonthSelectEventListener) {
        this.listener = onMonthSelectEventListener;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_year_add) {
            addYear();
        } else if (i == R.id.iv_year_sub) {
            subYear();
        }
    }

    /**
     * add year
     */
    public void addYear() {
        calendar.add(Calendar.YEAR, 1);
        cYear = calendar.get(Calendar.YEAR);
        tvYear.setText(String.valueOf(cYear));
        flushSelectStatus(false);
        if (cYear >= currentYear) {
            add.setVisibility(GONE);
        }
        sub.setVisibility(VISIBLE);
    }

    /**
     * sub year
     */
    public void subYear() {
        calendar.add(Calendar.YEAR, -1);
        cYear = calendar.get(Calendar.YEAR);
        tvYear.setText(String.valueOf(cYear));
        flushSelectStatus(false);

        if (cYear <= dateLine) {
            sub.setVisibility(GONE);
        }
        add.setVisibility(VISIBLE);
    }


    public interface OnMonthSelectEventListener {
        void onMonthSelected(long startTime, long endTime);

        void onCancel();
    }

}
