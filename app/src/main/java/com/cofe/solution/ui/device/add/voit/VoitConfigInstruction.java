package com.cofe.solution.ui.device.add.voit;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cofe.solution.R;

public class VoitConfigInstruction extends AppCompatActivity {

    private RadioButton radioWired, radioWifi;
    private LinearLayout wiredConnectionLayout, wifiConnectionLayout;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voit_config_instruction);

        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.pwoer_on_camera));

        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Initialize views
        radioWired = findViewById(R.id.radioWired);
        radioWifi = findViewById(R.id.radioWifi);
        wiredConnectionLayout = findViewById(R.id.wiredConnectionLayout);
        wifiConnectionLayout = findViewById(R.id.wifiConnectionLayout);
        nextButton = findViewById(R.id.nextButton);

        // Radio Button behavior
        radioWired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioWired.setChecked(true);
                radioWifi.setChecked(false);
                highlightSelectedLayout(true);
            }
        });

        radioWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioWired.setChecked(false);
                radioWifi.setChecked(true);
                highlightSelectedLayout(false);
            }
        });

        // Next Button Behavior
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioWired.isChecked()) {
                    Toast.makeText(VoitConfigInstruction.this, "Wired Connection Selected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VoitConfigInstruction.this, "Wi-Fi Connection Selected", Toast.LENGTH_SHORT).show();
                }
                // Proceed to the next activity here
            }
        });


    }

    private void highlightSelectedLayout(boolean isWiredSelected) {
        if (isWiredSelected) {
            wiredConnectionLayout.setBackgroundResource(R.drawable.add_device_type_selected);
            wifiConnectionLayout.setBackgroundResource(R.drawable.add_device_type_unselected);
        } else {
            wiredConnectionLayout.setBackgroundResource(R.drawable.add_device_type_unselected);
            wifiConnectionLayout.setBackgroundResource(R.drawable.add_device_type_selected);
        }
    }
}