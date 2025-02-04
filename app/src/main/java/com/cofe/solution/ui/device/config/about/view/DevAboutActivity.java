package com.cofe.solution.ui.device.config.about.view;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static android.view.View.VISIBLE;
import static com.lib.EFUN_ATTR.LOGIN_USER_ID;
import static com.lib.sdk.bean.SystemInfoBean.CONNECT_TYPE_P2P;
import static com.lib.sdk.bean.SystemInfoBean.CONNECT_TYPE_RPS;
import static com.lib.sdk.bean.SystemInfoBean.CONNECT_TYPE_RTS;
import static com.lib.sdk.bean.SystemInfoBean.CONNECT_TYPE_RTS_P2P;
import static com.lib.sdk.bean.SystemInfoBean.CONNECT_TYPE_TRANSMIT;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lib.ECONFIG;
import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.SysDevAbilityInfoBean;
import com.lib.sdk.bean.SystemFunctionBean;
import com.lib.sdk.bean.share.DevShareQrCodeInfo;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.manager.sysability.SysAbilityManager;
import com.utils.FileUtils;
import com.utils.XUtils;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ItemSetLayout;
import com.xm.ui.widget.ListSelectItem;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;

import com.cofe.solution.R;
import com.cofe.solution.ui.device.config.BaseConfigActivity;
import com.cofe.solution.ui.device.config.about.listener.DevAboutContract;
import com.cofe.solution.ui.device.config.about.presenter.DevAboutPresenter;
import io.reactivex.annotations.Nullable;

/**
 * 关于设备界面,包含设备基本信息(序列号,设备型号,硬件版本,软件版本,
 * 发布时间,设备时间,运行时间,网络模式,云连接状态,固件更新及恢复出厂设置)
 * Created by jiangping on 2018-10-23.
 */
public class DevAboutActivity extends BaseConfigActivity<DevAboutPresenter> implements DevAboutContract.IDevAboutView, View.OnClickListener {
    private TextView devSnText = null;
    private int FILE_PICKER_REQUEST_CODE = 101;
    private int MANAGE_STORAGE_PERMISSION_CODE = 2296;
    private int PERMISSION_REQUEST_CODE = 102;

    private TextView devModelText = null;
    private TextView devHWVerText = null;
    private TextView devSWVerText = null;
    private TextView devPubDateText = null;
    private TextView devPubTimeText = null;
    private TextView devRunTimeText = null;
    private TextView devNatCodeText = null;
    private TextView devNatStatusText = null;
    private ImageView devSNCodeImg = null;
    private ImageView updateFromStorage = null;
    private TextView devUpdateText = null;
    private ListSelectItem lsiDevUpgrade;
    private ListSelectItem lsiDevPid;//设备PID信息
    private ListSelectItem lsiDevLocalUpgrade;//本地升级
    private ListSelectItem lsiSyncDevTime;//同步时间
    private ListSelectItem lsiSyncDevTimeZone;//同步时区
    private ListSelectItem lsiOemId;//OEMID信息
    private ListSelectItem lsiICCID;//ICCID信息
    private ListSelectItem lsiIMEI;//IMEI信息
    private ListSelectItem lsiNetworkMode;//网络模式
    private Button defaltConfigBtn = null;
    private TextView tvDevInfo;
    private boolean isLocalUpgrade;//是否为本地升级
    private static final int SYS_LOCAL_FILE_REQUEST_CODE = 0x08;
    private String firmwareType;//固件類型 默认是System（主控），Mcu（单片机）


    @Override
    public DevAboutPresenter getPresenter() {
        return new DevAboutPresenter(this);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_system_info);
        initView();
        initData();
    }

    private void initView() {

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_system_info));
        titleBar.setLeftClick(this);

        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.device_system_info));


        devSnText = findViewById(R.id.textDeviceSN_1);
        devHWVerText = findViewById(R.id.textDeviceHWVer_1);
        devSWVerText = findViewById(R.id.textDeviceSWVer_1);
        devPubDateText = findViewById(R.id.textDevicePubDate_1);

        devRunTimeText = findViewById(R.id.textDeviceRunTime_1);
        devNatCodeText = findViewById(R.id.textDeviceNetwork);
        devSNCodeImg = findViewById(R.id.imgDeviceQRCode_1);
        devUpdateText = findViewById(R.id.textDeviceUpgrade_1);
        defaltConfigBtn = findViewById(R.id.defealtconfig_1);
        updateFromStorage = findViewById(R.id.updateFromStorage);
        defaltConfigBtn.setOnClickListener(this);

        //tvDevInfo = ((ItemSetLayout) findViewById(R.id.isl_device_info)).getMainLayout().findViewById(R.id.textDeviceNatCode_1);

        lsiDevUpgrade = findViewById(R.id.lsi_check_dev_upgrade);

        presenter.getDevInfo("System");

        lsiDevUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presenter.isDevUpgradeEnable()) {
                    presenter.startDevUpgrade();
                } else {
                    showToast(getString(R.string.already_latest), Toast.LENGTH_LONG);
                }
            }
        });
        updateFromStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openStorage();

            }
        });

        lsiDevLocalUpgrade = findViewById(R.id.lsi_local_dev_upgrade);

        lsiDevLocalUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, SYS_LOCAL_FILE_REQUEST_CODE);
            }
        });

        lsiDevPid = findViewById(R.id.lsi_dev_pid);

        lsiSyncDevTime = findViewById(R.id.lsi_sync_dev_time);

        lsiSyncDevTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                presenter.syncDevTime();
            }
        });

        lsiSyncDevTimeZone = findViewById(R.id.lsi_sync_dev_time_zone);
        lsiSyncDevTimeZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                presenter.syncDevTimeZone();
            }
        });

        lsiOemId = findViewById(R.id.lsi_dev_oemid);
        lsiICCID = findViewById(R.id.lsi_iccid);
        lsiIMEI = findViewById(R.id.lsi_imei);
        lsiNetworkMode = findViewById(R.id.lsi_network_mode);
    }

    private void initData() {
        Intent intent = getIntent();
        firmwareType = intent.getStringExtra("firmwareType");
        if (StringUtils.isStringNULL(firmwareType)) {
            firmwareType = "System";
        }

        presenter.getDevInfo(firmwareType);
        presenter.getDevCapsAbility(this);
        Log.d("Dev About Page ", "device id > " + presenter.getDevId());
        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
        if (xmDevInfo != null) {
            lsiDevPid.setRightText(StringUtils.isStringNULL(xmDevInfo.getPid()) ? "" : xmDevInfo.getPid());
        }

        this.publishDeviceData();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onUpdateView(String result) {
        try {
            Log.d("DevAbout Activity", "onUpdateView  result " + result);
            JSONObject originalResult = new JSONObject(result);
            JSONObject systemInfo = new JSONObject(originalResult.get("SystemInfo").toString());

            Log.d("DevAbout Activity", "onUpdateView  result > 210 " + systemInfo.get("BuildTime"));

            devSnText.setText(systemInfo.get("SerialNo").toString());
            devSnText.setText("a***n");
            devSWVerText.setText(systemInfo.get("SoftWareVersion").toString());
            devSWVerText.setText(systemInfo.get("HardWare").toString());
            devPubDateText.setText(systemInfo.get("BuildTime").toString());
            Bitmap bitmap = getShareDevQrCode(getApplicationContext());
            Glide.with(getApplicationContext()).load(bitmap).into(devSNCodeImg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void publishDeviceData() {
        DeviceManager.getInstance().getDevAllAbility(presenter.getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                Log.d("Device result ", " presenter.getDevId() > " + result.getOriginalJson());

                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                Log.d("onFailed > ", " errorId > " + errorId);
                Log.d("onFailed > ", " jsonName > " + jsonName);
            }
        });
    }

    @Override
    public void onCheckDevUpgradeResult(boolean isSuccess, boolean isNeedUpgrade) {
        if (isNeedUpgrade) {
            lsiDevUpgrade.setRightText(getString(R.string.have_new_version_click_to_upgrade));
        } else {
            lsiDevUpgrade.setRightText(getString(R.string.already_latest));
        }
    }

    @Override
    public void onDevUpgradeProgressResult(int upgradeState, int progress) {
        switch (upgradeState) {
            //正在下载升级包
            case ECONFIG.EUPGRADE_STEP_DOWN:
                if (isLocalUpgrade) {
                    lsiDevLocalUpgrade.setRightText(getString(R.string.download_dev_firmware) + ":" + progress);
                } else {
                    lsiDevUpgrade.setRightText(getString(R.string.download_dev_firmware) + ":" + progress);
                }
                break;
            //正在上传
            case ECONFIG.EUPGRADE_STEP_UP:
                if (isLocalUpgrade) {
                    lsiDevLocalUpgrade.setRightText(getString(R.string.upload_dev_firmware) + ":" + progress);
                } else {
                    lsiDevUpgrade.setRightText(getString(R.string.upload_dev_firmware) + ":" + progress);
                }
                break;
            //正在升级
            case ECONFIG.EUPGRADE_STEP_UPGRADE:
                if (isLocalUpgrade) {
                    lsiDevLocalUpgrade.setRightText(getString(R.string.dev_upgrading) + ":" + progress);
                } else {
                    lsiDevUpgrade.setRightText(getString(R.string.dev_upgrading) + ":" + progress);
                }
                break;
            //升级完成
            case ECONFIG.EUPGRADE_STEP_COMPELETE:
                if (isLocalUpgrade) {
                    lsiDevLocalUpgrade.setRightText(getString(R.string.completed_dev_upgrade));
                } else {
                    lsiDevUpgrade.setRightText(getString(R.string.completed_dev_upgrade));
                }

                isLocalUpgrade = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void syncDevTimeZoneResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        showToast(isSuccess ? getString(R.string.set_dev_config_success) : getString(R.string.set_dev_config_failed), Toast.LENGTH_LONG);
    }

    @Override
    public void syncDevTimeResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        showToast(isSuccess ? getString(R.string.set_dev_config_success) : getString(R.string.set_dev_config_failed), Toast.LENGTH_LONG);
    }

    @Override
    public void onGetDevOemIdResult(String oemId) {
        lsiOemId.setRightText(oemId);
    }

    @Override
    public void onGetICCIDResult(String iccid) {
        if (iccid != null) {
            lsiICCID.setVisibility(View.VISIBLE);
            lsiICCID.setRightText(iccid);
        }
    }

    @Override
    public void onGetIMEIResult(String imei) {
        if (imei != null) {
            lsiIMEI.setVisibility(View.VISIBLE);
            lsiIMEI.setRightText(imei);
        }
    }

    @Override
    public void onDevUpgradeFailed(int errorId) {
        //如果升级失败了，提示失败原因，然后重新检测升级
        XMPromptDlg.onShow(this, FunSDK.TS("TR_download_failure_click"), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // finish();
            }
        });
    }

    @Override
    public void onDevNetConnectMode(int netConnectType) {
        if (netConnectType == CONNECT_TYPE_P2P) {
            devNatCodeText.setText("P2P");
//            lsiNetworkMode.setRightText("P2P");
        } else if (netConnectType == CONNECT_TYPE_TRANSMIT) {
            devNatCodeText.setText(getString(R.string.settings_about_transmit_mode));
//            lsiNetworkMode.setRightText(getString(R.string.settings_about_transmit_mode));
        } else if (netConnectType == CONNECT_TYPE_RPS) {
            devNatCodeText.setText("RPS");
//            lsiNetworkMode.setRightText(FunSDK.TS("RPS"));
        } else if (netConnectType == CONNECT_TYPE_RTS_P2P) {
            devNatCodeText.setText("RTS P2P");
//            lsiNetworkMode.setRightText(FunSDK.TS("RTS P2P"));
        } else if (netConnectType == CONNECT_TYPE_RTS) {
            devNatCodeText.setText("RTS Proxy");
//            lsiNetworkMode.setRightText(FunSDK.TS("RTS Proxy"));
        } else {
            devNatCodeText.setText("IP");
//            lsiNetworkMode.setRightText("IP");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SYS_LOCAL_FILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String filePath = presenter.saveFileFromUri(this, uri);
                XMPromptDlg.onShow(DevAboutActivity.this, getString(R.string.please_sel_firmware_type), getString(R.string.main_control), getString(R.string.mcu), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.startDevLocalUpgrade(firmwareType, filePath);
                        isLocalUpgrade = true;
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.startDevLocalUpgrade(firmwareType, filePath);
                        isLocalUpgrade = true;
                    }
                });

            }
        }
        if (requestCode == FILE_PICKER_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    String filePath = presenter.saveFileFromUri(this, uri);
                    // showFirmwarePrompt(filePath); // Your custom method to handle the file
                }
            }
        }
        if (requestCode == MANAGE_STORAGE_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "Storage permission granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void openStorage() {
        if (checkPermission()) {
            // Permission granted, open file picker
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*"); // Specify MIME type as needed
            startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
        } else {
            // Request necessary permissions
            requestPermission();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above, request MANAGE_EXTERNAL_STORAGE permission
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivityForResult(intent, MANAGE_STORAGE_PERMISSION_CODE);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, MANAGE_STORAGE_PERMISSION_CODE);
            }
        } else {
            // For Android 10 and below, request READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean readGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeGranted = grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (readGranted && writeGranted) {
                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
                    openStorage(); // Open file picker after permissions are granted
                } else {
                    Toast.makeText(this, "Storage permissions are required!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showFirmwarePrompt(String filePath) {
        XMPromptDlg.onShow(
                this,
                getString(R.string.please_sel_firmware_type),
                getString(R.string.main_control),
                getString(R.string.mcu),
                view -> presenter.startDevLocalUpgrade(firmwareType, filePath),
                view -> presenter.startDevLocalUpgrade(firmwareType, filePath)
        );
    }

//    private void openStorage() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("*/*"); // Specify a MIME type if needed, e.g., "application/pdf"
//        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
//    }
//
//    private boolean checkPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            return Environment.isExternalStorageManager();
//        } else {
//            int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
//            int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
//        }
//    }
//
//    private void requestPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
//                startActivityForResult(intent, MANAGE_STORAGE_PERMISSION_CODE);
//            } catch (Exception e) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivityForResult(intent, MANAGE_STORAGE_PERMISSION_CODE);
//            }
//        } else {
//            // Below Android 11
//            ActivityCompat.requestPermissions(this,
//                    new String[]{
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    }, PERMISSION_REQUEST_CODE);
//        }
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0) {
//                boolean readGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                boolean writeGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                if (readGranted && writeGranted) {
//                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Storage permissions are required!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }
//    private void showFirmwarePrompt(String filePath) {
//        XMPromptDlg.onShow(
//                this,
//                getString(R.string.please_sel_firmware_type),
//                getString(R.string.main_control),
//                getString(R.string.mcu),
//                view -> presenter.startDevLocalUpgrade(firmwareType, filePath),
//                view -> presenter.startDevLocalUpgrade(firmwareType, filePath)
//        );
//    }



    public Bitmap getShareDevQrCode(Context context) {
        String loginUserId = FunSDK.GetFunStrAttr(LOGIN_USER_ID);
        String pwd = FunSDK.DevGetLocalPwd(presenter.getDevId());
        int devType = DevDataCenter.getInstance().getDevType(presenter.getDevId());
        DevShareQrCodeInfo devShareQrCodeInfo = new DevShareQrCodeInfo();
        devShareQrCodeInfo.setDevType(devType);//设备类型
        devShareQrCodeInfo.setUserId(loginUserId);//账号登录userId
        String loginName = FunSDK.DevGetLocalUserName(presenter.getDevId());//获取本地的设备登录密码
        devShareQrCodeInfo.setDevId(presenter.getDevId());//设置设备登录密码
        devShareQrCodeInfo.setPwd(pwd);//设置设备登录密码
        devShareQrCodeInfo.setLoginName(TextUtils.isEmpty(loginName) ? "admin" : loginName); //设备设备登录名
        devShareQrCodeInfo.setShareTimes(System.currentTimeMillis() / 1000);//设置分享时间（该时间是用来在扫描二维码添加的时候判断是否过期了，具体的过期时长可自定义，比如30分钟）
        String devToken = FunSDK.DevGetLocalEncToken(presenter.getDevId());//获取设备的登录Token（支持Token的设备才有）
        if (!TextUtils.isEmpty(devToken)) {
            devShareQrCodeInfo.setDevToken(devToken);//设置设备的登录Token
        }
        String permissionValue =         "{\"DP_ModifyConfig\": 1,\"DP_ModifyPwd\": 1,\"DP_CloudServer\": 1,\"DP_Intercom\": 1,\"DP_PTZ\": 1,\"DP_LocalStorage\": 1,\"DP_ViewCloudVideo\": 1,\"DP_DeleteCloudVideo\": 1,\"DP_AlarmPush\": 1,\"DP_DeleteAlarmInfo\": 1}";
        devShareQrCodeInfo.setPermissions(permissionValue);
        /**
         * 如果要设置访问权限，可以调用以下方法
         * devShareQrCodeInfo.setPermissions(“权限信息”)，这个权限信息自定义，比如
         * {
         * "DP_ModifyConfig": 0,//修改设备配置
         * "DP_ModifyPwd": 0,//修改设备密码 暂不提供修改
         * "DP_CloudServer": 0,//访问云服务 暂不提供修改
         * "DP_Intercom": 1,//对讲
         * "DP_PTZ": 1,//云台
         * "DP_LocalStorage": 1,//本地存储
         * "DP_ViewCloudVideo": 0,//查看云视频
         * "DP_DeleteCloudVideo": 0,//删除云视频 暂不提供修改
         * "DP_AlarmPush": 0,//推送（包括查看报警消息）
         * "DP_DeleteAlarmInfo": 0//删除报警消息（包括图片）暂不提供修改
         * }，该信息是透传的，生成二维码后扫描获取到该信息，直接解析就行；
         * */

        String info = JSON.toJSONString(devShareQrCodeInfo);//注意：该数据建议要加密处理后再生成二维码
        Log.d("share Generate QR code Share  ","QR code data "  + info);
        Log.d("share Generate QR code Share  ","QR code data devId"  + devShareQrCodeInfo.getDevId());

        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_);//获取logo
        System.out.println("encInfo:" + info);
        Bitmap bitmap = null;
        try {
            bitmap = createQRCodeBitmap(info, logo, BarcodeFormat.QR_CODE, 600);//生成二维码
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
    /**
     * 创建二维码
     * @param text
     * @param logo
     * @param format
     * @param qrSize
     * @return
     * @throws WriterException
     */
    private Bitmap createQRCodeBitmap(String text, Bitmap logo, BarcodeFormat format, int qrSize) throws WriterException {
        Matrix matrix = new Matrix();
        float sx = 80.0F / (float)logo.getWidth();
        float sy = 80.0F / (float)logo.getHeight();
        matrix.setScale(sx, sy);
        logo = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, false);
        MultiFormatWriter wirter = new MultiFormatWriter();
        Hashtable<EncodeHintType, Object> hintTypes = new Hashtable();
        hintTypes.put(EncodeHintType.CHARACTER_SET, CharacterSetECI.UTF8);
        hintTypes.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        BitMatrix bitMatrix = wirter.encode(text, format, qrSize, qrSize, hintTypes);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int halfW = width / 2;
        int halfH = height / 2;
        int[] pixels = new int[width * height];

        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                if (x > halfW - 40 && x < halfW + 40 && y > halfH - 40 && y < halfH + 40) {
                    pixels[y * width + x] = logo.getPixel(x - halfW + 40, y - halfH + 40);
                } else if (bitMatrix.get(x, y)) {
                    pixels[y * width + x] = -16777216;
                } else {
                    pixels[y * width + x] = -1;
                }
            }
        }

        logo = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        logo.setPixels(pixels, 0, width, 0, 0, width, height);
        return logo;
    }
}