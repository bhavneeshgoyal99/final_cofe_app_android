package com.cofe.solution.ui.device.add.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cofe.solution.ui.device.add.share.view.NewShareDevToOtherAccountActivity;
import com.cofe.solution.ui.device.push.view.DevPushActivity;
import com.google.gson.Gson;
import com.manager.db.XMDevInfo;

import com.cofe.solution.R;
import com.cofe.solution.ui.device.add.share.view.DevShareAccountListActivity;
import com.cofe.solution.ui.device.add.share.view.ShareDevToOtherAccountActivity;
import com.cofe.solution.ui.device.config.DeviceSetting;

public class ShareFirstScren extends AppCompatActivity {
    XMDevInfo xmDevInfo;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_deviec_frist_screen);
        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.device_setting));

        String personJson = getIntent().getStringExtra("dev");

        // Convert the JSON string back to a Person object
        if (personJson != null) {
            Gson gson = new Gson();
            xmDevInfo = gson.fromJson(personJson, XMDevInfo.class);
        }

        context = ShareFirstScren.this;

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Switch shareAllToggle = findViewById(R.id.share_all_toggle);
        TextView shareAllLabel = findViewById(R.id.share_all_label);

        // Permissions IDs
        LinearLayout ptzIcon = findViewById(R.id.ptz_ll); // Add ID in XML if missing
        LinearLayout intercomIcon = findViewById(R.id.share_ll); // Add ID in XML if missing
        LinearLayout localStorageIcon = findViewById(R.id.storage_ll); // Add ID in XML if missing
        LinearLayout changeDeviceConfigIcon = findViewById(R.id.config_ll); // Add ID in XML if missing
        LinearLayout pushIcon = findViewById(R.id.push_ll); // Add ID in XML if missing
        LinearLayout viewCloudVideoIcon = findViewById(R.id.cloud_ll); // Add ID in XML if missing

        // Sharing Methods
        LinearLayout shareQRCodeIcon = findViewById(R.id.qr_ll); // Add ID in XML if missing
        LinearLayout accountShareIcon = findViewById(R.id.account_ll); // Add ID in XML if missing

        TextView footerLink = findViewById(R.id.view_share_management);

        // Set OnClickListener for toggle
        shareAllToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String message = isChecked ? "Share All enabled" : "Share All disabled";
            Toast.makeText(ShareFirstScren.this, message, Toast.LENGTH_SHORT).show();
        });

        // Set OnClickListener for icons
        ptzIcon.setOnClickListener(v -> showToast("PTZ selected"));
        intercomIcon.setOnClickListener(v -> showToast("Intercom selected"));
        localStorageIcon.setOnClickListener(v -> showToast("Local Storage selected"));
        changeDeviceConfigIcon.setOnClickListener(v -> showToast("Change Device Config selected"));
        pushIcon.setOnClickListener(v -> showToast("Push selected"));
        viewCloudVideoIcon.setOnClickListener(v -> showToast("View Cloud Video selected"));

        // Set OnClickListener for sharing methods
        shareQRCodeIcon.setOnClickListener(v ->

                sharQR("Share QR Code selected")

        );
        accountShareIcon.setOnClickListener(v ->showToast("Account Share selected"));

        // Set OnClickListener for footer link
        footerLink.setOnClickListener(v -> showToast("View Share Management clicked"));
    }

    // Helper method to display a Toast message
    private void sharQR(String message) {
        Gson gson = new Gson();
        String personJson = gson.toJson(xmDevInfo);

        Intent intent = new Intent(this, ShareDevToOtherAccountActivity.class);
        intent.putExtra("dev", personJson);
        startActivity(intent);
    }

    void showToast(String text) {
        Gson gson = new Gson();
        String personJson = gson.toJson(xmDevInfo);

        Intent intent = new Intent(this, NewShareDevToOtherAccountActivity.class);
        intent.putExtra("dev", personJson);
        startActivity(intent);

    }
}