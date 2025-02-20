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
    private WifiManager wifiManager;
    private Button btnTurnOnWifi;
    private Button btnTurnOnBluetooth;
    LinearLayout wifiLl;
    LinearLayout g4Ll;
    LinearLayout viotLl;
    LinearLayout dvrLl;
    LinearLayout wiredLl;
    LinearLayout shareLL;
    Button btnShowBottomSheet;
    GridLayout gridlayout;
    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 1;


    private final BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        btnTurnOnWifi.setVisibility(View.GONE);
                        Toast.makeText(context, getString(R.string.wifi_turned_on), Toast.LENGTH_SHORT).show();
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        btnTurnOnWifi.setVisibility(View.VISIBLE);

                        Toast.makeText(context, getString(R.string.wifi_turned_off), Toast.LENGTH_SHORT).show();
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        btnTurnOnWifi.setVisibility(View.GONE);
                        Toast.makeText(context, getString(R.string.wifi_turning_on), Toast.LENGTH_SHORT).show();
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        btnTurnOnWifi.setVisibility(View.VISIBLE);
                        Toast.makeText(context, getString(R.string.wifi_turning_off), Toast.LENGTH_SHORT).show();
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        Toast.makeText(context, getString(R.string.wifi_state_unknown), Toast.LENGTH_SHORT).show();
                        btnTurnOnWifi.setVisibility(View.VISIBLE);
                        break;
                }
            }


            /*if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null) {
                    if (networkInfo.isConnected()) {
                        //Toast.makeText(context, "WiFi Connected", Toast.LENGTH_SHORT).show();
                        //btnTurnOnWifi.setVisibility(View.GONE);
                    } else if (networkInfo.getState() == NetworkInfo.State.CONNECTING) {
                        //btnTurnOnWifi.setVisibility(View.GONE);
                        //Toast.makeText(context, "WiFi Connecting...", Toast.LENGTH_SHORT).show();
                    } else if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
                        //btnTurnOnWifi.setVisibility(View.GONE);
                        //Toast.makeText(context, "WiFi Disconnected", Toast.LENGTH_SHORT).show();
                    } else if (networkInfo.getState() == NetworkInfo.State.DISCONNECTING) {
                        //btnTurnOnWifi.setVisibility(View.VISIBLE);
                        //Toast.makeText(context, "Please connect WiFi to a network", Toast.LENGTH_SHORT).show();
                    } else if (networkInfo.getState() == NetworkInfo.State.SUSPENDED) {
                        //btnTurnOnWifi.setVisibility(View.VISIBLE);
                        //Toast.makeText(context, "WiFi Connection Suspended", Toast.LENGTH_SHORT).show();
                    }
                }
            }*/

            if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                int errorCode = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                if (errorCode == WifiManager.ERROR_AUTHENTICATING) {
                    Toast.makeText(context, getString(R.string.wifi_authentication_error), Toast.LENGTH_SHORT).show();
                }
            }

            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    Toast.makeText(context, getString(R.string.wifi_internet_connected), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, getString(R.string.wifi_internet_not_available), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_add_new_device);
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

        gridlayout.post(new Runnable() {
            @Override
            public void run() {
                int uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK;

                if (uiMode == Configuration.UI_MODE_TYPE_TELEVISION) {
                    Log.d("UI_MODE_TYPE_MASK", "Device is a TV");
                    gridlayout.setColumnCount(3); // 3 columns for TV
                    //gridlayout.setRowCount(2);    // 6 items → 2 rows (3x2)
                } else {
                    Log.d("UI_MODE_TYPE_MASK", "Device is NOT a TV");

                    gridlayout.setColumnCount(2); // 2 columns for Phone
                   // gridlayout.setRowCount(2);    // 6 items → 3 rows (2x3)
                }
            }
        });

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        wifiLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddNewDeviceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddNewDeviceActivity.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, PERMISSIONS_REQUEST_CODE);
                } else {
                    Intent i = new Intent (AddNewDeviceActivity.this, WifiPowerOnCamer.class);
                    startActivity(i);
                }

            }
        });

        g4Ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent (AddNewDeviceActivity.this, com.cofe.solution.ui.device.add.four_g.WifiPowerOnCamer.class);
                startActivity(i);
            }
        });

        viotLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (AddNewDeviceActivity.this, PowerOnVIOT.class);
                startActivity(i);
            }
        });

        dvrLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (AddNewDeviceActivity.this, PowerOnDvrDevice.class);
                startActivity(i);

            }
        });

        wiredLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (AddNewDeviceActivity.this, DevLanConnectActivity.class);
                startActivity(i);

            }
        });

        shareLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (AddNewDeviceActivity.this, DevSnConnectActivity.class);
                startActivity(i);
            }
        });


        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // Check for necessary permissions

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        } else {
            //enableWifiTab();
            Toast.makeText(this, getString(R.string.wi_fi_is_already_on), Toast.LENGTH_SHORT).show();
            scanForNearbyDevices();
        }


        btnTurnOnWifi.setOnClickListener(v -> {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
                enableWifiTab();
                Toast.makeText(this, getString(R.string.wi_fi_turned_on), Toast.LENGTH_SHORT).show();
            } else {
                enableWifiTab();
                Toast.makeText(this, getString(R.string.wi_fi_is_already_on), Toast.LENGTH_SHORT).show();
            }
        });

        // Set OnClickListener for Turn on Bluetooth button
        btnTurnOnBluetooth.setOnClickListener(v -> {
            checkBluetoothPermission();
        });

        btnShowBottomSheet.setOnClickListener(v -> showBottomNavigationDrawer());


        /*IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter); */

	    IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);

        checkAndRequestPermissions();
        scanForNearbyDevices();
        checkBluetoothPermission();

    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, PERMISSIONS_REQUEST_CODE);
        }
    }

    private void scanForNearbyDevices() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (wifiManager.isWifiEnabled()) {
                wifiManager.startScan();
                List<ScanResult> results = wifiManager.getScanResults();
                if (!results.isEmpty()) {
                    for (ScanResult result : results) {
                        //result
                        //Toast.makeText(this, "Found device: " + result.SSID, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_devices_found_nearby), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.please_turn_on_wi_fi_first), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.location_permission_is_required_to_scan_for_devices), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
                enableWifiTab();

            } else {
                showAppSettingsPopup(" WIFI ");
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //startActivity(new Intent(AddNewDeviceActivity.this, DevBluetoothListActivity.class));
                Toast.makeText(this, getString(R.string.bluetooth_permission_granted), Toast.LENGTH_SHORT).show();
                btnTurnOnBluetooth.setVisibility(View.GONE);
                // Enable Bluetooth
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    bluetoothAdapter.enable();
                    // Enable Bluetooth
                    Toast.makeText(this, getString(R.string.bluetooth_enabled), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_CONNECT)) {
                    // User permanently denied permission
                    //showAppSettingsPopup();
                } else {
                    Toast.makeText(this, getString(R.string.bluetooth_permission_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void showBottomNavigationDrawer() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_navigation_drawer, null);

        LinearLayout btnOption1 = bottomSheetView.findViewById(R.id.ap_ll);
        LinearLayout btnOption2 = bottomSheetView.findViewById(R.id.ap_mode_ll);
        LinearLayout btnOption3 = bottomSheetView.findViewById(R.id.near_by_ll);

        btnOption1.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent i = new Intent(AddNewDeviceActivity.this, DevApConnectActivity.class);
                  startActivity(i);
              }
          });
        btnOption2.setOnClickListener(v -> Toast.makeText(this, getString(R.string.ap_mode), Toast.LENGTH_SHORT).show());
        btnOption3.setOnClickListener(v -> Toast.makeText(this, getString(R.string.near_by_camera), Toast.LENGTH_SHORT).show());

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }



    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android 12 and above
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_CONNECT)) {
                    // Show a dialog explaining why permission is needed
                    btnTurnOnBluetooth.setVisibility(View.VISIBLE);
                    showPermissionRationale();
                } else {
                    // Request Bluetooth permission
                    btnTurnOnBluetooth.setVisibility(View.VISIBLE);
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_PERMISSION_REQUEST_CODE);
                }
            } else {
                // Permission already granted
                btnTurnOnBluetooth.setVisibility(View.GONE);
                //startActivity(new Intent(AddNewDeviceActivity.this, DevBluetoothListActivity.class));
                Toast.makeText(this, getString(R.string.bluetooth_permission_granted), Toast.LENGTH_SHORT).show();
            }
        } else {
            // For Android versions below 12, no need to check for BLUETOOTH_CONNECT
            Toast.makeText(this, getString(R.string.bluetooth_permission_not_required_for_this_version), Toast.LENGTH_SHORT).show();
        }
    }

    private void showPermissionRationale() {
        // Show a custom dialog or Toast explaining why the permission is needed
        Toast.makeText(this, getString(R.string.bluetooth_permission_is_required_to_connect_to_devices), Toast.LENGTH_LONG).show();
        // Request permission again
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_PERMISSION_REQUEST_CODE);
    }

    private void showAppSettingsPopup(String permissionName) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(permissionName +" Permission Required")
                    .setMessage(getString(R.string.this_app_requires_location_permission_to_manage)  + permissionName+ getString(R.string.without_this_permission_the_app_cannot_suggest_or_connect_to) +permissionName +getString(R.string.networks))

                    .setCancelable(false)
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            settingsLauncher.launch(intent);
                        }
                    })
                    .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(AddNewDeviceActivity.this, getString(R.string.permission_rejected_wifi_features_won_t_work), Toast.LENGTH_SHORT).show();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
    }

    private final ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Check permission again after returning from settings
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.bluetooth_permission_granted), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.bluetooth_permission_is_still_not_granted), Toast.LENGTH_SHORT).show();
                }
            }
    );





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.option1) {
            Intent i =  new Intent(AddNewDeviceActivity.this, AddNewDeviceActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.option2) {
            Intent j =  new Intent(AddNewDeviceActivity.this, SetDevToRouterByQrCodeActivity.class);
            startActivity(j);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void enableWifiTab(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            Toast.makeText(this, "WiFi enabled.", Toast.LENGTH_SHORT).show();
            btnTurnOnWifi.setVisibility(View.GONE);
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "Please enable WiFi manually.", Toast.LENGTH_SHORT).show();

        }
    }

}