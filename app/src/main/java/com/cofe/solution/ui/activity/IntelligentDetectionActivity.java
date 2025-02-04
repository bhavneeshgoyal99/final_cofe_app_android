package com.cofe.solution.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.cofe.solution.R;
import com.cofe.solution.ui.device.BulletCamActivity;

public class IntelligentDetectionActivity extends AppCompatActivity {
    TextView tvTitleHeader;
    ImageView back_button;
    RelativeLayout rlAlarmPeriod;
    RelativeLayout rlDomeCam;
    RelativeLayout rlBulletCam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intelligent_detection);

        initUis();
    }

    private void initUis() {
        tvTitleHeader = findViewById(R.id.toolbar_title);
        back_button = findViewById(R.id.back_button);
        rlAlarmPeriod = findViewById(R.id.rlAlarmPeriod);
        rlDomeCam = findViewById(R.id.rlDomeCam);
        rlBulletCam = findViewById(R.id.rlBulletCam);


        tvTitleHeader.setText("Smart Alarm");

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rlAlarmPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(IntelligentDetectionActivity.this, AlarmPeriodActivity.class);

                startActivity(i);
            }
        });
        rlDomeCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(IntelligentDetectionActivity.this, BulletCamActivity.class);
                i.putExtra("title", "Dome cam 01");
                startActivity(i);
            }
        });
        rlBulletCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(IntelligentDetectionActivity.this, BulletCamActivity.class);
                i.putExtra("title", "Bullet cam 01");
                startActivity(i);
            }
        });


    }
}