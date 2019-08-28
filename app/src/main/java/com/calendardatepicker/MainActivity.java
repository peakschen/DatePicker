package com.calendardatepicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.calendardatepicker.test.SelectTimeUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.select_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectTimeUtil.showTimeDialog(MainActivity.this, time -> {
                    Toast.makeText(MainActivity.this, "" + time, Toast.LENGTH_SHORT).show();
                });
            }
        });
        findViewById(R.id.select_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String str = TimeUtil.getMonthDefaultTime();
//                TimeSeparate str1 = TimeUtil.getTime("2019/07-2019/08");
                SelectTimeUtil.showTimeDialog(MainActivity.this, time -> {
                    Toast.makeText(MainActivity.this, "" + time, Toast.LENGTH_SHORT).show();
                }, true);
            }
        });
    }
}
