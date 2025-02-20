package com.cofe.solution.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cofe.solution.R;
import com.cofe.solution.ui.device.aov.view.AovSettingActivity;

public class SmartAlarmActivity extends AppCompatActivity {
    TextView tvTitleHeader;
    ImageView back_button;
    RelativeLayout rlIntellegentDetection;
    RelativeLayout rlAlarmLinkage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_smart_alarm);

        initUis();
    }

    private void initUis()
    {
        tvTitleHeader=findViewById(R.id.toolbar_title);
        back_button=findViewById(R.id.back_button);
        rlIntellegentDetection=findViewById(R.id.rlIntellegentDetection);
        rlAlarmLinkage=findViewById(R.id.rlAlarmLinkage);



        tvTitleHeader.setText(getString(R.string.smart_alarm));

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rlIntellegentDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(SmartAlarmActivity.this, IntelligentDetectionActivity.class);

                startActivity(i);
            }
        });
        rlAlarmLinkage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(SmartAlarmActivity.this, AlarmLinkageActivity.class);

                startActivity(i);
            }
        });





    }
}