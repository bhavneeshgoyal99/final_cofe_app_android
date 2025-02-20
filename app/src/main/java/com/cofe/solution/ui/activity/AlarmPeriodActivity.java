package com.cofe.solution.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.R;
import com.cofe.solution.ui.adapter.CustomAlarmAdapter;

public class AlarmPeriodActivity extends AppCompatActivity {

    TextView tvTitleHeader;
    ImageView back_button;
    RelativeLayout rlCustomAlarm;
    RelativeLayout rlOtherAlarm;
    RecyclerView rvCustomAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarm_period);

        initUis();
    }

    private void initUis() {
        tvTitleHeader = findViewById(R.id.toolbar_title);
        back_button = findViewById(R.id.back_button);
        rlCustomAlarm = findViewById(R.id.rlCustomAlarm);
        rvCustomAlarm = findViewById(R.id.rvCustomAlarm);
        rlOtherAlarm = findViewById(R.id.rlOtherAlarm);

        rvCustomAlarm.setLayoutManager(new LinearLayoutManager(AlarmPeriodActivity.this));
        rvCustomAlarm.setAdapter(new CustomAlarmAdapter(AlarmPeriodActivity.this));


        tvTitleHeader.setText(getString(R.string.alarm_period));

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rlCustomAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvCustomAlarm.setVisibility(View.VISIBLE);
            }
        });
        rlOtherAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvCustomAlarm.setVisibility(View.GONE);
            }
        });


    }
}