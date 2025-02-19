package com.cofe.solution.ui.device.add.bluetooth;

import static com.constant.SDKLogConstant.APP_BLE;
import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.lib.sdk.bean.bluetooth.XMBleHead.CMD_CALLBACK;
import static com.lib.sdk.bean.bluetooth.XMBleHead.CMD_RECEIVE;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.device.add.ap.view.DevApConnectActivity;
import com.cofe.solution.ui.device.add.bluetooth.adapter.DevBluetoothListAdapter;
import com.cofe.solution.ui.device.add.bluetooth.listener.IDevBlueToothView;
import com.cofe.solution.ui.device.add.bluetooth.presenter.DevBluetoothConnectPresenter;
import com.cofe.solution.ui.device.add.dvr.PowerOnDvrDevice;
import com.cofe.solution.ui.device.add.lan.view.DevLanConnectActivity;
import com.cofe.solution.ui.device.add.sn.view.DevSnConnectActivity;
import com.cofe.solution.ui.device.add.voit.PowerOnVIOT;
import com.cofe.solution.ui.device.add.wifi.InputWiFiInfoActivity;
import com.cofe.solution.ui.device.add.wifi.WifiPowerOnCamer;
import com.cofe.solution.utils.SPUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.lib.SDKCONST;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.bluetooth.XMBleData;
import com.lib.sdk.bean.bluetooth.XMBleInfo;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.manager.bluetooth.XMBleManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SecondAddNewDeviceActivity extends DemoBaseActivity<DevBluetoothConnectPresenter> implements IDevBlueToothView {

    String ssid;
    String password;

    private static final int PERMISSIONS_REQUEST_CODE = 123;
    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_INPUT_WIFI_INFO_CODE = 0x01;
    private static final int REQUEST_LOCATION_SOURCE_SETTINGS = 0x02;

    private WifiManager wifiManager;
    private BluetoothAdapter bluetoothAdapter;

    private Button btnTurnOnWifi, btnTurnOnBluetooth, btnShowBottomSheet;
    private Button startBleNetwork, stopBleNetwork, searchBleDevs;
    private CheckBox cbOnlyNearDev;
    private RecyclerView rvDevBluetoothList;
    private DevBluetoothListAdapter devBluetoothListAdapter;
    private GridLayout gridlayout;

    private LinearLayout wifiLl, g4Ll, viotLl, dvrLl, wiredLl, shareLL;

    private boolean isStopBlePairing = false;
    private boolean isMassCtrl = false;
    private HashMap<String, Boolean> isBleNetworkSuccess = new HashMap<>();

    private DevBluetoothConnectPresenter presenter;

    private final BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                updateWifiButton(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
            }
        }
    };

    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                updateBluetoothButton(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR));
            }
        }
    };

    @Override
    public DevBluetoothConnectPresenter getPresenter() {
        return new DevBluetoothConnectPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_new_device);
        presenter = new DevBluetoothConnectPresenter(this);

        initializeViews();
        checkAndRequestPermissions();
        setupEventListeners();

        registerReceiver(wifiStateReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        updateWifiButton(wifiManager.getWifiState());
        updateBluetoothButton(bluetoothAdapter.getState());


    }

    private void updateWifiButton(int wifiState) {
        btnTurnOnWifi.setVisibility(wifiState == WifiManager.WIFI_STATE_ENABLED ? View.GONE : View.VISIBLE);
    }

    private void updateBluetoothButton(int state) {
        btnTurnOnBluetooth.setVisibility(state == BluetoothAdapter.STATE_ON ? View.GONE : View.VISIBLE);
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
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION})
     void initializeViews() {
        btnTurnOnWifi = findViewById(R.id.btn_turn_on_wifi);
        btnTurnOnBluetooth = findViewById(R.id.btn_turn_on_bluetooth);
        btnShowBottomSheet = findViewById(R.id.btn_show_bottom_sheet);

        startBleNetwork = findViewById(R.id.btn_start_ble_network);
        stopBleNetwork = findViewById(R.id.btn_stop_ble_network);
        searchBleDevs = findViewById(R.id.btn_search_ble_dev);
        cbOnlyNearDev = findViewById(R.id.cb_only_near_dev);

        rvDevBluetoothList = findViewById(R.id.rv_dev_bluetooth_list);
        rvDevBluetoothList.setLayoutManager(new LinearLayoutManager(this));

        gridlayout = findViewById(R.id.gridLay);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        presenter = new DevBluetoothConnectPresenter(this);
        devBluetoothListAdapter = new DevBluetoothListAdapter(this);
        rvDevBluetoothList.setAdapter(devBluetoothListAdapter);

        wifiLl = findViewById(R.id.wifi_ll);
        g4Ll = findViewById(R.id.g4_ll);
        viotLl = findViewById(R.id.viot_ll);
        dvrLl = findViewById(R.id.dvr_ll);
        wiredLl = findViewById(R.id.wired_ll);
        shareLL = findViewById(R.id.share_ll);
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
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_PERMISSION_REQUEST_CODE);
        }
    }


    private void toggleWiFi() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
            updateWifiButton(wifiManager.getWifiState());
        } else {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            Toast.makeText(this, "Please enable Wi-Fi manually.", Toast.LENGTH_SHORT).show();
        }
    }
    private void setupEventListeners() {
        btnTurnOnWifi.setOnClickListener(v -> toggleWiFi());
        btnTurnOnBluetooth.setOnClickListener(v -> checkBluetoothPermission());

        startBleNetwork.setOnClickListener(v -> startBluetoothPairing());
        stopBleNetwork.setOnClickListener(v -> stopBluetoothPairing());
        searchBleDevs.setOnClickListener(v -> searchBluetoothDevices());

        cbOnlyNearDev.setOnCheckedChangeListener((buttonView, isChecked) -> {
            devBluetoothListAdapter.clearData();
            presenter.setOnlySearchNearDev(isChecked);
            SPUtil.getInstance(this).setSettingParam("isOnlySearchNearDev", isChecked);
        });

        btnShowBottomSheet.setOnClickListener(v -> showBottomNavigationDrawer());

        wifiLl.setOnClickListener(v -> checkPermissionAndProceed(WifiPowerOnCamer.class));
        g4Ll.setOnClickListener(v -> startActivity(new Intent(this, com.cofe.solution.ui.device.add.four_g.WifiPowerOnCamer.class)));
        viotLl.setOnClickListener(v -> startActivity(new Intent(this, PowerOnVIOT.class)));
        dvrLl.setOnClickListener(v -> startActivity(new Intent(this, PowerOnDvrDevice.class)));
        wiredLl.setOnClickListener(v -> startActivity(new Intent(this, DevLanConnectActivity.class)));
        shareLL.setOnClickListener(v -> startActivity(new Intent(this, DevSnConnectActivity.class)));
    }


    private void checkPermissionAndProceed(Class<?> activityClass) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
        } else {
            startActivity(new Intent(this, activityClass));
        }
    }
    private void startBluetoothPairing() {
        if (devBluetoothListAdapter.getItemCount() == 0) {
            Toast.makeText(this, "No Bluetooth devices found.", Toast.LENGTH_LONG).show();
        } else {
            startActivityForResult(new Intent(this, InputWiFiInfoActivity.class), REQUEST_INPUT_WIFI_INFO_CODE);
        }
        isMassCtrl = true;
    }

    private void stopBluetoothPairing() {
        searchBleDevs.setVisibility(View.VISIBLE);
        stopBleNetwork.setVisibility(View.GONE);
        isStopBlePairing = true;
    }

    private void searchBluetoothDevices() {
        devBluetoothListAdapter.clearData();
        presenter.setOnlySearchNearDev(cbOnlyNearDev.isChecked());
        presenter.searchDevByBluetooth(this);
        searchBleDevs.setVisibility(View.GONE);
        startBleNetwork.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSearchDevBluetoothResult(XMBleInfo xmBleInfo) {
        devBluetoothListAdapter.setData(xmBleInfo);

        // Show the BottomSheet when the first device is found
        if (devBluetoothListAdapter.getItemCount() == 1) {
            showBluetoothDeviceBottomSheet();
        }
    }

    @Override
    public void onConnectDebBleResult(String mac, int resultCode) {
        Toast.makeText(this, resultCode == REQUEST_SUCCESS ? "Bluetooth connected successfully" : "Bluetooth connection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectWiFiResult(String mac, XMBleData xmBleData) {
        Toast.makeText(this, xmBleData != null ? "WiFi Configured!" : "WiFi Configuration Failed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDevBleItemSelected(XMBleInfo xmBleInfo) {
        if (stopBleNetwork.getVisibility() == View.VISIBLE) {
            return;
        }

        Intent intent = new Intent(this, InputWiFiInfoActivity.class);
        intent.putExtra("mac", xmBleInfo.getMac());
        startActivityForResult(intent, REQUEST_INPUT_WIFI_INFO_CODE);
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


    private void showBluetoothDeviceBottomSheet() {
        if (devBluetoothListAdapter.getItemCount() == 0) {
            Toast.makeText(this, "No Bluetooth devices found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.activity_device_bluetooth_list, null);

        // Initialize UI elements from XML
        Button startBleNetwork = bottomSheetView.findViewById(R.id.btn_start_ble_network);
        Button stopBleNetwork = bottomSheetView.findViewById(R.id.btn_stop_ble_network);
        Button searchBleDevs = bottomSheetView.findViewById(R.id.btn_search_ble_dev);
        CheckBox cbOnlyNearDev = bottomSheetView.findViewById(R.id.cb_only_near_dev);
        RecyclerView rvDevBluetoothList = bottomSheetView.findViewById(R.id.rv_dev_bluetooth_list);

        // Setup RecyclerView
        rvDevBluetoothList.setLayoutManager(new LinearLayoutManager(this));
        rvDevBluetoothList.setAdapter(devBluetoothListAdapter);

        // Handle Bluetooth Device Search
        searchBleDevs.setOnClickListener(v -> {
            devBluetoothListAdapter.clearData();
            presenter.setOnlySearchNearDev(cbOnlyNearDev.isChecked());
            presenter.searchDevByBluetooth(this);
            searchBleDevs.setVisibility(View.GONE);
            startBleNetwork.setVisibility(View.VISIBLE);
        });

        // Handle Start Bluetooth Pairing
        startBleNetwork.setOnClickListener(v -> {
            if (devBluetoothListAdapter.getItemCount() == 0) {
                Toast.makeText(this, "No Bluetooth devices found.", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(this, InputWiFiInfoActivity.class);
                startActivityForResult(intent, REQUEST_INPUT_WIFI_INFO_CODE);
            }
            isMassCtrl = true;
        });

        // Handle Stop Bluetooth Pairing
        stopBleNetwork.setOnClickListener(v -> {
            searchBleDevs.setVisibility(View.VISIBLE);
            stopBleNetwork.setVisibility(View.GONE);
            isStopBlePairing = true;
        });

        // Handle Near Device Filtering
        cbOnlyNearDev.setOnCheckedChangeListener((buttonView, isChecked) -> {
            devBluetoothListAdapter.clearData();
            presenter.setOnlySearchNearDev(isChecked);
            SPUtil.getInstance(this).setSettingParam("isOnlySearchNearDev", isChecked);
        });

        // Set the BottomSheet Content View and Show
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INPUT_WIFI_INFO_CODE) {
            if (resultCode == RESULT_OK) {
                ssid = data.getStringExtra("ssid");
                password = data.getStringExtra("password");
                String mac = data.getStringExtra("mac");
                isBleNetworkSuccess.clear();
                startBleNetwork.setVisibility(View.GONE);
                stopBleNetwork.setVisibility(View.VISIBLE);
                isStopBlePairing = false;
                if (!StringUtils.isStringNULL(mac)) {
                    startNextBelNetwork(mac);
                } else {
                    startNextBelNetwork();
                }
            }
        } else if (requestCode == REQUEST_LOCATION_SOURCE_SETTINGS) {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            //判断是否开启了GPS
            boolean ok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            //若开启，申请权限
            if (ok) {
                Toast.makeText(this, getString(R.string.enable_location), Toast.LENGTH_LONG).show();
                //checkBLEPermission();
            }
        }
    }

    /**
     * 开始下一个设备蓝牙配网
     */
    private void startNextBelNetwork(String mac) {
        if (isStopBlePairing) {
            return;
        }

        if (!StringUtils.isStringNULL(mac)) {
            presenter.connectBle(mac);
        } else {
            searchBleDevs.setVisibility(View.VISIBLE);
            stopBleNetwork.setVisibility(View.GONE);
            isStopBlePairing = true;
        }
    }

    private void startNextBelNetwork() {
        if (isMassCtrl) {
            XMBleInfo xmBleInfo = devBluetoothListAdapter.getData(isBleNetworkSuccess.size());
            if (xmBleInfo != null) {
                startNextBelNetwork(xmBleInfo.getMac());
            }
        }
    }

    /**
     * 处理蓝牙配网返回的数据
     *
     * @param data
     */
    private void dealWithDevInfoFromBleConfig(String mac, XMBleData data) {
        if (data == null) {
            return;
        }

        LogUtils.debugInfo(APP_BLE, "Received data from device:" + data.getContentDataHexString());
        try {

            /**
             * V1.0---->如果命令ID是设备端响应ID并且当前状态是配网状态的就认为是设备配网结果回调 或者命令ID是设备端回调ID
             * V2.0及以上版本---->根据不同的命令Id判断设备是回调还是响应
             */
            if (((data.getCmdId() == CMD_RECEIVE) && data.getVersion() == 1) || data.getCmdId() == CMD_CALLBACK) {
                XMBleInfo xmBleInfo = devBluetoothListAdapter.getData(mac);
                HashMap hashMap = XMBleManager.parseBleWiFiConfigResult(xmBleInfo.getProductId(), data.getContentDataHexString());
                if (hashMap != null) {
                    boolean isSuccess = (boolean) hashMap.get("isSuccess");
                    if (data.getVersion() > 1) {
                        //设备端返回配网结果后，APP段需要回一个应答包，如果一直不回复，设备重新进入配网状态
                        presenter.responseReceiveConnectWiFiResult(mac, isSuccess);
                    }
                    if (isSuccess) {
                        XMDevInfo xmDevInfo = (XMDevInfo) hashMap.get("devInfo");
                        if (xmDevInfo == null) {
                            return;
                        }

                        //判断是否为低功耗设备
                        if (DevDataCenter.getInstance().isLowPowerDevByPid(xmDevInfo.getPid())) {
                            SDBDeviceInfo sdbDeviceInfo = xmDevInfo.getSdbDevInfo();
                            sdbDeviceInfo.st_7_nType = SDKCONST.DEVICE_TYPE.IDR;
                            xmDevInfo.setDevType(SDKCONST.DEVICE_TYPE.IDR);
                        }


                        presenter.setDevId(xmDevInfo.getDevId());

                        //如果设备支持Token的话，需要获取设备特征码
                        if (!StringUtils.isStringNULL(xmDevInfo.getDevToken())) {
                            loaderDialog.setMessage();

                            presenter.getCloudCryNum(xmDevInfo);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                devBluetoothListAdapter.setProgress(mac, 100);
                                ToastUtils.showLong("配网成功：" + xmDevInfo.getDevId());
                            }
                        });

                        startNextBelNetwork();
                    }
                }
            } else if (data.getCmdId() == CMD_RECEIVE) {
                //设备响应上报
                String hexData = data.getContentDataHexString();
                int connectResult = ConvertUtils.hexString2Int(hexData.substring(0, 2));
                //命令发送失败
                if (connectResult == 0x01) {
                    LogUtils.debugInfo(APP_BLE, "Bluetooth distribution network command was sent,and the device responded successfully");
                } else {
                    LogUtils.debugInfo(APP_BLE, "Bluetooth distribution network command was sent,end the device  responded failed");
                    ToastUtils.showLong(R.string.distribution_network_failure);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showLong(R.string.distribution_network_failure);
        }
    }

    @Override
    public void onAddDevResult(XMDevInfo xmDevInfo, boolean isSuccess, int errorId) {
        loaderDialog.dismiss();
        if (!isSuccess) {
            if (errorId != -99992) {
                showToast(getString(R.string.Add_Dev_Failed) , Toast.LENGTH_LONG);
                return;
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiStateReceiver);
        unregisterReceiver(bluetoothStateReceiver);
    }
}
