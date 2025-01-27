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
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.R;
import com.cofe.solution.ui.adapter.LanguageAdapter;
import com.cofe.solution.ui.adapter.MuSharedDeviceAdapter;

public class MeSharingManagement extends AppCompatActivity {
    TextView tvTitleHeader;
    ImageView back_button;
    LinearLayout llFromSharedDevice;
    LinearLayout llMySharing;
    RelativeLayout rlMySharings;
    RelativeLayout rlFromSharedDevice;

    RecyclerView rvMySharings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_me_sharing_management);

        initUis();
    }

    private void initUis()
    {
        tvTitleHeader=findViewById(R.id.tvTitleHeader);
        back_button=findViewById(R.id.back_button);
        llFromSharedDevice=findViewById(R.id.llFromSharedDevice);
        llMySharing=findViewById(R.id.llMySharing);
        rlMySharings=findViewById(R.id.rlMySharings);
        rlFromSharedDevice=findViewById(R.id.rlFromSharedDevice);
        rvMySharings=findViewById(R.id.rvMySharings);

        rvMySharings.setLayoutManager(new LinearLayoutManager(this));
        rvMySharings.setAdapter(new MuSharedDeviceAdapter(MeSharingManagement.this));
        tvTitleHeader.setText("Sharing Management");

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rlFromSharedDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llFromSharedDevice.setVisibility(View.VISIBLE);
                llMySharing.setVisibility(View.GONE);
                rlFromSharedDevice.setBackgroundResource(R.drawable.shape_bg_round_white_5);
                rlMySharings.setBackground(null);
            }
        });

        rlMySharings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llFromSharedDevice.setVisibility(View.GONE);
                llMySharing.setVisibility(View.VISIBLE);
                rlFromSharedDevice.setBackground(null);
                rlMySharings.setBackgroundResource(R.drawable.shape_bg_round_white_5);

            }
        });
    }
}