package com.cofe.solution.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cofe.solution.R;

public class AlarmLinkageActivity extends AppCompatActivity {
    TextView tvTitleHeader;
    ImageView back_button;
    Switch switchAlarmLinkage;
    LinearLayout llAlarmLinkageOn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarm_linkage);

        initUis();
    }

    private void initUis()
    {
        tvTitleHeader=findViewById(R.id.tvTitleHeader);
        back_button=findViewById(R.id.back_button);
        switchAlarmLinkage=findViewById(R.id.switchAlarmLinkage);
        llAlarmLinkageOn=findViewById(R.id.llAlarmLinkageOn);

        llAlarmLinkageOn.setVisibility(View.GONE);


        switchAlarmLinkage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchAlarmLinkage.isChecked())
                {
                    llAlarmLinkageOn.setVisibility(View.VISIBLE);
                }else {
                    llAlarmLinkageOn.setVisibility(View.GONE);

                }
            }
        });




        tvTitleHeader.setText("Alarm Linkage");

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });







    }
}