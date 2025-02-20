package com.cofe.solution.ui.device.add.wifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cofe.solution.ui.device.add.AddNewDeviceActivity;
import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.utils.XMWifiManager;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;

import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.device.add.qrcode.view.SetDevToRouterByQrCodeActivity;
import com.cofe.solution.utils.SPUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;



/**
 * WiFi SSID和密码输入
 */
@RuntimePermissions
public class InputWiFiInfoActivityForQr extends DemoBaseActivity {
    private EditText wifiSSIDEdit;//WiFi 热点名称
    private EditText wifiPasswdEdit;// WiFi 密码
    private Button btnNext;//下一步
    private Disposable disposable;
    private XMWifiManager xmWifiManager;
    private ScanResult scanResult;
    private String mac;//蓝牙mac地址
    private MediaPlayer mediaPlayer;


    @Override
    public XMBasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_set_wifi);

        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.input_wifi_info));


        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wifiSSIDEdit.getText().toString().trim().length()>2 )
                    if(wifiPasswdEdit.getText().toString().trim().length()>2) {
                        //WiFi热点
                        String ssid = wifiSSIDEdit.getText().toString().trim();
                        //WiFi密码
                        String password = wifiPasswdEdit.getText().toString().trim();
                        SPUtil.getInstance(InputWiFiInfoActivityForQr.this).setSettingParam("wifi_ssid", ssid);
                        SPUtil.getInstance(InputWiFiInfoActivityForQr.this).setSettingParam("wifi_password", password);
                        Intent intent = new Intent(InputWiFiInfoActivityForQr.this, SetDevToRouterByQrCodeActivity.class);
                        intent.putExtra("ssid", ssid);
                        intent.putExtra("password", password);
                        intent.putExtra("mac", mac);
                        startActivity(intent);
                    } else {
                        Toast.makeText(wifiSSIDEdit.getContext(), getString(R.string.please_enter_wifi_password), Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(wifiSSIDEdit.getContext(), getString(R.string.please_enter_wifi_ssid), Toast.LENGTH_LONG).show();
                }
            }
        });

        wifiSSIDEdit = findViewById(R.id.editWifiSSID);
        wifiPasswdEdit = findViewById(R.id.editWifiPasswd);

        InputWiFiInfoActivityForQrPermissionsDispatcher.initDataWithPermissionCheck(this);
        mac = getIntent().getStringExtra("mac");
        playBackgroundAudio();

    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void initData() {
        xmWifiManager = XMWifiManager.getInstance(this);
        if (checkLocationService()) {// Whether to enable location permission
            checkGetWiFiInfoOk();
        } else {
            XMPromptDlg.onShow(InputWiFiInfoActivityForQr.this, getString(R.string.enable_location), FunSDK.TS("cancel"), FunSDK.TS("confirm"), null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
        }
    }

    protected boolean checkLocationService() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean isOpenGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            return isOpenGPS;
        } else {
            return true;
        }
    }

    /**
     * Confirm that the current WiFi information can be obtained normally
     */
    private void checkGetWiFiInfoOk() {
        disposable = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Exception {
                String curSSID = xmWifiManager.getSSID();
                scanResult = xmWifiManager.getCurScanResult(curSSID);
                if (scanResult == null) {
                    emitter.onNext(0);
                } else {
                    emitter.onNext(scanResult);
                }
            }
        }).subscribeOn(Schedulers.newThread()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) {

            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object result) throws Exception {
                if (disposable != null) {
                    disposable.dispose();
                    disposable = null;
                }
                if (result instanceof Integer && (Integer) result == 0) {
                    Toast.makeText(InputWiFiInfoActivityForQr.this, FunSDK.TS(getString(R.string.network_error)), Toast.LENGTH_LONG).show();
                } else {
                    if ((((ScanResult) result).frequency > 4900 && ((ScanResult) result).frequency < 5900)) {
                        Toast.makeText(InputWiFiInfoActivityForQr.this, FunSDK.TS(getString(R.string.frequency_support)), Toast.LENGTH_LONG).show();
                    }

                    String curSSID = xmWifiManager.getSSID();
                    if (!StringUtils.isStringNULL(curSSID)) {
                        wifiSSIDEdit.setText(curSSID);
                        String password = SPUtil.getInstance(InputWiFiInfoActivityForQr.this).getSettingParam("wifi_password", "");
                        wifiPasswdEdit.setText(password);
                    }
                }
            }
        });
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        InputWiFiInfoActivityForQrPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }



    private void playBackgroundAudio() {
        // Initialize MediaPlayer with an MP3 file from res/raw
        mediaPlayer = MediaPlayer.create(this, R.raw.first_voice ); // Replace 'sample_mp3' with your file name
        mediaPlayer.start(); // Start playing the audio
    }

    @Override
    public void onStop(){
        super.onStop();
        mediaPlayer.stop(); // Start playing the audio
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release MediaPlayer resources to avoid memory leaks
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
