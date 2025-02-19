package com.cofe.solution.ui.device.add.dvr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cofe.solution.R;
import com.cofe.solution.ui.device.add.wifi.CameraConfigInstruction;
import com.cofe.solution.ui.device.add.wifi.WifiPowerOnCamer;

public class DvrNetworkConfig extends AppCompatActivity {

    private ImageView backArrow;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voit_network_config);
        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.camera_network_config));

        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(DvrNetworkConfig.this, CameraConfigInstruction.class);
                startActivity(i);*/
            }
        });


    }
}