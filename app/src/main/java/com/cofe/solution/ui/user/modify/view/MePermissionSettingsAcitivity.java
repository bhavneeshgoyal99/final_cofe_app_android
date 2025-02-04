package com.cofe.solution.ui.user.modify.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.Window;
import android.widget.Toast;

import com.cofe.solution.R;
import com.cofe.solution.ui.device.add.list.view.DevListActivity;

public class MePermissionSettingsAcitivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 100;
    private String[] permissions = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? Manifest.permission.RECORD_AUDIO : null,
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? Manifest.permission.POST_NOTIFICATIONS : null
    };
    private ImageView back_button;

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
        back_button = findViewById(R.id.back_button);

        setClickListeners();
        for (String permission : permissions) {
            if (permission != null) {
                checkPermission(permission);
            }
        }
    }

    private void setClickListeners() {
        llBluetooth.setOnClickListener(v -> checkPermission(getBluetoothPermission(), "Bluetooth access is required to search for nearby devices."));
        llLocation.setOnClickListener(v -> checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, "Location access is needed for network configuration."));

        llStorage.setOnClickListener(v -> checkExternalStoragePermission());

        llCamera.setOnClickListener(v -> checkPermission(Manifest.permission.CAMERA, "Camera access is required to scan QR codes."));
        llMic.setOnClickListener(v -> checkPermission(Manifest.permission.RECORD_AUDIO, "Microphone access is required for voice intercom."));

        llNotification.setOnClickListener(v -> checkPermission(Manifest.permission.POST_NOTIFICATIONS, "Notification access is required to receive alerts."));
        back_button.setOnClickListener(v -> finish());
    }

    private String getBluetoothPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ?
                Manifest.permission.BLUETOOTH_CONNECT : Manifest.permission.BLUETOOTH;
    }

    private void checkExternalStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                showPermissionInfoDialogForStorage(new String[] {Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.READ_MEDIA_VIDEO, }, "Storage access is needed to save device images and videos.");
            } else{
                updateUI(Manifest.permission.READ_EXTERNAL_STORAGE, true);
            }
        } else {
            // Lower versions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                showPermissionInfoDialog(Manifest.permission.READ_EXTERNAL_STORAGE, "Storage access is needed to save device images and videos.");
            } else {
                updateUI(Manifest.permission.READ_EXTERNAL_STORAGE, true);
            }
        }
    }


    private void checkPermission(String permission, String message) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            updateUI(permission, true);
            Toast.makeText(getApplicationContext(), getString(R.string.permission_already_granted), Toast.LENGTH_SHORT).show();
        } else {
            showPermissionInfoDialog(permission, message);
        }
    }

    private void checkPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            updateUI(permission, true);
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
        Log.d("requestPermission > ", "permission > "  + permission);
        if(permission.contains("storage")) {
                ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO}, REQUEST_PERMISSION_CODE);
                requestPermissions(new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                }, REQUEST_PERMISSION_CODE);
            /* else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            }*/
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_PERMISSION_CODE);
        }
    }
    private void showPermissionInfoDialogForStorage(String[] permission, String message) {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage(message)
                .setPositiveButton("Accept", (dialog, which) -> requestPermissionForStorage(permission))
                .setNegativeButton("Deny", (dialog, which) -> updateUI(Manifest.permission.WRITE_EXTERNAL_STORAGE, false))
                .show();
    }

    private void requestPermissionForStorage(String[] permission) {
        Log.d("requestPermissionForStorage > ", "permission > "  + permission);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO}, REQUEST_PERMISSION_CODE);
                requestPermissions(new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                }, REQUEST_PERMISSION_CODE);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED && !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    openAppSettings();
                } else {
                    updateUI(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
                }
            }
        }
    }

    private void updateUI(String permission, boolean granted) {
        RelativeLayout targetLayout = null;

        if (permission.equals(Manifest.permission.BLUETOOTH) || permission.equals(Manifest.permission.BLUETOOTH_CONNECT))
            targetLayout = findViewById(R.id.ll_b);
        if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) targetLayout = findViewById(R.id.ll1_l);
        if (permission.equals(Manifest.permission.READ_MEDIA_VIDEO)) targetLayout = findViewById(R.id.ll2_s);
        if (permission.equals(Manifest.permission.READ_MEDIA_IMAGES)) targetLayout = findViewById(R.id.ll2_s);
        if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) targetLayout = findViewById(R.id.ll2_s);
        if (permission.equals(Manifest.permission.CAMERA)) targetLayout = findViewById(R.id.ll4_c);
        if (permission.equals(Manifest.permission.RECORD_AUDIO)) targetLayout = findViewById(R.id.ll3_m);
        if (permission.equals(Manifest.permission.POST_NOTIFICATIONS)) targetLayout = findViewById(R.id.ll5_n);

        if (targetLayout != null) {
            TextView statusView = targetLayout.findViewWithTag("status_text");
            if (statusView == null) {
                statusView = new TextView(this);
                statusView.setTag("status_text");
                targetLayout.addView(statusView);
            }
            statusView.setText(granted ? "On" : "Set");
            statusView.setTextColor(getResources().getColor(granted ? R.color.item_right_cap_text_color : R.color.red));
        }
    }

    private void openAppSettings() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Denied")
                .setMessage("This permission has been permanently denied. You need to enable it manually in settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}