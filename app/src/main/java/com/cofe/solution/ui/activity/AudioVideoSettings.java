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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cofe.solution.R;
import com.cofe.solution.ui.adapter.SharedUserAdapter;

public class AudioVideoSettings extends AppCompatActivity {
    TextView tvTitleHeader;
    ImageView back_button;
    RelativeLayout rlSpeakerVolume;
    LinearLayout llSpeakerVolume;
    LinearLayout llAudiVideoSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_audio_video_settings);


        initUis();
    }

    private void initUis()
    {
        tvTitleHeader=findViewById(R.id.tvTitleHeader);
        back_button=findViewById(R.id.back_button);
        rlSpeakerVolume=findViewById(R.id.rlSpeakerVolume);
        llSpeakerVolume=findViewById(R.id.llSpeakerVolume);
        llAudiVideoSettings=findViewById(R.id.llAudiVideoSettings);


        tvTitleHeader.setText("Audio & Video Settings");




        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rlSpeakerVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llSpeakerVolume.setVisibility(View.VISIBLE);
                llAudiVideoSettings.setVisibility(View.GONE);
            }
        });


    }

}