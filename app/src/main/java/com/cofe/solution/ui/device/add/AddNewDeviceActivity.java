package com.cofe.solution.ui.device.add;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import com.cofe.solution.R;
import com.cofe.solution.ui.device.add.ap.view.DevApConnectActivity;
import com.cofe.solution.ui.device.add.bluetooth.DevBluetoothListActivity;
import com.cofe.solution.ui.device.add.dvr.PowerOnDvrDevice;
import com.cofe.solution.ui.device.add.lan.view.DevLanConnectActivity;
import com.cofe.solution.ui.device.add.list.view.DevListActivity;
import com.cofe.solution.ui.device.add.qrcode.view.SetDevToRouterByQrCodeActivity;
import com.cofe.solution.ui.device.add.share.view.DevShareManageActivity;
import com.cofe.solution.ui.device.add.sn.view.DevSnConnectActivity;
import com.cofe.solution.ui.device.add.voit.PowerOnVIOT;
import com.cofe.solution.ui.device.add.wifi.WifiPowerOnCamer;
public class AddNewDeviceActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 123;
    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 1;

    private WifiManager wifiManager;
    private BluetoothAdapter bluetoothAdapter;

    private Button btnTurnOnWifi;
    private Button btnTurnOnBluetooth;

    private LinearLayout wifiLl, g4Ll, viotLl, dvrLl, wiredLl, shareLL;
    private Button btnShowBottomSheet;
    private GridLayout gridlayout;

    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                updateBluetoothButton(state);
            }
        }
    };

    private final BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                updateWifiButton(wifiState);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_new_device);

        // Initialize UI elements
        initializeViews();

        // Check and request permissions
        checkAndRequestPermissions();

        // Setup UI actions
        setupEventListeners();

        // Register WiFi State Receiver
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);

        // Check & update WiFi and Bluetooth button visibility
        updateWifiButton(wifiManager.getWifiState());
        updateBluetoothButton(bluetoothAdapter.getState());
    }

    private void initializeViews() {
        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.add_new_dev));

        btnTurnOnWifi = findViewById(R.id.btn_turn_on_wifi);
        btnTurnOnBluetooth = findViewById(R.id.btn_turn_on_bluetooth);
        wifiLl = findViewById(R.id.wifi_ll);
        g4Ll = findViewById(R.id.g4_ll);
        viotLl = findViewById(R.id.viot_ll);
        dvrLl = findViewById(R.id.dvr_ll);
        wiredLl = findViewById(R.id.wired_ll);
        shareLL = findViewById(R.id.share_ll);
        btnShowBottomSheet = findViewById(R.id.btn_show_bottom_sheet);
        gridlayout = findViewById(R.id.gridLay);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void setupEventListeners() {
        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        btnTurnOnWifi.setOnClickListener(v -> toggleWiFi());
        btnTurnOnBluetooth.setOnClickListener(v -> checkBluetoothPermission());

        wifiLl.setOnClickListener(v -> checkPermissionAndProceed(WifiPowerOnCamer.class));
        g4Ll.setOnClickListener(v -> startActivity(new Intent(this, com.cofe.solution.ui.device.add.four_g.WifiPowerOnCamer.class)));
        viotLl.setOnClickListener(v -> startActivity(new Intent(this, PowerOnVIOT.class)));
        dvrLl.setOnClickListener(v -> startActivity(new Intent(this, PowerOnDvrDevice.class)));
        wiredLl.setOnClickListener(v -> startActivity(new Intent(this, DevLanConnectActivity.class)));
        shareLL.setOnClickListener(v -> startActivity(new Intent(this, DevSnConnectActivity.class)));

        btnShowBottomSheet.setOnClickListener(v -> showBottomNavigationDrawer());

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter);

    }

    private void checkAndRequestPermissions() {
        List<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), PERMISSIONS_REQUEST_CODE);
        }
    }

    private void checkPermissionAndProceed(Class<?> activityClass) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
        } else {
            startActivity(new Intent(this, activityClass));
        }
    }

    private void toggleWiFi() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {  // Below Android 10
            wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
            updateWifiButton(wifiManager.getWifiState());
        } else {
            // Direct enabling is restricted on Android 10+, open Wi-Fi settings instead
            Intent panelIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(panelIntent);
            Toast.makeText(this, "Please enable Wi-Fi manually.", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateWifiButton(int wifiState) {
        if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
            btnTurnOnWifi.setVisibility(View.GONE);
        } else {
            btnTurnOnWifi.setVisibility(View.VISIBLE);
        }
    }

    private void updateBluetoothButton(int state) {
        if (state == BluetoothAdapter.STATE_ON) {
            btnTurnOnBluetooth.setVisibility(View.GONE);
        } else if (state == BluetoothAdapter.STATE_OFF) {
            btnTurnOnBluetooth.setVisibility(View.VISIBLE);
        }
    }


    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_PERMISSION_REQUEST_CODE);
            } else {
                enableBluetooth();
            }
        } else {
            enableBluetooth();
        }
    }

    private void enableBluetooth() {
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_PERMISSION_REQUEST_CODE);
                    return;
                }
            }
            // Open Bluetooth settings if direct enabling is not allowed
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_PERMISSION_REQUEST_CODE);
        }
    }


    private void showBottomNavigationDrawer() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_navigation_drawer, null);

        bottomSheetView.findViewById(R.id.ap_ll).setOnClickListener(v -> startActivity(new Intent(this, DevApConnectActivity.class)));
        bottomSheetView.findViewById(R.id.ap_mode_ll).setOnClickListener(v -> Toast.makeText(this, "AP mode", Toast.LENGTH_SHORT).show());
        bottomSheetView.findViewById(R.id.near_by_ll).setOnClickListener(v -> Toast.makeText(this, "Nearby Camera", Toast.LENGTH_SHORT).show());

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                showAppSettingsPopup("Wi-Fi");
            }
        } else if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableBluetooth();
            } else {
                showAppSettingsPopup("Bluetooth");
            }
        }
    }

    private void showAppSettingsPopup(String permissionName) {
        new AlertDialog.Builder(this)
                .setTitle(permissionName + " Permission Required")
                .setMessage("This app requires " + permissionName + " permission to function properly.")
                .setPositiveButton("Accept", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                    startActivity(intent);
                })
                .setNegativeButton("Reject", (dialog, which) -> Toast.makeText(this, permissionName + " features won't work.", Toast.LENGTH_SHORT).show())
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiStateReceiver);
        unregisterReceiver(bluetoothStateReceiver);
    }
}
