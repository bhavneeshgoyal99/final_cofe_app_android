package com.cofe.solution.ui.device.add.four_g;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cofe.solution.R;
import com.cofe.solution.ui.device.add.sn.view.DevSnConnectActivity;
import com.cofe.solution.ui.device.add.wifi.InputWiFiInfoActivityForQr;

public class CameraConfigInstruction extends AppCompatActivity {
    int CAMERA_PERMISSION_REQUEST_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_config_instruction);

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
                checkCameraPermission();

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                openCamera();
            } else {
                // Permission denied
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    // Show the explanation again
                    Toast.makeText(this, getString(R.string.permission_is_required_to_use_the_camera), Toast.LENGTH_SHORT).show();
                } else {
                    // Permission denied with "Do Not Ask Again"
                    showSettingsRedirectPopup();
                }
            }
        }
    }


    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            openCamera();
        } else {
            // Show permission explanation popup
            showPermissionExplanationPopup();
        }
    }

    private void showPermissionExplanationPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Camera Permission Required")
                .setMessage(getString(R.string.this_app_needs_camera_access_to_take_photos_please_grant_the_permission_to_proceed))
                .setCancelable(false)
                .setPositiveButton("Accept", (dialog, which) -> {
                    // Request camera permission
                    ActivityCompat.requestPermissions(CameraConfigInstruction.this,
                            new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                })
                .setNegativeButton("Reject", (dialog, which) -> {
                    dialog.dismiss();
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                });
        builder.create().show();
    }


    private void showSettingsRedirectPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.camera_permission_required))
                .setMessage(getString(R.string.camera_permission_is_permanently_denied_please_enable_it_in_the_app_settings))
                .setCancelable(false)
                .setPositiveButton("Open Settings", (dialog, which) -> openAppSettings())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void openCamera() {
        Toast.makeText(this, getString(R.string.camera_is_now_accessible), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(CameraConfigInstruction.this, DevSnConnectActivity.class);
        startActivity(i);
    }


    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


}