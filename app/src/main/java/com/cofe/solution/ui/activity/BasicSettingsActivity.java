package com.cofe.solution.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cofe.solution.R;

public class BasicSettingsActivity extends AppCompatActivity {

    private TextView tvTitleHeader;
    RelativeLayout rlDayNightSwitchOverSens;
    RelativeLayout rlSpeakerVolume;
    RelativeLayout rlLanguage;
    LinearLayout llDayNightSwitchesSensitivity;
    LinearLayout llBasicSettings;
    LinearLayout llSpeakerVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_basic_settings);
        // setContentView(R.layout.activity_device_settings_2);

        initUis();
    }

    private void initUis() {
        tvTitleHeader = findViewById(R.id.tvTitleHeader);
        rlDayNightSwitchOverSens = findViewById(R.id.rlDayNightSwitchOverSens);
        llDayNightSwitchesSensitivity = findViewById(R.id.llDayNightSwitchesSensitivity);
        llBasicSettings = findViewById(R.id.llBasicSettings);
        rlSpeakerVolume = findViewById(R.id.rlSpeakerVolume);
        llSpeakerVolume = findViewById(R.id.llSpeakerVolume);
        rlLanguage = findViewById(R.id.rlLanguage);
        tvTitleHeader.setText("Basic Settings");

        rlDayNightSwitchOverSens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llDayNightSwitchesSensitivity.setVisibility(View.VISIBLE);
                llBasicSettings.setVisibility(View.GONE);
                llSpeakerVolume.setVisibility(View.GONE);
            }
        });
        rlSpeakerVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llDayNightSwitchesSensitivity.setVisibility(View.GONE);
                llBasicSettings.setVisibility(View.GONE);
                llSpeakerVolume.setVisibility(View.VISIBLE);
            }
        });

        rlLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llDayNightSwitchesSensitivity.setVisibility(View.GONE);
                llBasicSettings.setVisibility(View.GONE);
                llSpeakerVolume.setVisibility(View.GONE);
            }
        });
    }
}