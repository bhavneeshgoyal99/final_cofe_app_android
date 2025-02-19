package com.cofe.solution.ui.user.info.view;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.R;
import com.cofe.solution.ui.adapter.LanguageAdapter;

public class BasicSettingsActivity extends AppCompatActivity {

    private TextView tvTitleHeader;
    RelativeLayout rlDayNightSwitchOverSens;
    RelativeLayout rlSpeakerVolume;
    RelativeLayout rlLanguage;
    RecyclerView rvLanguage;
    LinearLayout llDayNightSwitchesSensitivity;
    LinearLayout llBasicSettings;
    LinearLayout llSpeakerVolume;
    LinearLayout llLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_basic_settings);
        // setContentView(R.layout.activity_device_settings_2);

        initUis();
    }

    private void initUis() {
        tvTitleHeader = findViewById(R.id.toolbar_title);
        rlDayNightSwitchOverSens = findViewById(R.id.rlDayNightSwitchOverSens);
        llDayNightSwitchesSensitivity = findViewById(R.id.llDayNightSwitchesSensitivity);
        llBasicSettings = findViewById(R.id.llBasicSettings);
        rlSpeakerVolume = findViewById(R.id.rlSpeakerVolume);
        llSpeakerVolume = findViewById(R.id.llSpeakerVolume);
        rlLanguage = findViewById(R.id.rlLanguage);
        rvLanguage = findViewById(R.id.rvLanguage);
        llLanguage = findViewById(R.id.llLanguage);
        //tvTitleHeader.setText("Basic Settings");


        rvLanguage.setLayoutManager(new LinearLayoutManager(this));
        rvLanguage.setAdapter(new LanguageAdapter(BasicSettingsActivity.this));

        rlDayNightSwitchOverSens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llDayNightSwitchesSensitivity.setVisibility(View.VISIBLE);
                llBasicSettings.setVisibility(View.GONE);
                llSpeakerVolume.setVisibility(View.GONE);
                llLanguage.setVisibility(View.GONE);

            }
        });

        rlSpeakerVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llDayNightSwitchesSensitivity.setVisibility(View.GONE);
                llBasicSettings.setVisibility(View.GONE);
                llSpeakerVolume.setVisibility(View.VISIBLE);
                llLanguage.setVisibility(View.GONE);

            }
        });

        rlLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llDayNightSwitchesSensitivity.setVisibility(View.GONE);
                llBasicSettings.setVisibility(View.GONE);
                llSpeakerVolume.setVisibility(View.GONE);
                llLanguage.setVisibility(View.VISIBLE);
            }
        });
    }
}