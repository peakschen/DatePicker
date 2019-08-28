package com.calendardatepicker.month;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.calendardatepicker.R;

import java.util.ArrayList;
import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder> {

    private OnMonthClickListener listener = null;

    private Context mContext;
    private List<DateItem> mList = new ArrayList<>();

    public void setOnMonthClickListener(OnMonthClickListener onMonthClickListener) {
        this.listener = onMonthClickListener;
    }

    public MonthAdapter(Context context, List<DateItem> datas) {
        this.mContext = context;
        this.mList = datas;
    }

    @Override
    public MonthAdapter.MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MonthViewHolder holder = new MonthViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.item_select_month, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MonthViewHolder holder, final int position) {
        if (null != mList && mList.size() > 0) {
            if (null != mList.get(position)) {
                DateItem item = mList.get(position);
                holder.tv.setText(item.getName());
                holder.tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != listener)
                            listener.onItemSelect(position);
                    }
                });
                holder.tv.setSelected(item.isSelected());
                holder.tv.setTextColor(Color.parseColor(item.isCanSelect() ? "#3b4144" : "#BDBDBD"));
            }

        }

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public interface OnMonthClickListener {
        void onItemSelect(int position);
    }

    class MonthViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public MonthViewHolder(View view) {
            super(view);
            tv = view.findViewById(R.id.select_month_name);
        }
    }
}
