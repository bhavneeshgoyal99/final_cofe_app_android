package com.cofe.solution.ui.device.add.qrcode.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.sdk.bean.StringUtils;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.utils.LogUtils;
import com.xm.activity.base.XMBaseActivity;
import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;

import java.util.logging.ErrorManager;

import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.device.add.qrcode.contract.SetDevToRouterByQrCodeContract;
import com.cofe.solution.ui.device.add.qrcode.presenter.SetDevToRouterByQrCodePresenter;
import com.cofe.solution.ui.device.add.qrcode.view.fragement.ConnectiobnInfoFragment;
import com.cofe.solution.ui.device.add.qrcode.view.fragement.ConntionDisplayFragment;
import com.cofe.solution.ui.device.preview.view.DevMonitorActivity;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * 通过二维码扫描方式添加设备
 * Devices are added by means of QR code scanning
 * @author hws
 * @class
 * @time 2020/8/11 17:19
 */
@RuntimePermissions
public class SetDevToRouterByQrCodeActivity extends DemoBaseActivity<SetDevToRouterByQrCodePresenter> implements SetDevToRouterByQrCodeContract.ISetDevToRouterByQrCodeView {
    private ImageView ivQrCode;
    private EditText etSsid;
    private EditText etPwd;
    private TextView tvResult;
    private Button btnShowQrCode;
    String ssid;
    String password;
    @Override
    public SetDevToRouterByQrCodePresenter getPresenter() {
        return new SetDevToRouterByQrCodePresenter(this);
    }
    ViewPager2 viewPager;
    private LinearLayout indicatorLayout;
    private int indicatorCount;
    private ImageView[] indicators;
    private MediaPlayer mediaPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_to_router_by_qr_code);
        ssid =  getIntent().getStringExtra("ssid");
        password =  getIntent().getStringExtra("password");
        initView();
        initListener();

        indicatorLayout = findViewById(R.id.indicatorLayout);

        Log.d("TAG","thu sus testig");
        viewPager  = findViewById(R.id.viewPager);
        SetDevToRouterByQrCodeActivityPermissionsDispatcher.initDataWithPermissionCheck(this);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        // Set up indicators
        indicatorCount = adapter.getItemCount();
        indicators = new ImageView[indicatorCount];
        setupIndicators();

        // Add PageChangeCallback
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicators(position);
            }
        });

        // Initial highlight
        updateIndicators(0);

        initData();

        findViewById(R.id.cancle_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupIndicators() {
        for (int i = 0; i < indicatorCount; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(indicatorLayout.getContext().getDrawable(R.drawable.indicator_inactive));
            indicators[i].setPadding(8, 8, 8, 8);

            // Add indicator to layout
            indicatorLayout.addView(indicators[i]);
        }
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < indicatorCount; i++) {
            if (i == position) {
                indicators[i].setImageDrawable(indicatorLayout.getContext().getDrawable(R.drawable.indicator_active));
            } else {
                indicators[i].setImageDrawable(indicatorLayout.getContext().getDrawable(R.drawable.indicator_inactive));
            }
        }
    }

    private void initView() {
        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.network_configuration_procedure));
        ivQrCode = findViewById(R.id.iv_qr_code);
        etSsid = findViewById(R.id.editWifiSSID);
        etPwd = findViewById(R.id.editWifiPasswd);
        btnShowQrCode = findViewById(R.id.btn_show_qr_code_1);
        tvResult = findViewById(R.id.tv_result_info);


        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etSsid.setText(ssid);
        etPwd.setText(password);

        playBackgroundAudio();

    }

    private void initListener() {
        btnShowQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ssid = etSsid.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                if (StringUtils.isStringNULL(ssid)) {
                    ToastUtils.showLong(R.string.the_ssid_is_not_empty);
                    return;
                }

                int pwdType = 0;
                if (!TextUtils.isEmpty(pwd)) {
                    pwdType = 1;
                }

                presenter.stopSetDevToRouterByQrCode();
                Bitmap bitmap = presenter.startSetDevToRouterByQrCodeSimple(ssid, pwd,pwdType);
                if (bitmap != null) {
                    ivQrCode.setImageBitmap(bitmap);
                } else {
                    showToast(getString(R.string.libfunsdk_set_dev_to_router_f), Toast.LENGTH_LONG);
                }
                findViewById(R.id.rl_show_qr_code).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.btn_show_qr_code_simple).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        checkingLoginToken();
    }

    void checkingLoginToken(){
        if(DevDataCenter.getInstance().getAccountUserName()!=null) {
            if(DevDataCenter.getInstance().getAccessToken()==null) {
                AccountManager.getInstance().xmLogin(DevDataCenter.getInstance().getAccountUserName(), DevDataCenter.getInstance().getAccountPassword(), 1,
                        new BaseAccountManager.OnAccountManagerListener() {
                            @Override
                            public void onSuccess(int msgId) {
                                Log.d("Access toekn" ," > "  +DevDataCenter.getInstance().getAccessToken());

                                if(DevDataCenter.getInstance().getAccessToken()==null) {
                                    checkingLoginToken();
                                }
                            }

                            @Override
                            public void onFailed(int msgId, int errorId) { }

                            @Override
                            public void onFunSDKResult(Message msg, MsgContent ex) { }
                        });

            } else { }

        } else { }

    }
    /**
     * 需要获取定位权限（获取WiFI列表及信息的时候需要）
     * Location permission is required (when accessing WiFI list and information)
     */
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void initData() {
        if (checkLocationService()) {
            presenter.initWiFi();
            etSsid.setText(presenter.getConnectWiFiSsid());
            btnShowQrCode.performClick();
            btnShowQrCode.callOnClick();
        } else {
            XMPromptDlg.onShow(SetDevToRouterByQrCodeActivity.this, FunSDK.TS("System_SDK_INT_Tip"), FunSDK.TS("cancel"), FunSDK.TS("confirm"), null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onSetDevToRouterResult(boolean isSuccess, XMDevInfo xmDevInfo) {
        if (isSuccess) {
            showToast(getString(R.string.libfunsdk_set_dev_to_router_s), Toast.LENGTH_LONG);
            loaderDialog.setMessage();
        }
    }

    @Override
    public void onAddDevToAccountResult(boolean isSuccess, int errorId) {
        Log.d("onAddDevToAccountResult", "adding device to account > " +errorId  + " | | > " + isSuccess);
        Log.d("DevDataCenter","data > " + DevDataCenter.getInstance());

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loaderDialog.dismiss();
                tvResult.setVisibility(View.VISIBLE);
                findViewById(R.id.rl_show_qr_code).setVisibility(View.GONE);

                if (isSuccess) {
                    showToast(getString(R.string.add_s), Toast.LENGTH_LONG);
                    tvResult.setText(tvResult.getText().toString() + "\n" + getString(R.string.add_s));
                    presenter.syncDevTimeZone();
                } else {
                    if (errorId == -604101) {
                        showToast(getString(R.string.the_dev_already_exist), Toast.LENGTH_LONG);
                        findViewById(R.id.rl_show_qr_code).setVisibility(View.GONE);
                        tvResult.setText(tvResult.getText().toString() + "\n" + getString(R.string.the_dev_already_exist));

                        finish();
                    } else {
                        showToast(getString(R.string.add_f) , Toast.LENGTH_LONG);
                        findViewById(R.id.rl_show_qr_code).setVisibility(View.GONE);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public void onPrintConfigDev(String printLog) {
        LogUtils.debugInfo("QrCodeConfig",printLog);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                tvResult.setText(tvResult.getText().toString() + "\n" + printLog);
            }
        });
    }

    @Override
    public void onSyncDevTimeResult(boolean isSuccess, int errorId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XMPromptDlg.onShow(SetDevToRouterByQrCodeActivity.this, getString(R.string.need_turn_to_monitor), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        turnToActivity(DevMonitorActivity.class);
                        finish();
                    }
                },null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stopSetDevToRouterByQrCode();

        // Release MediaPlayer resources to avoid memory leaks
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SetDevToRouterByQrCodeActivityPermissionsDispatcher.initDataWithPermissionCheck(this);
    }

    public class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return the appropriate fragment
            if (position == 0) {
                return new ConntionDisplayFragment();
            } else {
                return new ConnectiobnInfoFragment();
            }
        }

        @Override
        public int getItemCount() {
            // Total number of pages
            return 2;
        }
    }

    private void playBackgroundAudio() {
        // Initialize MediaPlayer with an MP3 file from res/raw
        mediaPlayer = MediaPlayer.create(this, R.raw.second_voice ); // Replace 'sample_mp3' with your file name
        mediaPlayer.start(); // Start playing the audio
    }


    @Override
    public void onStop(){
        super.onStop();
        mediaPlayer.stop(); // Start playing the audio
    }

}