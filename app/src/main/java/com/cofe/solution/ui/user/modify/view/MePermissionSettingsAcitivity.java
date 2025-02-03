package com.cofe.solution.ui.user.modify.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cofe.solution.R;

public class MePermissionSettingsAcitivity extends AppCompatActivity {

    TextView tvTitleHeader;
    ImageView back_button;

    private static final int REQUEST_PERMISSION_CODE = 100;
    private String[] permissions = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS
    };

    private RelativeLayout llBluetooth, llLocation, llStorage, llCamera, llMic, llNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_me_permission_settings_acitivity);
        llBluetooth = findViewById(R.id.ll_b);
        llLocation = findViewById(R.id.ll1_l);
        llStorage = findViewById(R.id.ll2_s);
        llMic = findViewById(R.id.ll3_m);
        llCamera = findViewById(R.id.ll4_c);
                llNotification = findViewById(R.id.ll5_n);

        setClickListeners();
    }

    private void setClickListeners() {
        llBluetooth.setOnClickListener(v -> checkPermission(Manifest.permission.BLUETOOTH, "Bluetooth access is required to search for nearby devices."));
        llLocation.setOnClickListener(v -> checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, "Location access is needed for network configuration."));
        llStorage.setOnClickListener(v -> checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, "Storage access is needed to save device images and videos."));
        llCamera.setOnClickListener(v -> checkPermission(Manifest.permission.CAMERA, "Camera access is required to scan QR codes."));
        llMic.setOnClickListener(v -> checkPermission(Manifest.permission.RECORD_AUDIO, "Microphone access is required for voice intercom."));
        llNotification.setOnClickListener(v -> checkPermission(Manifest.permission.POST_NOTIFICATIONS, "Notification access is required to receive alerts."));
    }

    private void checkPermission(String permission, String message) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            updateUI(permission, true);
        } else {
            showPermissionInfoDialog(permission, message);
        }
    }

    private void showPermissionInfoDialog(String permission, String message) {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage(message)
                .setPositiveButton("Accept", (dialog, which) -> requestPermission(permission))
                .setNegativeButton("Deny", (dialog, which) -> updateUI(permission, false))
                .show();
    }

    private void requestPermission(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                updateUI(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
            }
        }
    }

    private void updateUI(String permission, boolean granted) {
        LinearLayout targetLayout = null;

        if (permission.equals(Manifest.permission.BLUETOOTH)) targetLayout = findViewById(R.id.ll);
        if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) targetLayout = findViewById(R.id.ll1);
        if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) targetLayout = findViewById(R.id.ll2);
        if (permission.equals(Manifest.permission.CAMERA)) targetLayout = findViewById(R.id.ll4);
        if (permission.equals(Manifest.permission.RECORD_AUDIO)) targetLayout = findViewById(R.id.ll3);
        if (permission.equals(Manifest.permission.POST_NOTIFICATIONS)) targetLayout = findViewById(R.id.ll5);

        if (targetLayout != null) {
            TextView statusView = targetLayout.findViewWithTag("status_text");
            if (statusView != null) {
                statusView.setText(granted ? "On" : "Set");
                statusView.setTextColor(getResources().getColor(granted ? R.color.item_right_cap_text_color : R.color.red));
            }
        }
    }
}
