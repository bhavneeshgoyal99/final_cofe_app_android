package com.cofe.solution.ui.device.preview.view;

import static android.view.View.VISIBLE;
import static com.blankj.utilcode.util.ScreenUtils.getScreenWidth;
import static com.blankj.utilcode.util.StringUtils.getString;
import static com.manager.db.Define.LOGIN_NONE;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_PlAY;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_SAVE_PIC_FILE_S;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_SAVE_RECORD_FILE_S;
import static com.manager.device.media.audio.XMAudioManager.SPEAKER_TYPE_MAN;
import static com.manager.device.media.audio.XMAudioManager.SPEAKER_TYPE_NORMAL;
import static com.manager.device.media.audio.XMAudioManager.SPEAKER_TYPE_WOMAN;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.cofe.solution.app.SDKDemoApplication;
import com.cofe.solution.base.BatteryDrawable;
import com.cofe.solution.base.CustomBottomSheet;
import com.cofe.solution.base.CustomBottomSheetDialogFragment;
import com.cofe.solution.base.SharedPreference;
import com.cofe.solution.ui.device.alarm.view.DevAlarmMsgActivity;
import com.cofe.solution.ui.device.alarm.view.DevAlarmMsgFragment;
import com.cofe.solution.ui.device.config.detecttrack.DetectTrackFragment;
import com.cofe.solution.ui.device.record.view.DevRecordFragment;
import com.cofe.solution.ui.dialog.CustomBottomDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.lib.EFUN_ERROR;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.ElectCapacityBean;
import com.lib.sdk.bean.SensorInfoBean;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.SystemFunctionBean;
import com.lib.sdk.bean.WifiRouteInfo;
import com.lib.sdk.bean.tour.TourBean;
import com.lib.sdk.struct.MultiLensParam;
import com.lib.sdk.struct.SDK_FishEyeFrame;
import com.manager.ScreenOrientationManager;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.manager.device.config.PwdErrorManager;
import com.manager.device.config.preset.PresetManager;
import com.manager.device.idr.IDRManager;
import com.manager.device.media.attribute.PlayerAttribute;
import com.manager.device.media.monitor.MonitorManager;
import com.utils.XUtils;
import com.video.opengl.GLSurfaceView20;
import com.xm.linke.face.FaceFeature;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.manager.AutoHideManager;
import com.xm.ui.media.MultiWinLayout;
import com.xm.ui.widget.BtnColorBK;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.RippleButton;
import com.xm.ui.widget.XMScaleSeekBar;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.dialog.EditDialog;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.ptzview.PtzView;

import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.base.HistoryTracker;
import com.cofe.solution.ui.activity.DeviceConfigActivity;
import com.cofe.solution.ui.device.alarm.view.ActivityGuideDeviceLanAlarm;
import com.cofe.solution.ui.device.alarm.view.DoubleLightActivity;
import com.cofe.solution.ui.device.alarm.view.DoubleLightBoxActivity;
import com.cofe.solution.ui.device.alarm.view.GardenDoubleLightActivity;
import com.cofe.solution.ui.device.alarm.view.MusicLightActivity;
import com.cofe.solution.ui.device.alarm.view.WhiteLightActivity;
import com.cofe.solution.ui.device.aov.view.AovSettingActivity;
import com.cofe.solution.ui.device.config.DeviceSetting;
import com.cofe.solution.ui.device.config.cameralink.view.CameraLinkSetActivity;
import com.cofe.solution.ui.device.config.detecttrack.DetectTrackActivity;
import com.cofe.solution.ui.device.config.simpleconfig.view.DevSimpleConfigActivity;
import com.cofe.solution.ui.device.picture.view.DevPictureActivity;
import com.cofe.solution.ui.device.preview.listener.DevMonitorContract;
import com.cofe.solution.ui.device.preview.listener.SensorChangeContract;
import com.cofe.solution.ui.device.preview.presenter.DevMonitorPresenter;
import com.cofe.solution.ui.device.preview.presenter.SensorChangePresenter;
import com.cofe.solution.ui.device.preview.view.fragment_list.MessageFragment;
import com.cofe.solution.ui.device.preview.view.fragment_list.PlaybackFragment;
import com.cofe.solution.ui.device.preview.view.fragment_list.RealTimeFragment;
import com.cofe.solution.ui.device.record.view.DevRecordActivity;
import com.cofe.solution.utils.SPUtil;
import io.reactivex.annotations.Nullable;

import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_MEDIA_DISCONNECT;
import static com.xm.ui.dialog.PasswordErrorDlg.INPUT_TYPE_DEV_USER_PWD;

import static com.cofe.solution.base.DemoConstant.LAST_CHANGE_SCALE_TIMES;
import static com.cofe.solution.base.DemoConstant.MULTI_LENS_THREE_SENSOR;
import static com.cofe.solution.base.DemoConstant.MULTI_LENS_TWO_SENSOR;
import static com.cofe.solution.base.DemoConstant.SENSOR_MAX_TIMES;
import static com.cofe.solution.base.DemoConstant.SUPPORT_SCALE_THREE_LENS;
import static com.cofe.solution.base.DemoConstant.SUPPORT_SCALE_TWO_LENS;
import static com.cofe.solution.base.FunError.EE_DVR_ACCOUNT_PWD_NOT_VALID;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


/**
 * 设备预览界面,可以控制播放,停止,码流切换,截图,录制,全屏,信息.
 * 保存图像视频,语音对话.设置预设点并控制方向
 * <p>
 * The device preview interface allows control of playback, pause, stream switching, screenshot, recording, fullscreen, and information.
 * Save images and videos, engage in voice conversations, set presets, and control directions.
 */
public class  DevMonitorActivity extends DemoBaseActivity<DevMonitorPresenter> implements DevMonitorContract.IDevMonitorView, OnClickListener, SensorChangeContract.ISensorChangeView, RealTimeFragment.OnFeatureClickListener, DetectTrackFragment.OnFragmentCallbackListener   {
    private static final String TAG = "DevMonitorActivity";
    private MultiWinLayout playWndLayout;
    private RecyclerView rvMonitorFun;
    private ViewGroup wndLayout;
    private MonitorFunAdapter monitorFunAdapter;
    private ScreenOrientationManager screenOrientationManager;//Screen rotation manager
    private ViewGroup[] playViews;
    private int portraitWidth;
    private int portraitHeight;
    private int chnCount;
    private PresetViewHolder presetViewHolder;
    //电量WiFi状态显示，只有低功耗设备才会显示
    //Display battery and WiFi status, shown only on low-power devices.
    private TextView tvBatteryState;
    private TextView tvWiFiState;
    //实时码流
    //"Real-time stream"
    private TextView tvRate;
    // 是否打开指哪看哪
    //  Is point-to-view enabled
    private boolean isOpenPointPtz;

    //伴音
    // Background audio
    private static final int FUN_VOICE = 0;
    //抓图
    // Capture image
    private static final int FUN_CAPTURE = 1;
    //剪切
    // Clip
    private static final int FUN_RECORD = 2;
    // PTZ control
    // 云台控制
    private static final int FUN_PTZ = 3;
    //Gimbal Calibration
    //云台校正
    private static final int FUN_PTZ_CALIBRATION = 4;

    //  Intercom
    // 对讲
    private static final int FUN_INTERCOM = 5;

    // Playback
    // 回放
    private static final int FUN_PLAYBACK = 6;

    // Stream switching
    // 码流切换
    private static final int FUN_CHANGE_STREAM = 7;

    // Preset point
    // 预置点
    private static final int FUN_PRESET = 8;

    // Full screen
    // 全屏
    private static final int FUN_LANDSCAPE = 9;

    // Full stream
    // 满屏
    private static final int FUN_FULL_STREAM = 10;

    // LAN alarm
    // 局域网报警
    private static final int FUN_LAN_ALARM = 11;

    // Device image
    // 设备端图片
    private static final int FUN_DEV_PIC = 12;

    // Capture and save on the device side
    // 设备端抓图并保存
    private static final int FUN_DEV_CAPTURE_SAVE = 13;
    //设备远程抓图传回给APP（设备端不保存图片）
    private static final int FUN_DEV_CAPTURE_TO_APP = 14;
    // Real-time preview timeliness
    // 实时预览实时性
    private static final int FUN_REAL_PLAY = 15;

    // Simple data interaction
    // 简单数据交互
    private static final int FUN_SIMPLE_DATA = 16;

    // Video frame rotation
    // 视频画面旋转
    private static final int FUN_VIDEO_ROTATE = 17;

    // Feeding
    // 喂食
    private static final int FUN_FEET = 18;

    // irCut
    // irCut
    private static final int FUN_IRCUT = 19;

    // Restore factory settings
    // 恢复出厂设置
    private static final int FUN_RESET = 20;

    // Point-to-view
    // 指哪看哪
    private static final int FUN_POINT = 21;

    // Manual alarm
    // 手动警戒
    private static final int FUN_MANUAL_ALARM = 22;

    private static final int FUN_ALARM_BY_VOICE_LIGHT = 23;// 声光报警
    //Camera linkage
    //相机联动
    private static final int FUN_CAMERA_LINK = 24;

    //Mobile Tracking (Humanoid Tracking)
    //移动追踪（人形跟随）
    private static final int FUN_DETECT_TRACK = 25;

    //cruise
    //巡航
    private static final int FUN_CRUISE = 26;

    //联系家人(视频通话)
    //Contact family

    private static final int FUN_CONTACT_FAMILY = 27;
    //APP变倍缩放
    //App zooming
    private static final int FUN_SHOW_APP_ZOOMING = 28;


    //APP 软件实现多目效果
    //Multi-Objective Effect in an App
    private static final int FUN_APP_OBJ_EFFECT = 29;
    private List<HashMap<String, Object>> monitorFunList = new ArrayList<>();//预览页面的功能列表
    private AutoHideManager autoHideManager;//自动隐藏控件
    private Dialog tourDlg;
    private SystemFunctionBean systemFunctionBean;//设备能力集
    private XMScaleSeekBar sbVideoScale;//多目变倍控件
    private SensorChangePresenter sensorChangePresenter;
    private boolean isFirstGetVideoStream = true;//打开视频后首次获取到码流数据
    private boolean isDelayChangeStream = false;//针对多目设备获取到镜头信息后再进行码流切换
    private boolean isShowAPPZooming = false;//是否显示APP软变倍功能
    private boolean isShowAppMoreScreen = false;//是否显示APP端多目效果

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    HistoryTracker historyTracker;
    ImageView cameraImg;
    ImageView videoImg;
    ImageView soundImg;
    ImageView sdImag;
    ImageView microphoneImg, rotateScreen,openSetting,backImage;
    TextView dName;
    TextView sdTxt;
    ActivityResultLauncher<Intent> resultLauncher;
    Boolean isPlayBackOpen = false;
    String devId;
    int  chId;
    Button btnLayout1, btnLayout2, btnLayout3;
    LinearLayout layout1, layout2, layout3;
    private PopupWindow popupWindow;

    Handler handler;

    public static final String CUSTOM_HOME_ACTION = "com.cofe.solution.HOME_ACTION";
    MyHomeClickReceiver mHomeClickReceiver;
    LinearLayout battery;
    LinearLayout icon_ptz;
    LinearLayout mainButtonsLl;
    boolean isAOVDevice;
    boolean isLoadCompleteFirstTime = false;
    int playerWindowCount = 0;
    ImageView backArrowImg;

    boolean mainVideoStarted = false;
    boolean isVideoCaptureStart = false;
    boolean isSoundCaptureStart = false;
    boolean isFunInterCallActive = false;
    boolean isFunIntercomeTrigger = false;

    CustomBottomDialog audioDialog;
    private BatteryDrawable batteryDrawable;
    //com.rejowan.abv.ABV batteryIndicator;
    ProgressBar batteryProgressBar;

    Handler lowBatteryHandler;
    int wndInnerRVOriginalHeight = 0;
    BottomSheetDialog ptZBottomSheetDialog = null;
    BottomSheetDialog intercomeBottomSheetDialog = null;
    boolean isUHDActivated = false;
    View intercomeLayout;
    BottomSheetBehavior<View> intercomeBottomSheetInfoBehavior;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_camera);
        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition));
        getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition));

        // Initialize buttons and layouts
        btnLayout1 = findViewById(R.id.btn_layout1);
        btnLayout2 = findViewById(R.id.btn_layout2);
        btnLayout3 = findViewById(R.id.btn_layout3);

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);

        // Set up button click listeners
        btnLayout1.setOnClickListener(v -> showLayout(1));
        btnLayout2.setOnClickListener(v -> showLayout(2));
        btnLayout3.setOnClickListener(v -> showLayout(3));

        // Initialize default state
        showLayout(1);
        batteryDrawable = new BatteryDrawable();
        batteryProgressBar = findViewById(R.id.batteryProgressBar);
        initView();

    }
    private void showLayout(int layoutNumber) {
        // Reset all layouts to GONE

        // Reset button colors to dim
        btnLayout1.setBackgroundTintList(getColorStateList(R.color.dim_color));
        btnLayout2.setBackgroundTintList(getColorStateList(R.color.dim_color));
        btnLayout3.setBackgroundTintList(getColorStateList(R.color.dim_color));

        // Show the selected layout and highlight the corresponding button
        switch (layoutNumber) {
            case 1:
                layout1.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);

                btnLayout3.setBackground(ContextCompat.getDrawable(this, R.drawable.squaer_button_background_ubselected));
                btnLayout2.setBackground(ContextCompat.getDrawable(this, R.drawable.squaer_button_background_ubselected));
                btnLayout1.setBackground(ContextCompat.getDrawable(this, R.drawable.squaer_button_background));

                break;
            case 2:
                //layout1.setVisibility(View.GONE);
                //layout3.setVisibility(View.GONE);
                //layout2.setVisibility(VISIBLE);
                //loadRecordFragment(new Object[][]{{"devId", presenter.getDevId()}, {"chnId", presenter.getChnId()}});
                SharedPreference cookies = new SharedPreference(getApplicationContext());
                int width = wndLayout.getWidth();
                int height = wndLayout.getHeight();
                cookies.saveDevicePreviewHeightandWidth(width, height);
                cookies.saveisDeviceAOV(isAOVDevice);

                turnToActivity1(DevRecordActivity.class, new Object[][]{{"devId", presenter.getDevId()}, {"chnId", presenter.getChnId()}});

                //onFeatureClicked(FUN_PLAYBACK+"");
                break;
            case 3:
                if(mainVideoStarted){
                    layout3.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    btnLayout3.setBackground(ContextCompat.getDrawable(this, R.drawable.squaer_button_background));
                    btnLayout2.setBackground(ContextCompat.getDrawable(this, R.drawable.squaer_button_background_ubselected));
                    btnLayout1.setBackground(ContextCompat.getDrawable(this, R.drawable.squaer_button_background_ubselected));
                    presenter.getDevId();
                    loadFragment();
                } else {
                    showToast(getString(R.string.please_wait_for_start_video_stram), Toast.LENGTH_SHORT);
                }
                //turnToActivity(DevAlarmMsgActivity.class);
                break;


        }


        // Initialize icons
        LinearLayout cloudStorageIcon = findViewById(R.id.icon_cloud_storage);
        LinearLayout aiIcon = findViewById(R.id.icon_ai);
        LinearLayout stobeIcon = findViewById(R.id.icon_alarm);

        LinearLayout icon_motion_tracking = findViewById(R.id.icon_motion_tracking);
        icon_ptz = findViewById(R.id.icon_ptz);
        LinearLayout icon_favorites = findViewById(R.id.icon_favorites);

        LinearLayout icon_med_tracking = findViewById(R.id.icon_med_tracking);
        LinearLayout icon_ptz_reverse = findViewById(R.id.icon_ptz_reverse);
        LinearLayout icon_sd_card_alb = findViewById(R.id.icon_sd_card_alb);
        LinearLayout zoom = findViewById(R.id.zoom);
        LinearLayout imageListLl = findViewById(R.id.image_list_ll);
        icon_sd_card_alb.setVisibility(View.GONE);

        battery = findViewById(R.id.battery);

        // Set click listeners
        cloudStorageIcon.setOnClickListener(v -> {
            onFeatureClicked(cloudStorageIcon.getTag().toString());
        });

        aiIcon.setOnClickListener(v -> {
            onFeatureClicked(aiIcon.getTag().toString());
        });

        stobeIcon.setOnClickListener(v -> {
            onFeatureClicked(stobeIcon.getTag().toString());
        });

        icon_motion_tracking.setOnClickListener(v -> {
            loadMoionFragment();
            //onFeatureClicked(icon_motion_tracking.getTag().toString());
        });

        icon_ptz.setOnClickListener(v -> {
            DetectTrackFragment myFragment = (DetectTrackFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container1);
            if (myFragment != null) {
                myFragment.closeThisFragment();
            }

            onFeatureClicked(icon_ptz.getTag().toString());
        });


        icon_favorites.setOnClickListener(v -> {
            onFeatureClicked(icon_favorites.getTag().toString());
        });


        icon_med_tracking.setOnClickListener(v -> {
            onFeatureClicked(icon_med_tracking.getTag().toString());
        });

        icon_ptz_reverse.setOnClickListener(v -> {
            onFeatureClicked(icon_ptz_reverse.getTag().toString());
        });

        icon_sd_card_alb.setOnClickListener(v -> {
            onFeatureClicked(icon_sd_card_alb.getTag().toString());
        });

        zoom.setOnClickListener(v -> {
            onFeatureClicked(zoom.getTag().toString());
        });

        imageListLl.setOnClickListener(v -> {
            onFeatureClicked(imageListLl.getTag().toString());
        });

        battery.setOnClickListener(v -> {
            onFeatureClicked(battery.getTag().toString());
        });
        findViewById(R.id.iv_answer_call).setOnClickListener(v -> {
            onFeatureClicked(findViewById(R.id.iv_answer_call).getTag().toString());
        });

        //checkAOVBattery(false);

    }


    void checkUserLogin() {
        if(DevDataCenter.getInstance().getAccountUserName()!=null) {
            if(DevDataCenter.getInstance().getAccessToken()==null) {
                AccountManager.getInstance().xmLogin(DevDataCenter.getInstance().getAccountUserName(), DevDataCenter.getInstance().getAccountPassword(), 1,
                        new BaseAccountManager.OnAccountManagerListener() {
                            @Override
                            public void onSuccess(int msgId) {
                                Log.d("Access toekn" ," > "  +DevDataCenter.getInstance().getAccessToken());
                                checkAOVBattery(false);

                            }

                            @Override
                            public void onFailed(int msgId, int errorId) {

                            }

                            @Override
                            public void onFunSDKResult(Message msg, MsgContent ex) {

                            }
                        });//LOGIN_BY_INTERNET（1）  Account login type

            } else {
                checkAOVBattery(false);
            }

        } else {
            checkAOVBattery(false);
        }
    }

    void checkAOVBattery(boolean isPreviewChange) {
        DeviceManager.getInstance().getDevAllAbility(presenter.getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                Log.d("Device id " , " presenter.getDevId() > "  +presenter.getDevId());
                Log.d("Device id " , " result.OtherFunction.AovMode > "  +result.OtherFunction.AovMode);
                Log.d("Device id " , " result.OtherFunction.BatteryManager > "  +result.OtherFunction.BatteryManager);

                try {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(devId);
                    if(xmDevInfo.getDevType() == SDKCONST.DEVICE_TYPE.ROBOT){
                        findViewById(R.id.iv_answer_call_ll).setVisibility(VISIBLE);
                    }

                    if (result.OtherFunction.AovMode) {
                        isAOVDevice =  result.OtherFunction.AovMode;
                        battery.setVisibility(VISIBLE);
                        batteryProgressBar.setVisibility(VISIBLE);
                        changePlayViewSize();
                        //findViewById(R.id.iv_answer_call_ll).setVisibility(VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(isPreviewChange){
                                    /*if(playerWindowCount==2) {
                                        onFeatureClicked("" + FUN_APP_OBJ_EFFECT);
                                    }*/
                                    //onFeatureClicked("" + FUN_APP_OBJ_EFFECT);
                                }

                            }
                        }, 3000);
                    } else {
                        isAOVDevice =  false;
                    }
                    initData();

                }catch ( Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                Log.d("onFailed > ", " errorId > "  +errorId);
                Log.d("onFailed > ", " jsonName > "  +jsonName);
            }
        });
    }
    private void initView() {

        /*//DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setHomeAsUpIndicator(batteryDrawable);
            }
        });*/

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_preview));
        titleBar.setRightBtnResource(R.mipmap.icon_setting_normal, R.mipmap.icon_setting_pressed);
        titleBar.setLeftClick(this);
        titleBar.setRightIvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                Intent intent = new Intent();
                intent.setClass(DevMonitorActivity.this, DeviceConfigActivity.class);
                intent.putExtra("devId", presenter.getDevId());
                DevMonitorActivity.this.startActivity(intent);
            }
        });

        titleBar.setBottomTip(DevMonitorActivity.class.getName());
        playWndLayout = findViewById(R.id.layoutPlayWnd);
        rvMonitorFun = findViewById(R.id.rv_monitor_fun);
        wndLayout = findViewById(R.id.wnd_layout);

        tvBatteryState = findViewById(R.id.tv_battery_state);
        tvWiFiState = findViewById(R.id.tv_wifi_state);
        tvRate = findViewById(R.id.tv_rate);
        initPresetLayout();

        autoHideManager = new AutoHideManager();
        autoHideManager.addView(findViewById(R.id.ll_dev_state));
        autoHideManager.addView(findViewById(R.id.top_nav_bar));
        autoHideManager.addView(findViewById(R.id.side_setting_icon_rl));

        autoHideManager.show();

        // Initialize TabLayout and ViewPager2
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        cameraImg = findViewById(R.id.camera);
        videoImg= findViewById(R.id.video);
        soundImg = findViewById(R.id.sound);
        sdImag = findViewById(R.id.sd);
        sdTxt = findViewById(R.id.sd_txt);
        microphoneImg = findViewById(R.id.microphone);
        rotateScreen = findViewById(R.id.rotate_icon);
        openSetting = findViewById(R.id.settings_icon);
        backImage = findViewById(R.id.settings_icon);
        dName = findViewById(R.id.device_name);
        SharedPreference cookies = new SharedPreference(getApplication());
        dName.setText(cookies.retrievDevName());

        mainButtonsLl = findViewById(R.id.main_buttons_ll);

        findViewById(R.id.camera_ll).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dealWithMonitorFunction(Integer.parseInt(cameraImg.getTag().toString()), true);
            }
        });
        findViewById(R.id.video_ll).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideoCaptureStart = (isVideoCaptureStart) ? false : true;
                int image= (isVideoCaptureStart) ? R.drawable.active_video : R.drawable.video_icon ;
                Glide.with(getApplicationContext()).load(image).into(videoImg);

                dealWithMonitorFunction(Integer.parseInt(videoImg.getTag().toString()), isVideoCaptureStart);
            }
        });
        findViewById(R.id.sound_ll).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isSoundCaptureStart = (isSoundCaptureStart) ? false : true;
                int image= (isSoundCaptureStart) ? R.drawable.speaker_icon_1 : R.drawable.speaker_disabled_icon ;
                Glide.with(getApplicationContext()).load(image).into(soundImg);
                dealWithMonitorFunction(Integer.parseInt(soundImg.getTag().toString()), isSoundCaptureStart);
            }
        });
        findViewById(R.id.sd_ll).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isUHDActivated = (isUHDActivated) ? false : true;

                    if(isUHDActivated) {
                        sdTxt.setVisibility(View.GONE);
                        sdImag.setVisibility(View.VISIBLE);
                    } else{
                        sdTxt.setVisibility(View.VISIBLE);
                        sdImag.setVisibility(View.GONE);
                    }
                    dealWithMonitorFunction(Integer.parseInt(sdImag.getTag().toString()), true);
            }
        });

        findViewById(R.id.microphone_ll).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dealWithMonitorFunction(Integer.parseInt(microphoneImg.getTag().toString()), true);
            }
        });

        rotateScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    dealWithMonitorFunction(9, true);
                } else {
                    screenOrientationManager.portraitScreen(DevMonitorActivity.this, true);
                }
            }
        });

        openSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent (DevMonitorActivity.this, DeviceSetting.class);
                XMDevInfo devInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
                Gson gson = new Gson();
                String personJson = gson.toJson(devInfo);

                i.putExtra("dev", personJson);
                startActivity(i);
            }
        });


        findViewById(R.id.stobe_img).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeatureClicked(findViewById(R.id.stobe_img).getTag().toString());
            }
        });

        // Set up the ViewPager2 Adapter
        viewPager.setAdapter(new DeviceViewPagerAdapter(this));
        historyTracker = new HistoryTracker();
        historyTracker.trackPageChange(viewPager);


        // Link TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Real-time");
                        break;
                    case 1:
                        tab.setText("Playback");
                        break;
                    case 2:
                        tab.setText("Message");
                        break;
                }
            }
        }).attach();

        findViewById(R.id.back_arrow_img).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mHomeClickReceiver = new MyHomeClickReceiver();

        dName.setText(cookies.retrievDevName());
        checkUserLogin();

        intercomeBottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);

        // intercome work
        LinearLayout actionLayout = findViewById(R.id.main_buttons_ll);
        audioDialog = new CustomBottomDialog(this, actionLayout);
        intercomeLayout = LayoutInflater.from(this).inflate(R.layout.view_intercom, null, false);
        //audioDialog.setExternalView(intercomeLayout);
        RippleButton rippleButton = intercomeLayout.findViewById(R.id.btn_talk);
        ListSelectItem lsiDoubleTalkSwitch = intercomeLayout.findViewById(R.id.lsi_double_talk_switch);
        ListSelectItem lsiTalkBroadcast = intercomeLayout.findViewById(R.id.lsi_double_talk_broadcast);
        lsiTalkBroadcast.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lsiTalkBroadcast.setRightImage(lsiTalkBroadcast.getRightValue() == SDKCONST.Switch.Close ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
            }
        });
        lsiTalkBroadcast.setVisibility(chnCount > 1 ? VISIBLE : View.GONE);//如果是多通道设备，那么支持广播对讲功能
        rippleButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //判断双向对讲配置是否开启
                        if (lsiDoubleTalkSwitch.getRightValue() == SDKCONST.Switch.Open) {
                            //双向对讲
                            if (presenter.isTalking(presenter.getChnId())) {
                                //当前对讲开启中，则需要关闭对讲
                                presenter.stopIntercom(presenter.getChnId());// Pause the intercom in the middle of a conversation
                                rippleButton.clearState();
                                isFunInterCallActive = false;
                                audioDialog.setCanDismiss(isFunInterCallActive);

                            } else {
                                //当前对讲未开启，则需要开启对讲 (如果设备的通道数(chnCount)大于1，比如NVR设备的话，那么要使用通道对讲)
                                boolean isTalkBroadcast = lsiTalkBroadcast.getRightValue() == SDKCONST.Switch.Open;
                                presenter.startDoubleIntercom(presenter.getChnId(), isTalkBroadcast); //Turn on the two-way intercom
                                rippleButton.setUpGestureEnable(false);
                                //对讲开启的时候，视频伴音会随之关闭
                                monitorFunAdapter.changeBtnState(FUN_VOICE, false);
                                isFunInterCallActive = true;
                                audioDialog.setCanDismiss(isFunInterCallActive);
                            }

                        } else {
                            //(如果设备的通道数(chnCount)大于1，比如NVR设备的话，那么要使用通道对讲)
                            boolean isTalkBroadcast = lsiTalkBroadcast.getRightValue() == SDKCONST.Switch.Open;
                            presenter.startSingleIntercomAndSpeak(presenter.getChnId(), isTalkBroadcast);//Enable a one-way intercom
                            rippleButton.setUpGestureEnable(true);
                            //对讲开启的时候，视频伴音会随之关闭
                            monitorFunAdapter.changeBtnState(FUN_VOICE, false);
                            isFunInterCallActive = true;
                            audioDialog.setCanDismiss(isFunInterCallActive);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (lsiDoubleTalkSwitch.getRightValue() == SDKCONST.Switch.Close) {
                            presenter.stopSingleIntercomAndHear(presenter.getChnId());
                            isFunInterCallActive = false;
                            audioDialog.setCanDismiss(isFunInterCallActive);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        lsiDoubleTalkSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                lsiDoubleTalkSwitch.setRightImage(lsiDoubleTalkSwitch.getRightValue() == SDKCONST.Switch.Close ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
                if (lsiDoubleTalkSwitch.getRightValue() == SDKCONST.Switch.Open) {
                    rippleButton.setTabText(getString(R.string.click_to_open_two_way_talk));
                } else {
                    rippleButton.setTabText(getString(R.string.long_press_open_one_way_talk));
                }

                isFunInterCallActive = false;
                audioDialog.setCanDismiss(isFunInterCallActive);

                presenter.stopIntercom(presenter.getChnId());
            }
        });

        String[] voiDataList =  new String[]{getString(R.string.speaker_type_normal), getString(R.string.speaker_type_man), getString(R.string.speaker_type_woman)};
        final boolean[] isVoiceCick = {false};
        LinearLayout aboveLl =  intercomeLayout.findViewById(R.id.above_ll);
        ListView lv =  intercomeLayout.findViewById(R.id.lv_voice_change);
        ListSelectItem lsiChooseVoice = intercomeLayout.findViewById(R.id.lsi_choose_voice);
        TextView voiceTitleTxtv = intercomeLayout.findViewById(R.id.voice_title_txtv);
        TextView voiceSelectedValueTxtv = intercomeLayout.findViewById(R.id.voice_title_value_txtv);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.itemlistview, R.id.itemTextView, voiDataList);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = voiDataList[position];
                lsiChooseVoice.setRightText(selectedItem);
                voiceSelectedValueTxtv.setText(selectedItem);
                presenter.setSpeakerType(presenter.getChnId(), position);

                //Toast.makeText(getApplicationContext(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                isVoiceCick[0] =  (isVoiceCick[0]) ? false  : true;
                if(isVoiceCick[0]) {
                    lv.setVisibility(VISIBLE);
                    voiceTitleTxtv.setVisibility(VISIBLE);
                    aboveLl.setVisibility(View.GONE);
                    lsiChooseVoice.setVisibility(View.GONE);
                    rippleButton.setVisibility(View.GONE);
                } else{
                    lv.setVisibility(View.GONE);
                    voiceTitleTxtv.setVisibility(View.GONE);
                    aboveLl.setVisibility(VISIBLE);
                    lsiChooseVoice.setVisibility(VISIBLE);
                    rippleButton.setVisibility(VISIBLE);
                }
            }
        });

        lsiChooseVoice.getExtraSpinner().initData(new String[]{getString(R.string.speaker_type_normal), getString(R.string.speaker_type_man), getString(R.string.speaker_type_woman)}, new Integer[]{SPEAKER_TYPE_NORMAL, SPEAKER_TYPE_MAN, SPEAKER_TYPE_WOMAN});
        lsiChooseVoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isVoiceCick[0] =  (isVoiceCick[0]) ? false  : true;
                if(isVoiceCick[0]) {
                    lv.setVisibility(VISIBLE);
                    voiceTitleTxtv.setVisibility(VISIBLE);
                    aboveLl.setVisibility(View.GONE);
                    lsiChooseVoice.setVisibility(View.GONE);
                    rippleButton.setVisibility(View.GONE);

                } else{
                    voiceTitleTxtv.setVisibility(View.GONE);
                    lv.setVisibility(View.GONE);
                    aboveLl.setVisibility(VISIBLE);
                    lsiChooseVoice.setVisibility(VISIBLE);
                    rippleButton.setVisibility(VISIBLE);

                }
                //lsiChooseVoice.toggleExtraView();
            }
        });
        intercomeLayout.findViewById(R.id.voice_change_ll).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isVoiceCick[0] =  (isVoiceCick[0]) ? false  : true;
                if(isVoiceCick[0]) {
                    lv.setVisibility(VISIBLE);
                    voiceTitleTxtv.setVisibility(VISIBLE);
                    aboveLl.setVisibility(View.GONE);
                    lsiChooseVoice.setVisibility(View.GONE);
                    rippleButton.setVisibility(View.GONE);

                } else{
                    voiceTitleTxtv.setVisibility(View.GONE);
                    lv.setVisibility(View.GONE);
                    aboveLl.setVisibility(VISIBLE);
                    lsiChooseVoice.setVisibility(VISIBLE);
                    rippleButton.setVisibility(VISIBLE);

                }
                //lsiChooseVoice.toggleExtraView();
            }
        });

        lsiChooseVoice.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener<Integer>() {
            @Override
            public void onItemClick(int position, String key, Integer value) {
                lsiChooseVoice.toggleExtraView(true);
                lsiChooseVoice.setRightText(key);
                presenter.setSpeakerType(presenter.getChnId(), value);
            }
        });

                /*XMPromptDlg.onShow(this, layout, true, new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                       // presenter.stopIntercom(presenter.getChnId());
                    }
                });*/

        lsiDoubleTalkSwitch.performClick();
        showAudioCallPopup(intercomeLayout);


        initSensorView();
    }

    /**
     * 初始化播放窗口布局
     * "Initialize the layout of playback windows."
     */
    private void initPlayWnd() {
        //窗口个数按照1、4、9、16...整数的平方规则来设置，如果不是可开平方的数，例如： 5，那么要传两个参数，第一个参数是5，第二个是每行显示的窗口数，（5,2）就表示 每行是2个，总共3行
        //"The number of windows is set according to the rule of integer squares, such as 1, 4, 9, 16..."
        int wndCount = (int) Math.sqrt(chnCount);
        if (wndCount < Math.sqrt(chnCount)) {
            wndCount = (int) (Math.pow(wndCount + 1, 2));
        } else {
            wndCount = (int) Math.pow(wndCount, 2);
        }
        playerWindowCount = wndCount;

        playViews = playWndLayout.setViewCount(wndCount);
        try {
            playWndLayout.setOnMultiWndListener(new MultiWinLayout.OnMultiWndListener() {
                @Override
                public boolean isDisableToChangeWndSize(int i) {
                    int orientation = getResources().getConfiguration().orientation;
                    return isShowAppMoreScreen && orientation == Configuration.ORIENTATION_PORTRAIT;//如果是APP的假多目效果，则禁止多窗口双击切换
                }

                @Override
                public boolean onTouchEvent(int wndId, MotionEvent motionEvent) {
                    return false;
                }

                @Override
                public boolean onSingleWnd(int i, MotionEvent motionEvent, boolean b) {
                    //如果当前是假多目模式，默认是通道0
                    if (isShowAppMoreScreen) {
                        presenter.setChnId(0);
                    } else {
                        presenter.setChnId(i);
                    }
                    return false;
                }

                @Override
                public boolean onSelectedWnd(int i, MotionEvent motionEvent, boolean isSelected) {
                    //如果当前是假多目模式，默认是通道0
                    if (isShowAppMoreScreen) {
                        presenter.setChnId(0);
                    } else {
                        if (isSelected) {
                            presenter.setChnId(i);
                        }
                    }
                    return false;
                }

                @Override
                public boolean onSingleTapUp(int i, MotionEvent motionEvent, boolean b) {
                    return false;
                }

                @Override
                public boolean onSingleTapConfirmed(int i, MotionEvent motionEvent, boolean b) {
                    try {
                        if (autoHideManager.isVisible()) {
                            autoHideManager.hide();
                        } else {
                            autoHideManager.show();
                        }

                        if (isOpenPointPtz) {
                            // 上下分屏的时候 打开指哪看哪才会触发
                            // "When in split-screen mode, triggering 'point-to-view' will activate the view corresponding to the direction pointed at."

                            // 点击相对x坐标
                            // "Click on the relative X coordinate."
                            float x = motionEvent.getX();
                            // 点击相对y坐标
                            // "Click on the relative Y coordinate."
                            float y = motionEvent.getY();
                            int xOffset = 0;
                            int yOffset = 0;

                            // 按照上半视频中心点划分距离 总共步长范围为 -4096 到 4096
                            // "Divide the distance based on the center point of the upper half of the video. The total step range is from -4096 to 4096."
                            xOffset = (int) (x * 8192 / playWndLayout.getWidth() - 4096);

                            // 按照上半视频中心点划分距离 总共步长范围为 -4096 到 4096
                            // "Divide the distance based on the center point of the upper half of the video. The total step range is from -4096 to 4096."
                            yOffset = -(int) (y * 8192 / (playWndLayout.getHeight() / 2) - 4096);
                            Log.d("dzc", "xoffset:" + xOffset + "  yoffset:" + yOffset);
                            if (xOffset >= -4096 && xOffset <= 4096 && yOffset >= -4096 && yOffset <= 4096) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("Name", "OPPtzLocate");
                                JSONObject object = new JSONObject();
                                object.put("xoffset", xOffset);
                                object.put("yoffset", yOffset);
                                JSONArray jsonArray = new JSONArray();
                                jsonArray.add(object);
                                jsonObject.put("OPPtzLocate", jsonArray);

                                presenter.setPointPtz(jsonObject.toString());


                            }
                        }


                        MonitorManager monitorManager = presenter.getCurSelMonitorManager(playWndLayout.getSelectedId());
                        if (monitorManager != null) {
                            dealWithVideoScale(monitorManager.getScale());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        recreate();
                    }
                    return false;
                }


                @Override
                public boolean onDoubleTap(View view, MotionEvent motionEvent) {
                    return false;
                }

                @Override
                public void onLongPress(View view, MotionEvent motionEvent) {

                }

                @Override
                public void onFling(View view, MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
//                float x = motionEvent1.getX() - motionEvent.getX();
//                float y = motionEvent1.getY() - motionEvent.getY();
//                int chnId = view.getId();
//                System.out.println("onFling:" + presenter.getTwoLensesScreen(0) + " " + x + " " + y);
//                if (Math.abs(x) > Math.abs(y)) {
//                    if (Math.abs(x) > 100) {
//                        if (x > 0) {
//                            // 向右滑动
//                            if (presenter.getTwoLensesScreen(0) == VRSoftDefine.XMTwoLensesScreen.ScreenRightOnly) {
//                                presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenDouble);
//                            } else {
//                                presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenLeftOnly);
//                            }
//
//                            if (--chnId < 0) {
//                                chnId = 0;
//                            }
//                        } else {
//                            //向左滑动
//                            if (presenter.getTwoLensesScreen(0) == VRSoftDefine.XMTwoLensesScreen.ScreenLeftOnly) {
//                                presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenDouble);
//                            } else {
//                                presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenRightOnly);
//                            }
//
//
//                            if (++chnId >= chnCount) {
//                                chnId = chnCount - 1;
//                            }
//                        }
//
//                        //是否为多窗口设备，并且当前是单窗口没有缩放显示，才支持切换窗口
//                        if (chnCount > 1 && presenter.getChnId() != chnId && playWndLayout.isSingleWnd() && !presenter.isVideoScale()) {
//                            playWndLayout.changeChannel(chnId);
//                        }
//                    }
//
//                } else {
//                    if (y > 0) {
//                        // 向下滑动
//                        if (presenter.getTwoLensesScreen(0) == VRSoftDefine.XMTwoLensesScreen.ScreenRightOnly) {
//                            presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenDouble);
//                        } else {
//                            presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenLeftOnly);
//                        }
//                    } else {
//                        // 下上滑动
//                        if (presenter.getTwoLensesScreen(0) == VRSoftDefine.XMTwoLensesScreen.ScreenLeftOnly) {
//                            presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenDouble);
//                        } else {
//                            presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenRightOnly);
//                        }
//                    }
//                }
                }
            });
        } catch ( Exception e) {
            e.printStackTrace();
            recreate();
        }
    }

    /**
     * 初始化预置点页面
     */
    private void initPresetLayout() {
        presetViewHolder = new PresetViewHolder();
        presetViewHolder.initView(LayoutInflater.from(getContext()).inflate(R.layout.view_preset_layout, null));
    }

    /**
     * 初始化Sensor切换布局(多目变倍)
     */
    private void initSensorView() {
        sbVideoScale = findViewById(R.id.sb_video_scale);
        sbVideoScale.setOnScaleSeekBarListener(new XMScaleSeekBar.OnScaleSeekBarListener() {
            @Override
            public void onItemSelected(int progress, boolean isTouchUp) {
                if (isTouchUp) {
                    MonitorManager monitorManager = presenter.getCurSelMonitorManager(playWndLayout.getSelectedId());
                    if (monitorManager != null) {
                        SPUtil.getInstance(DevMonitorActivity.this).setSettingParam(LAST_CHANGE_SCALE_TIMES + presenter.getDevId(), (float) (progress * ((float) 1 / sbVideoScale.getSmallSubCount())));
                        sensorChangePresenter.ctrlVideoScaleWhenUp(monitorManager, progress, -1, 1);
                    }
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        chnCount = intent.getIntExtra("chnCount", 1);
        titleBar.setTitleText(getString(R.string.channel) + ":" + presenter.getChnId());
        presenter.setChnId(0);
        devId = presenter.getDevId();
        chId = presenter.getChnId();

        initPlayWnd();

        for (int i = 0; i < chnCount && i < playViews.length; ++i) {
            presenter.initMonitor(i, playViews[i]);
        }

        monitorFunAdapter = new MonitorFunAdapter();
        rvMonitorFun.setLayoutManager(new GridLayoutManager(this, 4));
        rvMonitorFun.setAdapter(monitorFunAdapter);

        screenOrientationManager = ScreenOrientationManager.getInstance();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getContext().registerReceiver(mHomeClickReceiver, filter, RECEIVER_NOT_EXPORTED);
        } else{
            registerReceiver(mHomeClickReceiver, filter);

        }
        sensorChangePresenter = new SensorChangePresenter(this);
    }


    /**
     * onResume->manager.loginDev（）->iDevMonitorView.onLoginResult（）-> true ?  startMonitor(): manager.loginDev or toast
     * onResume->manager.loginDev（）-> initData()-> manager.getDevAbility() -> getDevWiFiSignalLevel() ->iDevMonitorView.onWiFiSignalLevelResult() -> Setting the wifi Level
     **/
    @Override
    protected void onResume() {
        super.onResume();
        if(getApplicationContext()!=null) {
            SharedPreference cookies = new SharedPreference(getApplicationContext());
            if(cookies.retrievPreviewPageTabSelection().equals("real-tab")) {
                showLayout(1);
            } else if (cookies.retrievPreviewPageTabSelection().equals("message-tab")){
                showLayout(3);
            }
        }
        overridePendingTransition(0, 0);
        if (!isHomePress) {
            loaderDialog.setMessage();
            presenter.loginDev();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (int i = 0; i < chnCount && i < playViews.length; ++i) {
            presenter.stopMonitor(i);
        }

        isFirstGetVideoStream = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (int i = 0; i < chnCount && i < playViews.length; ++i) {
            presenter.destroyMonitor(i);
        }

        if (screenOrientationManager != null) {
            screenOrientationManager.release(this);
        }

        //如果是低功耗设备，退出预览的时候需要发起休眠命令
        if (DevDataCenter.getInstance().isLowPowerDev(presenter.getDevId())) {
            IDRManager.sleepNow(this, presenter.getDevId());
        }

        presenter.release();

        if (mHomeClickReceiver != null) {
            //unregisterReceiver(mHomeClickReceiver);
        }
        if(handler!=null){
            handler.removeCallbacks(null);
            handler =  null;
        }

    }

    @Override
    public DevMonitorPresenter getPresenter() {
        return new DevMonitorPresenter(this);
    }

    /**
     * 登录回调
     *
     * @param isSuccess
     * @param errorId
     */
    @Override
    public void onLoginResult(boolean isSuccess, int errorId) {
        if (isSuccess) {
            for (int i = 0; i < chnCount && i < playViews.length; ++i) {
                presenter.startMonitor(i);
            }
        } else {
            //密码错误后，会弹出密码输入框，需输入正确的密码重新登录
            if (errorId == EFUN_ERROR.EE_DVR_PASSWORD_NOT_VALID || errorId == EE_DVR_ACCOUNT_PWD_NOT_VALID) {
                XMDevInfo devInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
                //参考下方弹出密码输入框 输入正确的密码，如果不参加以下的弹框，可以按照以下方式操作：
                //第一步让用户输入正确密码，拿到正确密码后，直接调用 FunSDK.DevSetLocalPwd(devId, userName, passWord); 其中 devId是设备序列号、userName是设备登录名（默认是admin），password是要传入的正确密码
                //第二步再次调用 presenter.loginDev();
                // To reference the pop-up password input box below, if you don't want to participate in the pop-up dialog, you can follow the steps below:
                // Step 1: Ask the user to enter the correct password. Once you have the correct password, directly call FunSDK.DevSetLocalPwd(devId, userName, passWord), where devId is the device serial number, userName is the device login name (default is admin), and passWord is the correct password you want to pass.
                // Step 2: Call presenter.loginDev() again.
                XMPromptDlg.onShowPasswordErrorDialog(this, devInfo.getSdbDevInfo(), 0, new PwdErrorManager.OnRepeatSendMsgListener() {
                    @Override
                    public void onSendMsg(int msgId) {
                        loaderDialog.setMessage();
                        presenter.loginDev();
                    }
                });
            } else if (errorId < 0) {
                //showToast(getString(R.string.open_video_f) + errorId, Toast.LENGTH_LONG);
                showToast(getString(R.string.open_video_f), Toast.LENGTH_LONG);
                recreate();
            }
        }
    }

    /**
     * 获取设备能力集结果回调
     *
     * @param systemFunctionBean 能力集
     * @param errorId            错误码
     */
    @Override
    public void onGetDevAbilityResult(SystemFunctionBean systemFunctionBean, int errorId) {
        this.systemFunctionBean = systemFunctionBean;
        monitorFunList.clear();
        loaderDialog.dismiss();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_VOICE);
        hashMap.put("itemName", getString(R.string.device_setup_encode_audio));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_CAPTURE);
        hashMap.put("itemName", getString(R.string.capture));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_RECORD);
        hashMap.put("itemName", getString(R.string.cut_video));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_PTZ);
        hashMap.put("itemName", getString(R.string.ptz_ctrl));
        monitorFunList.add(hashMap);

        if (systemFunctionBean != null) {
            //是否支持云台校正
            if (systemFunctionBean.OtherFunction.SupportPtzAutoAdjust) {
                hashMap = new HashMap<>();
                hashMap.put("itemId", FUN_PTZ_CALIBRATION);
                hashMap.put("itemName", getString(R.string.pzt_calibration));
                monitorFunList.add(hashMap);
            }
        }

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_INTERCOM);
        hashMap.put("itemName", getString(R.string.one_way_intercom));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_PLAYBACK);
        hashMap.put("itemName", getString(R.string.playback));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_CHANGE_STREAM);
        hashMap.put("itemName", getString(R.string.main_stream));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_PRESET);
        hashMap.put("itemName", getString(R.string.preset));
        monitorFunList.add(hashMap);

        if (systemFunctionBean != null) {
            //是否支持巡航
            if (systemFunctionBean.OtherFunction.SupportPTZTour) {
                hashMap = new HashMap<>();
                hashMap.put("itemId", FUN_CRUISE);
                hashMap.put("itemName", getString(R.string.cruise));
                monitorFunList.add(hashMap);
            }
        }

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_LANDSCAPE);
        hashMap.put("itemName", getString(R.string.full_stream));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_FULL_STREAM);
        hashMap.put("itemName", getString(R.string.video_fit_center));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_LAN_ALARM);
        hashMap.put("itemName", getString(R.string.start_lan_alarm));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_DEV_PIC);
        hashMap.put("itemName", getString(R.string.search_dev_pic));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_DEV_CAPTURE_SAVE);
        hashMap.put("itemName", getString(R.string.capture_pic_from_dev_and_save));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_DEV_CAPTURE_TO_APP);
        hashMap.put("itemName", getString(R.string.capture_pic_from_dev_and_to_app));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_REAL_PLAY);
        hashMap.put("itemName", getString(R.string.realtime_play));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_SIMPLE_DATA);
        hashMap.put("itemName", getString(R.string.other_config));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_VIDEO_ROTATE);
        hashMap.put("itemName", getString(R.string.video_flip));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_FEET);
        hashMap.put("itemName", getString(R.string.feeding));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_IRCUT);
        hashMap.put("itemName", getString(R.string.ircut));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_RESET);
        hashMap.put("itemName", getString(R.string.factory_reset));
        monitorFunList.add(hashMap);


        if (systemFunctionBean != null) {
            //是否支持指哪看那和相机联动配置功能
            if (systemFunctionBean.OtherFunction.SupportGunBallTwoSensorPtzLocate) {
                hashMap = new HashMap<>();
                hashMap.put("itemId", FUN_POINT);
                hashMap.put("itemName", getString(R.string.look_where_you_point));
                monitorFunList.add(hashMap);

                hashMap = new HashMap<>();
                hashMap.put("itemId", FUN_CAMERA_LINK);
                hashMap.put("itemName", getString(R.string.camera_link_set));
                monitorFunList.add(hashMap);
            }

            //是否支持手动警报功能
            if (systemFunctionBean.AlarmFunction.ManuIntellAlertAlarm) {
                hashMap = new HashMap<>();
                hashMap.put("itemId", FUN_MANUAL_ALARM);
                hashMap.put("itemName", getString(R.string.manual_alarm));
                monitorFunList.add(hashMap);
            }

            //是否支持声光警戒功能
            if (getSupportLightType(systemFunctionBean) != NO_LIGHT_CAMERA) {
                hashMap = new HashMap<>();
                hashMap.put("itemId", FUN_ALARM_BY_VOICE_LIGHT);
                hashMap.put("itemName", getString(R.string.alarm_by_voice_light));
                monitorFunList.add(hashMap);
            }

            //是否支持移动追踪
            if (systemFunctionBean.AlarmFunction.MotionHumanDection) {
                hashMap = new HashMap<>();
                hashMap.put("itemId", FUN_DETECT_TRACK);
                hashMap.put("itemName", getString(R.string.detect_track));
                monitorFunList.add(hashMap);
            }

            hashMap = new HashMap<>();
            hashMap.put("itemId", 27);
            hashMap.put("itemName", "联系家人(视频通话)");
            monitorFunList.add(hashMap);

            //将设备是否支持双目或者三目设备端变倍能力保存到本地
            SPUtil.getInstance(DevMonitorActivity.this).setSettingParam(SUPPORT_SCALE_TWO_LENS + presenter.getDevId(), systemFunctionBean.OtherFunction.SupportScaleTwoLens);
            SPUtil.getInstance(DevMonitorActivity.this).setSettingParam(SUPPORT_SCALE_THREE_LENS + presenter.getDevId(), systemFunctionBean.OtherFunction.SupportScaleThreeLens);

            //将双目或者三目设备 是否需要APP支持变倍的能力保存到本地
            SPUtil.getInstance(DevMonitorActivity.this).setSettingParam(MULTI_LENS_TWO_SENSOR + presenter.getDevId(), systemFunctionBean.OtherFunction.MultiLensTwoSensor);
            SPUtil.getInstance(DevMonitorActivity.this).setSettingParam(MULTI_LENS_THREE_SENSOR + presenter.getDevId(), systemFunctionBean.OtherFunction.MultiLensThreeSensor);
            //如果设备支持多目变倍功能 需要显示变倍控件
            if (systemFunctionBean.OtherFunction.SupportScaleThreeLens || systemFunctionBean.OtherFunction.SupportScaleTwoLens) {
                sbVideoScale.setVisibility(VISIBLE);
            }
        } else {
            showToast(getString(R.string.get_dev_ability_failed), Toast.LENGTH_LONG);
        }

        //APP软件变倍功能
        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_SHOW_APP_ZOOMING);
        hashMap.put("itemName", getString(R.string.show_app_zooming));
        monitorFunList.add(hashMap);

        //APP软件实现多目效果
        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_APP_OBJ_EFFECT);
        hashMap.put("itemName", getString(R.string.app_obj_effect));
        monitorFunList.add(hashMap);
    }


    //不支持灯光
    public static final int NO_LIGHT_CAMERA = -1;
    //支持双光
    public static final int DOUBLE_LIGHT_CAMERA = 0;
    //支持双光枪机
    public static final int DOUBLE_LIGHT_BOX_CAMERA = 1;
    //支持音乐灯
    public static final int MUSIC_LIGHT_CAMERA = 2;
    //支持庭院双光灯
    public static final int GARDEN_DOUBLE_LIGHT_CAMERA = 3;
    //支持白光灯
    public static final int WHITE_LIGHT_CAMERA = 4;
    //支持低功耗设备灯光能力 / 支持单品智能警戒
    public static final int LOW_POWER_WHITE_LIGHT_CAMERA = 5;

    /**
     * 支持的灯光类型
     */
    private int getSupportLightType(SystemFunctionBean systemFunctionBean) {
        if (systemFunctionBean == null) {
            return NO_LIGHT_CAMERA;
        }

        if (systemFunctionBean.OtherFunction.SupportCameraWhiteLight) {
            Log.d(TAG, "支持基础白光灯");
            if (systemFunctionBean.OtherFunction.SupportDoubleLightBulb) {
                Log.d(TAG, "支持双光      " + presenter.getDevId());
                return DOUBLE_LIGHT_CAMERA;
            } else if (systemFunctionBean.OtherFunction.SupportDoubleLightBoxCamera) {
                Log.d(TAG, "支持双光枪机   " + presenter.getDevId());
                return DOUBLE_LIGHT_BOX_CAMERA;
            } else if (systemFunctionBean.OtherFunction.SupportMusicLightBulb) {
                Log.d(TAG, "支持音乐灯     " + presenter.getDevId());
                return MUSIC_LIGHT_CAMERA;
            } else if (systemFunctionBean.OtherFunction.SupportBoxCameraBulb) {
                Log.d(TAG, "支持庭院双光灯     " + presenter.getDevId());
                return GARDEN_DOUBLE_LIGHT_CAMERA;
            } else {
                Log.d(TAG, "支持白光灯     " + presenter.getDevId());
                return WHITE_LIGHT_CAMERA;
            }
        } else if (systemFunctionBean.OtherFunction.LP4GSupportDoubleLightSwitch || systemFunctionBean.AlarmFunction.IntellAlertAlarm || systemFunctionBean.OtherFunction.SupportLowPowerDoubleLightToLightingSwitch) {
            //支持低功耗设备灯光能力 / 支持单品智能警戒
            Log.d(TAG, "支持低功耗设备灯光能力 / 支持单品智能警戒 / 庭院灯照明开关   " + presenter.getDevId());
            return LOW_POWER_WHITE_LIGHT_CAMERA;

        }
        return NO_LIGHT_CAMERA;
    }


    /**
     * 播放状态回调
     *
     * @param chnId   通道号
     * @param state   状态
     * @param errorId 错误码
     */
    @Override
    public void onPlayState(int chnId, int state, int errorId) {
        if (state == E_STATE_PlAY) {
            if (errorId == EFUN_ERROR.EE_DVR_PASSWORD_NOT_VALID) {
                XMDevInfo devInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
                XMPromptDlg.onShowPasswordErrorDialog(this, devInfo.getSdbDevInfo(), 0, new PwdErrorManager.OnRepeatSendMsgListener() {
                    @Override
                    public void onSendMsg(int msgId) {
                        loaderDialog.setMessage();
                        presenter.startMonitor(chnId);
                    }
                });
            } else if (errorId == EFUN_ERROR.EE_DVR_LOGIN_USER_NOEXIST) {
                XMDevInfo devInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
                XMPromptDlg.onShowPasswordErrorDialog(this, devInfo.getSdbDevInfo(), 0, getString(R.string.input_username_password), INPUT_TYPE_DEV_USER_PWD, true, new PwdErrorManager.OnRepeatSendMsgListener() {
                    @Override
                    public void onSendMsg(int msgId) {
                        loaderDialog.setMessage();
                        presenter.startMonitor(chnId);
                    }
                }, false);
            } else if (errorId < 0) {
                showToast(getString(R.string.open_video_f) , Toast.LENGTH_LONG);
            }
        }

        if (state == E_STATE_SAVE_RECORD_FILE_S) {
            showToast(getString(R.string.record_s), Toast.LENGTH_LONG);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else{
                ((SDKDemoApplication)getApplicationContext()).makeFilesVisibleInGallery();
            }


        } else if (state == E_STATE_SAVE_PIC_FILE_S) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else{
                ((SDKDemoApplication)getApplicationContext()).makeFilesVisibleInGallery();
            }
            showToast(getString(R.string.capture_s), Toast.LENGTH_LONG);
        } else if (state == E_STATE_MEDIA_DISCONNECT) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else{
                ((SDKDemoApplication)getApplicationContext()).makeFilesVisibleInGallery();
            }
            showToast(getString(R.string.media_disconnect), Toast.LENGTH_LONG);

        }
    }

    @Override
    public void onVideoBufferEnd(PlayerAttribute attribute, MsgContent ex) {
        //如果是假多目的话，在码流上来后 需要进行画面分割处理
        if (isShowAppMoreScreen) {
            playWndLayout.showSingleWnd(presenter.getChnId(), false);
            presenter.splitScreen(playViews[1]);
        }

        mainVideoStarted = true;

        try {
            monitorFunAdapter.changeBtnState(FUN_CHANGE_STREAM, presenter.getStreamType(attribute.getChnnel()) == SDKCONST.StreamType.Main);
            if (!isLoadCompleteFirstTime && isAOVDevice) {
                onFeatureClicked(FUN_APP_OBJ_EFFECT+"");
            } else{
                ViewGroup.LayoutParams playWinLayoutParams = playWndLayout.getLayoutParams();
                wndInnerRVOriginalHeight = playWinLayoutParams.height;
            }


            isLoadCompleteFirstTime = true;
        }catch ( Exception e){
            e.printStackTrace();
            recreate();
        }

    }

    @Override
    public void onUpdateFaceFrameView(final FaceFeature[] faceFeatures, final int width, final int height) {
    }

    @Override
    public void onLightCtrlResult(String jsonData) {
        XMPromptDlg.onShow(this, jsonData, null);
    }

    /**
     * 电量、SD卡存储等状态回调
     * devStorageStatus 0:没有存储 1:有存储 2:存储设备插入 -2:存储设备未知
     * electable 显示是否在充电 0:未充电; 1:正在充电;2:充电满；3:未知
     * percent 电量百分比
     * level 电量等级  如果电量状态是处于充电中，那这个电量可能会不准，也可能会返回-1，所以需要判断只有电量未充电的情况下在根据显示实际的电量情况
     *
     * @param electCapacityBean
     */
    @Override
    public void onElectCapacityResult(ElectCapacityBean electCapacityBean) {
        if (tvBatteryState.getVisibility() == View.GONE) {
            tvBatteryState.setVisibility(VISIBLE);
        }
        tvBatteryState.setVisibility(View.GONE);

        /*batteryIndicator.setChargeLevel(electCapacityBean.percent);

        if (electCapacityBean.electable == 0) {
            batteryIndicator.setCharging(false);
        } else if(electCapacityBean.electable == 1) {
            batteryIndicator.setCharging(true);
        }  else if(electCapacityBean.electable == 2){
            batteryIndicator.setCharging(false);
        } else {
            batteryIndicator.setChargeLevel(5);
            batteryIndicator.setCharging(false);
        }*/

        //batteryDrawable.setBatteryLevel(electCapacityBean.percent);
        updateBatteryColor(electCapacityBean.percent, electCapacityBean.electable);

        //tvBatteryState.setText(String.format(getString(R.string.battery_state_show), electCapacityBean.percent, electCapacityBean.devStorageStatus, electCapacityBean.electable));
    }
    private void updateBatteryColor( int percentage, int chargeState) {
        boolean isCharging = false;
        if (chargeState == 0) {
            isCharging = false;
        } else if(chargeState == 1) {
            isCharging = true;
        }  else if(chargeState == 2) {
            isCharging = false;
        } else {
            isCharging = false;
        }
            batteryProgressBar.setProgress(percentage);

            if (isCharging) {
                // Show charging icon
                batteryProgressBar.setProgressDrawable(getDrawable(R.drawable.battery_indicator_charging));
            } else {
                // Hide charging icon
                batteryProgressBar.setProgressDrawable(getDrawable(R.drawable.battery_indicator));
                // Get the progress layer from the drawable
                LayerDrawable layerDrawable = (LayerDrawable) batteryProgressBar.getProgressDrawable();
                Drawable progressLayer = layerDrawable.findDrawableByLayerId(android.R.id.progress);
                // Change color based on battery percentage
                // percentage = 10;
                if (percentage > 0 &&  percentage <= 20) {
                    Log.d("under 10 " ,"percentage > "  +percentage);
                    progressLayer.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                } else if (percentage <= 40) {
                    progressLayer.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                } else if (percentage > 40) {
                    progressLayer.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                    //progressLayer.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                } else{
                    Log.d("else is working under 10 " ,"percentage > "  +percentage);
                    progressLayer.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                }

                if(percentage < 20 ) {
                    showLowBatterDialog();
                    showLowBatterToast();
                }

            }
        batteryProgressBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.bt_txtv).setVisibility(VISIBLE);
                findViewById(R.id.bt_txtv).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.bt_txtv).setVisibility(View.GONE);
                    }
                }, 5000);
            }
        });



        /*LayerDrawable layerDrawable = (LayerDrawable) batteryProgressBar.getProgressDrawable();
            Drawable progressLayer = layerDrawable.findDrawableByLayerId(android.R.id.progress);
            if (percentage > 0 && percentage < 20) {
                progressLayer.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (percentage > 21 && percentage < 50) {
                progressLayer.setColorFilter(Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                progressLayer.setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
            }*/


    }

    /**
     * WiFi信号回调
     *
     * @param wifiRouteInfo
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onWiFiSignalLevelResult(WifiRouteInfo wifiRouteInfo) {
        if (wifiRouteInfo != null) {
            if (tvWiFiState.getVisibility() == View.GONE) {
                tvWiFiState.setVisibility(VISIBLE);
            }
            tvWiFiState.setVisibility(View.GONE);
            tvWiFiState.setText(String.format(getString(R.string.wifi_state_show), wifiRouteInfo.getSignalLevel()));

            ImageView wifiSignalView = findViewById(R.id.wifiSignalView);

            // Simulate signal strength (Weak, Medium, Strong)
            int signalStrength = wifiRouteInfo.getSignalLevel(); // Custom method to get WiFi signal strength

            if (signalStrength < 2) {
                wifiSignalView.setImageDrawable(getDrawable(R.drawable.wifi_fair));
            } else if (signalStrength < 4) {
                wifiSignalView.setImageDrawable(getDrawable(R.drawable.wifi_good));
            } else {
                wifiSignalView.setImageDrawable(getDrawable(R.drawable.wifi_strong));
            }


        }
    }

    /**
     * 视频码流回调
     *
     * @param rate
     */
    @Override
    public void onVideoRateResult(String rate) {
        if (tvRate.getVisibility() == View.GONE) {
            tvRate.setVisibility(VISIBLE);
        }

        Log.d("onVideoRateResult > "  ," value  > " +rate);

        tvRate.setText(rate);
    }

    /**
     * 喂食结果回调
     *
     * @param isSuccess 喂食是否成功
     * @param values
     * @param errorMsg
     */
    @Override
    public void onFeedResult(boolean isSuccess, byte[] values, String errorMsg) {
        ToastUtils.showLong(isSuccess ? getString(R.string.libfunsdk_operation_success) : getString(R.string.libfunsdk_operation_failed) );
    }

    @Override
    public void changeChannel(int chnId) {
        playWndLayout.changeChannel(chnId);
    }

    /**
     * 视频缩放结果回调
     *
     * @param imgScale
     */
    @Override
    public void onVideoScaleResult(float imgScale) {
        dealWithVideoScale(imgScale);
    }

    private void dealWithVideoScale(float imgScale) {
        if (sbVideoScale.getVisibility() == VISIBLE) {
            //推荐真实倍数3倍，显示放大倍数6倍，最好可以整除，显示倍数大于真实倍数
            int maxTimes = 3;
            int maxTimesShow = 6;
            float scale = (imgScale - 1) * maxTimesShow / maxTimes;
            sbVideoScale.setProgress((int) (scale * sbVideoScale.getSmallSubCount()));
            sensorChangePresenter.setCurScale(scale);
        }
    }

    @Override
    public boolean isSingleWnd() {
        return playWndLayout.isSingleWnd();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 获取当前镜头Sensor信息
     *
     * @param devId          设备序列号
     * @param chnId          设备通道号
     * @param sensorInfoBean Sensor信息
     */
    @Override
    public void onGetSensorResult(String devId, int chnId, SensorInfoBean sensorInfoBean) {
        if (sensorInfoBean.getMaxTimes() > 0) {
            if (sbVideoScale.getSubCount() != sensorInfoBean.getMaxTimes() - 1) {
                sbVideoScale.setSubCount(sensorInfoBean.getMaxTimes() - 1);
            }
        } else {
            if (sensorChangePresenter.getSensorCount() == 2) {
                if (sbVideoScale.getSubCount() != 7) {
                    sbVideoScale.setSubCount(7);
                }
            } else if (sensorChangePresenter.getSensorCount() == 3) {
                if (sbVideoScale.getSubCount() != 19) {
                    sbVideoScale.setSubCount(19);
                }
            }
        }

        if (sbVideoScale.getSubCount() < 7) {
            sbVideoScale.setSmallSubCount(10);
            sensorChangePresenter.setSmallSubCount(10);
        } else {
            sbVideoScale.setSmallSubCount(5);
            sensorChangePresenter.setSmallSubCount(5);
        }

        SPUtil.getInstance(this).setSettingParam(SENSOR_MAX_TIMES + devId, sbVideoScale.getSubCount() + 1);
        boolean scaleTwo = SPUtil.getInstance(this).getSettingParam(SUPPORT_SCALE_TWO_LENS + devId, false);
        boolean scaleThree = SPUtil.getInstance(this).getSettingParam(SUPPORT_SCALE_THREE_LENS + devId, false);
        if (scaleTwo || scaleThree) {
            int curSensorId = 0;
            int streamType = presenter.getCurSelMonitorManager(presenter.getChnId()).getStreamType();
            if (streamType == SDKCONST.StreamType.Main) {
                curSensorId = sensorInfoBean.getSensorMain();
            } else if (streamType == SDKCONST.StreamType.Extra) {
                curSensorId = sensorInfoBean.getSensorExtra();
            }
            sensorChangePresenter.setCurSensorId(curSensorId);
            if (isFirstGetVideoStream && streamType != sensorInfoBean.getScaleStream()) {
                sensorChangePresenter.ctrlVideoScale(devId, streamType, sensorChangePresenter.getProgress(), -1, 2);
                isFirstGetVideoStream = false;
            }
        }
        updateSensorUIByScale(devId);
    }

    @Override
    public void onSetSensorResult(String devId, boolean isSuccess) {
        if (isSuccess) {
            if (isDelayChangeStream) {
                presenter.changeStream(presenter.getChnId());
                isDelayChangeStream = false;
            }
        }
    }

    @Override
    public void onInitSensor(String devId, ArrayList<Float> sensorFLs) {
        updateSensorUIByScale(devId);
    }

    @Override
    public void onSetScaleResult(String devId, float scale, boolean isSuccess) {
        if (isDelayChangeStream) {
            presenter.changeStream(presenter.getChnId());
            isDelayChangeStream = false;
        }
    }

    /**
     * 根据缩放比例更新UI
     *
     * @param devId
     */
    private void updateSensorUIByScale(String devId) {
        sbVideoScale.setProgress(sensorChangePresenter.getProgress());
        boolean scaleTwo = SPUtil.getInstance(this).getSettingParam(SUPPORT_SCALE_TWO_LENS + devId, false);
        boolean scaleThree = SPUtil.getInstance(this).getSettingParam(SUPPORT_SCALE_THREE_LENS + devId, false);
        if (!scaleTwo && !scaleThree) {
            MonitorManager monitorManager = presenter.getCurSelMonitorManager(presenter.getChnId());
            if (sensorChangePresenter.getScaleList() != null) {
                int curSensorId = sensorChangePresenter.getCurSensorId();
                if (curSensorId > 0) {
                    monitorManager.setMaxScale((sensorChangePresenter.getMinScale(curSensorId) + 1) / curSensorId);
                } else {
                    monitorManager.setMaxScale(sensorChangePresenter.getMaxScale(curSensorId) + 1);
                }
            }
            monitorManager.setScale(sensorChangePresenter.getCurScale() + 1f);
        }
    }

    @Override
    public void onFrameInfo(PlayerAttribute playerAttribute, SDK_FishEyeFrame fishEyeFrame) {
        //判断是否为多目信息
        if (fishEyeFrame instanceof MultiLensParam) {
            MultiLensParam multiLensParam = (MultiLensParam) fishEyeFrame;
            int curSensorId = 0;
            if (playerAttribute.getStreamType() == SDKCONST.StreamType.Main) {
                curSensorId = multiLensParam.st_1_sensorMain;
            } else if (playerAttribute.getStreamType() == SDKCONST.StreamType.Extra) {
                curSensorId = multiLensParam.st_2_sensorExtra;
            }
            float curScale = multiLensParam.st_4_times;//当前码流信息帧返回的倍数
            sensorChangePresenter.setCurSensorId(curSensorId);
            //monitor里的maxScale需要上层设置，除以curSensorId是为了兼容GLSurfaceView20里计算放大倍数的方法，后面可以再改
            MonitorManager monitorManager = presenter.getMonitorManager(playerAttribute.getDevId());
            if (sensorChangePresenter.getScaleList() != null) {
                if (curSensorId > 0) {
                    monitorManager.setMaxScale((sensorChangePresenter.getMinScale(curSensorId) + 1) / curSensorId);
                } else {
                    monitorManager.setMaxScale(sensorChangePresenter.getMaxScale(curSensorId) + 1);
                }
            }
            //双目设备变倍
            boolean scaleTwo = SPUtil.getInstance(this).getSettingParam(SUPPORT_SCALE_TWO_LENS + playerAttribute.getDevId(), false);
            //三目设备变倍
            boolean scaleThree = SPUtil.getInstance(this).getSettingParam(SUPPORT_SCALE_THREE_LENS + playerAttribute.getDevId(), false);
            int streamType = monitorManager.getStreamType();

            //如果当前的码流类型匹配的话，将当前码流信息帧获取到的倍数保存到本地
            if (multiLensParam.st_3_scaleStream == streamType) {
                SPUtil.getInstance(this).setSettingParam(LAST_CHANGE_SCALE_TIMES + playerAttribute.getDevId(), multiLensParam.st_4_times);
            }

            if (scaleTwo || scaleThree) {
                //如果是首次获取到码流数据或者当前的信息帧中的缩放码流类型和当前码流类型匹配的情况下 进行缩放比例同步
                if (isFirstGetVideoStream || multiLensParam.st_3_scaleStream == streamType) {
                    //回调中控制变倍的码流和预览码流相同时才需要同步进度
                    sensorChangePresenter.setCurScale(curScale);
                    if (!sbVideoScale.isMoving()) {
                        sbVideoScale.setProgress((int) (curScale * sbVideoScale.getSmallSubCount()));
                    }
                    //双目设备，根据画面倍数，改变云台转动速率, 记录当前画面倍数
                    SPUtil.getInstance(this).setSettingParam("IS_TURN_AROUND_SPEED_MULTI" + playerAttribute.getDevId(), curScale);
                }

                //如果是首次获取到码流数据并且当前的码流类型不一致的情况下要进行变倍处理
                if (isFirstGetVideoStream && multiLensParam.st_3_scaleStream != streamType) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            sensorChangePresenter.ctrlVideoScale(playerAttribute.getDevId(), playerAttribute.getStreamType(), sbVideoScale.getProgress(), -1, 1);
                        }
                    });
                }
            }
        }
    }

    /**
     * 实时预览功能列表适配器
     */
    public class MonitorFunAdapter extends RecyclerView.Adapter<MonitorFunAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_monitor_fun, null));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HashMap<String, Object> hashMap = monitorFunList.get(position);
            if (hashMap != null) {
                String itemName = (String) hashMap.get("itemName");
                int itemId = (int) hashMap.get("itemId");
                holder.btnMonitorFun.setText(itemName);
                holder.btnMonitorFun.setTag(itemId);
                if (FUN_FULL_STREAM == itemId) {
                    holder.btnMonitorFun.setSelected(!presenter.isVideoFullScreen(presenter.getChnId()));
                } else {
                    Object result = hashMap.get("itemSel");
                    if (result instanceof Boolean) {
                        holder.btnMonitorFun.setSelected((Boolean) result);
                    } else {
                        holder.btnMonitorFun.setSelected(false);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return monitorFunList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            BtnColorBK btnMonitorFun;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                btnMonitorFun = itemView.findViewById(R.id.btn_monitor_fun);
                btnMonitorFun.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        HashMap<String, Object> hashMap = monitorFunList.get(position);
                        if (hashMap != null) {
                            int itemId = (int) hashMap.get("itemId");
                            boolean isBtnChange = dealWithMonitorFunction(itemId, btnMonitorFun.isSelected());
                            if (isBtnChange) {
                                hashMap.put("itemSel", !btnMonitorFun.isSelected());
                                btnMonitorFun.setSelected(!btnMonitorFun.isSelected());
                            }
                        }
                    }
                });

            }
        }

        public void changeBtnState(int itemId, boolean isSelected) {
            int position = getItemPosition(itemId);
            HashMap itemMap = monitorFunList.get(position);
            BtnColorBK btnMonitorFun = rvMonitorFun.findViewWithTag(itemId);
            if (btnMonitorFun != null) {
                itemMap.put("itemSel", isSelected);
                btnMonitorFun.setSelected(isSelected);
            }
        }

        private int getItemPosition(int itemId) {
            for (int i = 0; i < monitorFunList.size(); ++i) {
                HashMap itemMap = monitorFunList.get(i);
                if ((Integer) itemId == itemMap.get("itemId")) {
                    return i;
                }
            }

            return 0;
        }
    }
    //FUN_APP_OBJ_EFFECT
    private boolean dealWithMonitorFunction(int itemId, boolean isSelected) {
        switch (itemId) {
            case FUN_VOICE://开启和关闭音频
                if (isSelected) {
                    presenter.closeVoice(presenter.getChnId());
                } else {
                    presenter.openVoice(presenter.getChnId());
                }
                return true;
            case FUN_CAPTURE://视频抓图
                presenter.capture(presenter.getChnId());
                break;
            case FUN_RECORD://视频剪切
                if (isSelected) {
                    presenter.startRecord(presenter.getChnId());
                } else {
                    presenter.stopRecord(presenter.getChnId());
                }
                return true;
            case FUN_PTZ://云台控制
            {

                PtzView contentLayout = (PtzView) LayoutInflater.from(this).inflate(R.layout.view_ptz, null);
                if (presenter.isOnlyHorizontal() && !presenter.isOnlyVertically()) {
                    //Horizontal display
                    contentLayout.setOnlyHorizontal(true);
                    contentLayout.setNormalBgSrcId(R.mipmap.ic_only_h_ptz_ctrl_nor);
                    contentLayout.setSelectedLeftBgSrcId(R.mipmap.ic_only_h_ptz_ctrl_left);
                    contentLayout.setSelectedRightBgSrcId(R.mipmap.ic_only_h_ptz_ctrl_right);
                } else if (presenter.isOnlyVertically() && !presenter.isOnlyHorizontal()) {
                    //Vertically display
                    contentLayout.setOnlyVertically(true);
                    contentLayout.setNormalBgSrcId(R.mipmap.ic_only_v_ptz_ctrl_nor);
                    contentLayout.setSelectedDownBgSrcId(R.mipmap.ic_only_v_ptz_ctrl_down);
                    contentLayout.setSelectedUpBgSrcId(R.mipmap.ic_only_v_ptz_ctrl_top);
                }

                contentLayout.setOnPtzViewListener(new PtzView.OnPtzViewListener() {
                    @Override
                    public void onPtzDirection(int direction, boolean stop) {
                        //如果是假多目的情况下，通道号默认传0
                        if (isShowAppMoreScreen) {
                            presenter.devicePTZControl(0, direction, 4, stop);
                        } else {
                            presenter.devicePTZControl(presenter.getChnId(), direction, 4, stop);
                        }
                    }
                });

                showBottomSheet( contentLayout);
                //XMPromptDlg.onShow(this, contentLayout);
                break;
            }
            case FUN_PTZ_CALIBRATION://云台校正
                loaderDialog.setMessage();
                presenter.ptzCalibration();
                break;
            case FUN_INTERCOM: //单向对讲，按下去说话，放开后听到设备端的声音，对话框消失后 对讲结束
            {
                showIntercomBottomSheetDialog();
            }
            break;
            case FUN_PLAYBACK://录像回放
                SharedPreference cookies = new SharedPreference(getApplicationContext());
                int width = wndLayout.getWidth();
                int height = wndLayout.getHeight();
                cookies.saveDevicePreviewHeightandWidth(width, height);
                //turnToActivity(DevRecordActivity.class, new Object[][]{{"devId", presenter.getDevId()}, {"chnId", presenter.getChnId()}, {"height", height}, {"width", width}});

                break;
            case FUN_CHANGE_STREAM://主副码流切换
                // The primary stream is clearer and the secondary stream is blurry
                //如果支持多目镜头变倍切换的，等镜头切换或者变倍后再切换码流
                if (sensorChangePresenter.getSensorCount() > 1 && sbVideoScale.getVisibility() == VISIBLE) {
                    MonitorManager monitorManager = presenter.getCurSelMonitorManager(presenter.getChnId());
                    boolean scaleTwo = SPUtil.getInstance(this).getSettingParam(SUPPORT_SCALE_TWO_LENS + presenter.getDevId(), false);
                    boolean scaleThree = SPUtil.getInstance(this).getSettingParam(SUPPORT_SCALE_THREE_LENS + presenter.getDevId(), false);

                    int afterChangeStreamType = monitorManager.getStreamType() == SDKCONST.StreamType.Main ? SDKCONST.StreamType.Extra : SDKCONST.StreamType.Main;
                    //是否支持设备端变倍
                    int streamIconResource;
                    if (scaleTwo || scaleThree) {
                        streamIconResource = R.drawable.sd_icon;
                        sensorChangePresenter.ctrlVideoScale(presenter.getDevId(), afterChangeStreamType, sbVideoScale.getProgress(), -1, 1);
                    } else {
                        streamIconResource = R.drawable.fhd_icon;
                        sensorChangePresenter.switchSensor(presenter.getDevId(), -1, afterChangeStreamType, -1);
                    }
                    Glide.with(getApplicationContext()).load(streamIconResource).into(sdImag);

                    isDelayChangeStream = true;
                } else {
                    presenter.changeStream(presenter.getChnId());
                }
                break;
            case FUN_PRESET://预置点
                presetViewHolder.showPresetDlg = XMPromptDlg.onShow(getContext(), presetViewHolder.presetLayout, (int) (screenWidth * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT, true, null);
                break;
            case FUN_CRUISE://巡航
            {
                loaderDialog.setMessage();
                presenter.getTour(presenter.getChnId());
                break;
            }
            case FUN_LANDSCAPE://全屏
                changePlayViewSize();
                screenOrientationManager.landscapeScreen(this, true);
                break;
            case FUN_FULL_STREAM://满屏显示 视频画面按比例显示
                if (isSelected) {
                    presenter.setVideoFullScreen(presenter.getChnId(), true);
                } else {
                    presenter.setVideoFullScreen(presenter.getChnId(), false);
                }
                return true;
            case FUN_LAN_ALARM: //局域网报警
                turnToActivity(ActivityGuideDeviceLanAlarm.class);
                break;
            case FUN_DEV_PIC://设备端图片
                turnToActivity(DevPictureActivity.class);
                break;
            case FUN_DEV_CAPTURE_SAVE://设备端抓图并保存到设备端本地存储，不会将图片回传给APP
                XMPromptDlg.onShow(this, getString(R.string.not_all_dev_support), new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.capturePicFromDevAndSave(presenter.getChnId());
                    }
                });
                break;
            case FUN_DEV_CAPTURE_TO_APP://设备端抓图并回传给APP，但是不会将图片保存到设备本地
                presenter.capturePicFromDevAndToApp(presenter.getChnId());
                break;
            case FUN_REAL_PLAY://实时预览实时性（局域网IP访问才生效）
                loaderDialog.setMessage();
                presenter.setRealTimeEnable(isSelected);

                for (int k = 0; k < chnCount && k < playViews.length; ++k) {
                    presenter.stopMonitor(k);
                }

                isFirstGetVideoStream = true;
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < chnCount && i < playViews.length; ++i) {
                            presenter.startMonitor(i);
                        }
                        loaderDialog.dismiss();
                    }
                }, 1000);
                return true;
            case FUN_SIMPLE_DATA://简单数据交互
                turnToActivity(DevSimpleConfigActivity.class);
                break;
            case FUN_VIDEO_ROTATE://视频画面旋转
                presenter.setVideoFlip(presenter.getChnId());
                break;
            case FUN_FEET://喂食，默认喂食3份
                XMPromptDlg.onShowEditDialog(this, getString(R.string.food_portions), "3", new EditDialog.OnEditContentListener() {
                    @Override
                    public void onResult(String content) {
                        if (StringUtils.isStringNULL(content)) {
                            ToastUtils.showLong(R.string.input_food_portions);
                        } else {
                            XMPromptDlg.onShow(DevMonitorActivity.this, getString(R.string.please_sel_feet_type), getString(R.string.feed_the_dog), getString(R.string.feed_the_fish), new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    presenter.feedTheDog(Integer.parseInt(content));
                                }
                            }, new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    presenter.feedTheFish(Integer.parseInt(content));
                                }
                            });

                        }
                    }
                });
                break;
            case FUN_IRCUT://irCut反序
            {
                View layout = LayoutInflater.from(this).inflate(R.layout.view_ircut, null);
                ListSelectItem lsiIrCut = layout.findViewById(R.id.lsi_ircut);
                lsiIrCut.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (presenter.getCameraParamBean() != null) {
                            boolean open = lsiIrCut.getSwitchState() == SDKCONST.Switch.Open;
                            lsiIrCut.setSwitchState(!open ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
                            presenter.setIrCutInfo(!open, new DeviceManager.OnDevManagerListener() {
                                @Override
                                public void onSuccess(String s, int i, Object o) {

                                }

                                @Override
                                public void onFailed(String s, int i, String s1, int i1) {

                                }
                            });
                        }
                    }
                });

                XMPromptDlg.onShow(this, layout, true, new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });

                if (presenter.getCameraParamBean() != null) {
                    lsiIrCut.setSwitchState(presenter.getCameraParamBean().IrcutSwap);
                } else {
                    presenter.getIrCutInfo();
                    showToast("获取irCut配置失败，请重试！", Toast.LENGTH_LONG);
                }

            }
            break;
            case FUN_RESET://恢复出厂设置
                XMPromptDlg.onShow(DevMonitorActivity.this, getString(R.string.factory_reset), getString(R.string.only_factory_reset), getString(R.string.factory_reset_and_delete_dev), new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.factoryReset(new DeviceManager.OnDevManagerListener() {
                            @Override
                            public void onSuccess(String devId, int operationType, Object result) {
                                showToast(getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG);
                            }

                            @Override
                            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                                showToast(getString(R.string.libfunsdk_operation_failed) , Toast.LENGTH_LONG);
                            }
                        });
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.factoryReset(new DeviceManager.OnDevManagerListener() {
                            @Override
                            public void onSuccess(String devId, int operationType, Object result) {
                                showToast(getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG);
                                //未使用AccountManager(包括XMAccountManager或LocalAccountManager)登录（包括账号登录和本地临时登录），只能将设备信息临时缓存，重启应用后无法查到设备信息。
                                if (DevDataCenter.getInstance().getLoginType() == LOGIN_NONE) {
                                    DevDataCenter.getInstance().removeDev(devId);
                                    showToast(getString(R.string.delete_s), Toast.LENGTH_LONG);
                                    finish();
                                } else {
                                    AccountManager.getInstance().deleteDev(presenter.getDevId(), new BaseAccountManager.OnAccountManagerListener() {
                                        @Override
                                        public void onSuccess(int msgId) {
                                            showToast(getString(R.string.delete_s), Toast.LENGTH_LONG);
                                            finish();
                                        }

                                        @Override
                                        public void onFailed(int msgId, int errorId) {
                                            showToast(getString(R.string.delete_f), Toast.LENGTH_LONG);
                                        }

                                        @Override
                                        public void onFunSDKResult(Message message, MsgContent msgContent) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                                showToast(getString(R.string.libfunsdk_operation_failed) , Toast.LENGTH_LONG);
                            }
                        });
                    }
                });
                break;
            case FUN_POINT://指哪看那
                //如果支持指哪看哪的功能，需要将预览画布的触摸事件设置成false，传递给上层控件
                //If you want to support any viewing function, you need to set the touch event of the preview
                // canvas to false, pass it to the top layer control.
                if (!isShowAppMoreScreen) {
                    isOpenPointPtz = !isOpenPointPtz;
                    if (isOpenPointPtz) {
                        XMPromptDlg.onShow(DevMonitorActivity.this, getString(R.string.camera_point_ptz_tips), null);
                    }

                    changePlayViewSize();
                } else {
                    XMPromptDlg.onShow(this, getString(R.string.is_app_more_screen_not_support_this_function), null);
                }
                return true;
            case FUN_MANUAL_ALARM://手动警戒
                boolean isManualAlarmOpen = presenter.changeManualAlarmSwitch();
                monitorFunAdapter.changeBtnState(itemId, isManualAlarmOpen);
                break;
            case FUN_ALARM_BY_VOICE_LIGHT: //声光报警
                switch (getSupportLightType(systemFunctionBean)) {
                    case DOUBLE_LIGHT_BOX_CAMERA:
                        turnToActivity(DoubleLightBoxActivity.class);
                        break;
                    //支持低功耗设备灯光能力 / 支持单品智能警戒
                    case LOW_POWER_WHITE_LIGHT_CAMERA:
                        Intent intent = new Intent(DevMonitorActivity.this, WhiteLightActivity.class);
                        intent.putExtra("devId", presenter.getDevId());
                        intent.putExtra("supportLightSwitch", false);
                        startActivity(intent);
                        break;

                    //支持白光灯
                    case WHITE_LIGHT_CAMERA:
                        Intent whiteLightIntent = new Intent(DevMonitorActivity.this, WhiteLightActivity.class);
                        whiteLightIntent.putExtra("devId", presenter.getDevId());
                        whiteLightIntent.putExtra("supportLightSwitch", true);
                        startActivity(whiteLightIntent);
                        break;
                    //支持双光
                    case DOUBLE_LIGHT_CAMERA:
                        Intent doubleLightIntent = new Intent(DevMonitorActivity.this, DoubleLightActivity.class);
                        doubleLightIntent.putExtra("devId", presenter.getDevId());
                        doubleLightIntent.putExtra("supportLightSwitch", true);
                        startActivity(doubleLightIntent);
                        break;
                    //支持音乐灯
                    case MUSIC_LIGHT_CAMERA:
                        Intent musicLightIntent = new Intent(DevMonitorActivity.this, MusicLightActivity.class);
                        musicLightIntent.putExtra("devId", presenter.getDevId());
                        musicLightIntent.putExtra("supportLightSwitch", true);
                        startActivity(musicLightIntent);
                        break;
                    //支持庭院双光灯
                    case GARDEN_DOUBLE_LIGHT_CAMERA:
                        Intent GardenDoubleLightIntent = new Intent(DevMonitorActivity.this, GardenDoubleLightActivity.class);
                        GardenDoubleLightIntent.putExtra("devId", presenter.getDevId());
                        startActivity(GardenDoubleLightIntent);
                        break;
                    default:
                        break;
                }
                break;
            case FUN_CAMERA_LINK://相机联动
                turnToActivity(CameraLinkSetActivity.class);
                break;
            case FUN_DETECT_TRACK://移动追踪
                turnToActivity(DetectTrackActivity.class);
                break;
            case FUN_CONTACT_FAMILY://联系家人(视频通话)
                turnToActivity(VideoIntercomActivity.class);
                break;
            case FUN_SHOW_APP_ZOOMING://APP软变倍
                if (!(presenter.getCurSelMonitorManager(presenter.getChnId()).getSurfaceView() instanceof GLSurfaceView20)) {
                    XMPromptDlg.onShow(this, getString(R.string.this_function_only_glsurfaceview), null);
                    break;
                }

                isShowAPPZooming = !isShowAPPZooming;
                if (isShowAPPZooming) {
                    sbVideoScale.setVisibility(VISIBLE);
                    //推荐真实倍数3倍，显示放大倍数6倍，最好可以整除，显示倍数大于真实倍数
                    int maxTimes = 3;
                    int maxTimesShow = 6;
                    float scale = SPUtil.getInstance(DevMonitorActivity.this).getSettingParam(LAST_CHANGE_SCALE_TIMES + presenter.getDevId(), 0f);
                    sbVideoScale.setProgress((int) (scale * sbVideoScale.getSmallSubCount()));
                    sbVideoScale.setSubCount(maxTimesShow - 1);

                    sensorChangePresenter.setSensorCount(1);
                    sensorChangePresenter.setSensorItemCount(18);
                    sensorChangePresenter.setNeedSendData(false);
                    sensorChangePresenter.setScaleRate((float) maxTimes / maxTimesShow);

                    if (sbVideoScale.getSubCount() < 7) {
                        sbVideoScale.setSmallSubCount(10);
                        sensorChangePresenter.setSmallSubCount(10);
                    } else {
                        sbVideoScale.setSmallSubCount(5);
                        sensorChangePresenter.setSmallSubCount(5);
                    }

                    new Handler(Looper.getMainLooper()).postDelayed(() -> presenter.getCurSelMonitorManager(playWndLayout.getSelectedId()).setScale(scale * maxTimes / maxTimesShow + 1f), 500);
                } else {
                    sbVideoScale.setVisibility(View.GONE);
                }

                return true;
            case FUN_APP_OBJ_EFFECT://APP软件实现多目效果
                if (!isOpenPointPtz) {

                    isShowAppMoreScreen = !isShowAppMoreScreen;
                    presenter.destroyAllMonitor();

                    if (isShowAppMoreScreen) {
                        //2 表示 总共窗口数，1 表示 每行1个窗口，比如（6,2）的话，就是总共6个窗口，每行2个窗口
                        playViews = playWndLayout.setViewCount(2, 1);
                        presenter.initMonitor(0, playViews[0], false);
                        playWndLayout.changeChannel(0);
                        presenter.startMonitor(0);
                    } else {
                        playViews = playWndLayout.setViewCount(1);
                        for (int j = 0; j < chnCount && j < playViews.length; ++j) {
                            presenter.initMonitor(j, playViews[j], true);
                            presenter.startMonitor(j);
                        }

                        monitorFunAdapter.changeBtnState(FUN_SHOW_APP_ZOOMING, false);
                        sbVideoScale.setVisibility(View.GONE);
                        isShowAPPZooming = false;
                    }

                    changePlayViewSize();
                } else {
                    XMPromptDlg.onShow(this, getString(R.string.is_open_point_ptz_not_support_this_function), null);
                }
                return true;
            default:
                Toast.makeText(DevMonitorActivity.this, getString(R.string.not_support_tip), Toast.LENGTH_LONG).show();
                break;
        }

        return false;
    }

    /**
     * 改变播放布局大小
     */
    private void changePlayViewSize() {
        ViewGroup.LayoutParams layoutParams = wndLayout.getLayoutParams();
        ViewGroup.LayoutParams playWinLayoutParams = playWndLayout.getLayoutParams();
        if (isOpenPointPtz || isShowAppMoreScreen) {
            layoutParams.height = XUtils.getScreenWidth(DevMonitorActivity.this) * 18 / 16;
            playWinLayoutParams.height = layoutParams.height;
        } else {
            layoutParams.height = XUtils.getScreenWidth(DevMonitorActivity.this) * 9 / 16;
            playWinLayoutParams.height = layoutParams.height;
        }
        Log.d("changePlayViewSize > ",  "layoutParams.height >"+ layoutParams.height);
        playWndLayout.setLayoutParams(playWinLayoutParams);
        findViewById(R.id.wnd_inner_layout).setLayoutParams(playWinLayoutParams);
        wndLayout.setLayoutParams(layoutParams);
        ViewGroup.LayoutParams layoutParams1 = wndLayout.getLayoutParams();
        Log.d("changePlayViewSize after set > ",  "layoutParams1 .height >"+ layoutParams1.height);
        wndInnerRVOriginalHeight = layoutParams.height;

        presenter.setPlayViewTouchable(0, false);
    }


     void setheightOfMonitorPlayViewAccordingToOrientation(boolean isLandScape) {
         int height;
         ViewGroup.LayoutParams playWinLayoutParams = playWndLayout.getLayoutParams();
         if(isLandScape) {
             height = XUtils.getScreenHeight(DevMonitorActivity.this);
         } else {
             height = wndInnerRVOriginalHeight;
         }

         playWinLayoutParams.height = height;
         playWndLayout.setLayoutParams(playWinLayoutParams);

         findViewById(R.id.wnd_inner_layout).setLayoutParams(playWinLayoutParams);
         ViewGroup.LayoutParams layoutParams1 = wndLayout.getLayoutParams();

         Log.d("changePlayViewSize after set > ",  "layoutParams1 .height >"+ layoutParams1.height);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            titleBar.setVisibility(View.GONE);
            rvMonitorFun.setVisibility(View.GONE);
            portraitWidth = findViewById(R.id.wnd_inner_layout).getWidth();
            portraitHeight = findViewById(R.id.wnd_inner_layout).getHeight();

            ViewGroup.LayoutParams layoutParams = findViewById(R.id.wnd_inner_layout).getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

            findViewById(R.id.wnd_inner_layout).setLayoutParams(layoutParams);
            findViewById(R.id.wnd_inner_layout).requestLayout();
            findViewById(R.id.wnd_inner_layout).invalidate();


            ViewGroup.LayoutParams layoutParams1 = wndLayout.getLayoutParams();
            layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams1.height = ViewGroup.LayoutParams.MATCH_PARENT;

            wndLayout.setLayoutParams(layoutParams1);
            wndLayout.requestLayout();
            wndLayout.invalidate();


            presenter.setPlayViewTouchable(0, true);
            SharedPreference cookies = new SharedPreference(getApplication());
            dName.setText(cookies.retrievDevName());


            //如果是假多目，横屏的时候需要更改画布

            if (isShowAppMoreScreen && isAOVDevice) {
                playViews = playWndLayout.setViewCount(2, 2);
                presenter.changePlayView(playViews);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            //titleBar.setVisibility(VISIBLE);
            //rvMonitorFun.setVisibility(VISIBLE);
            ViewGroup.LayoutParams layoutParams = findViewById(R.id.wnd_inner_layout).getLayoutParams();
            layoutParams.width = portraitWidth;
            layoutParams.height = portraitHeight;

            findViewById(R.id.wnd_inner_layout).setLayoutParams(layoutParams);
            findViewById(R.id.wnd_inner_layout).requestLayout();
            findViewById(R.id.wnd_inner_layout).invalidate();

            ViewGroup.LayoutParams layoutParams1 = wndLayout.getLayoutParams();
            layoutParams1.width = portraitWidth;
            layoutParams1.height = portraitHeight;

            wndLayout.setLayoutParams(layoutParams1);
            wndLayout.requestLayout();
            wndLayout.invalidate();


            //如果支持指哪看哪的功能，需要将预览画布的触摸事件设置成false，传递给上层控件
            DeviceManager.getInstance().getDevAbility(presenter.getDevId(), new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String s, int i, Object o) {
                    if ((boolean) o) {
                        presenter.setPlayViewTouchable(0, false);
                    }
                }

                @Override
                public void onFailed(String s, int i, String s1, int i1) {

                }
            }, "OtherFunction", "SupportGunBallTwoSensorPtzLocate");

            //如果是假多目，横屏的时候需要更改画布
            if (isShowAppMoreScreen && isAOVDevice) {
                playViews = playWndLayout.setViewCount(2, 1);
                presenter.changePlayView(playViews);
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenOrientationManager.portraitScreen(this, true);
            super.onBackPressed();
        } else {
            finish();
            super.onBackPressed();
        }
    }

    class PresetViewHolder {
        /**
         * 预置点操作页面
         */
        View presetLayout;
        /**
         * 键盘数字按钮0-9
         */
        BtnColorBK[] btnNumbers = new BtnColorBK[10];
        /**
         * 添加预置点
         */
        BtnColorBK btnAddPreset;
        /**
         * 调用预置点
         */
        BtnColorBK btnCtrlPreset;
        BtnColorBK btnCancel;
        BtnColorBK btnTurnToPresetList;//跳转到预置点列表
        ImageView ivBack;
        ImageView ivClear;
        EditText etInput;
        Dialog showPresetDlg;

        void initView(View rootLayout) {
            presetLayout = rootLayout;
            btnNumbers[0] = presetLayout.findViewById(R.id.btn_keypad_0);
            btnNumbers[1] = presetLayout.findViewById(R.id.btn_keypad_1);
            btnNumbers[2] = presetLayout.findViewById(R.id.btn_keypad_2);
            btnNumbers[3] = presetLayout.findViewById(R.id.btn_keypad_3);
            btnNumbers[4] = presetLayout.findViewById(R.id.btn_keypad_4);
            btnNumbers[5] = presetLayout.findViewById(R.id.btn_keypad_5);
            btnNumbers[6] = presetLayout.findViewById(R.id.btn_keypad_6);
            btnNumbers[7] = presetLayout.findViewById(R.id.btn_keypad_7);
            btnNumbers[8] = presetLayout.findViewById(R.id.btn_keypad_8);
            btnNumbers[9] = presetLayout.findViewById(R.id.btn_keypad_9);

            for (int i = 0; i < btnNumbers.length; i++) {
                btnNumbers[i].setOnClickListener(numberClickLs);
                btnNumbers[i].setTag(i);
            }

            btnAddPreset = presetLayout.findViewById(R.id.btn_add_preset);
            btnCtrlPreset = presetLayout.findViewById(R.id.btn_ctrl_preset);
            btnTurnToPresetList = presetLayout.findViewById(R.id.btn_turn_to_preset_list);
            ivBack = presetLayout.findViewById(R.id.iv_back);
            ivClear = presetLayout.findViewById(R.id.iv_clear);
            btnCancel = presetLayout.findViewById(R.id.btn_cancel);

            etInput = presetLayout.findViewById(R.id.et_preset_input);
            etInput.setInputType(InputType.TYPE_NULL);
            etInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s != null && !StringUtils.isStringNULL(s.toString())) {
                        int number = Integer.parseInt(s.toString());
                        int index = etInput.getSelectionEnd();
                        if (number == 0 || number > 255) {
                            Editable editable = etInput.getEditableText();
                            editable.delete(index - 1, index);
                            Toast.makeText(getContext(), FunSDK.TS("TR_Preset_Range"), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = etInput.getSelectionEnd();
                    Editable editable = etInput.getEditableText();
                    if (editable.length() > 0 && index > 0) {
                        editable.delete(index - 1, index);
                    }
                }
            });

            ivClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etInput.setText("");
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (showPresetDlg != null) {
                        showPresetDlg.dismiss();
                    }
                }
            });


            btnCtrlPreset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inputContent = etInput.getText().toString();
                    if (!StringUtils.isStringNULL(inputContent)) {
                        presenter.turnToPreset(0, Integer.parseInt(inputContent));
                    } else {
                        Toast.makeText(getContext(), R.string.TR_Input_Preset_Empty_Tip, Toast.LENGTH_LONG).show();
                    }
                }
            });

            btnAddPreset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inputContent = etInput.getText().toString();
                    if (!StringUtils.isStringNULL(inputContent)) {
                        presenter.addPreset(0, Integer.parseInt(inputContent));
                    } else {
                        Toast.makeText(getContext(), R.string.TR_Input_Preset_Empty_Tip, Toast.LENGTH_LONG).show();
                    }
                }
            });

            btnTurnToPresetList.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    turnToActivity(PresetListActivity.class);
                }
            });
        }

        View.OnClickListener numberClickLs = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = etInput.getSelectionEnd();
                Editable editable = etInput.getEditableText();
                editable.insert(index, v.getTag() + "");
            }
        };

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isHomePress) {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);
            if (intent != null) {
                startActivityForResult(intent, 0);
            }
        }
    }

    @Override
    public void onChangeManualAlarmResult(boolean isSuccess, int errorId) {
        if (isSuccess) {
            showToast(getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.libfunsdk_operation_failed) , Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onPtzCalibrationResult(boolean isSuccess, int errorId) {
        if (isSuccess) {
            showToast(getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.libfunsdk_operation_failed) , Toast.LENGTH_LONG);
        }

        loaderDialog.dismiss();
    }

    /**
     * 新建预置点ID取值252、253、254，将这三个预置点作为巡航点
     *
     * @param tourBeans
     * @param errorId
     */
    @Override
    public void onShowTour(List<TourBean> tourBeans, int errorId) {
        loaderDialog.dismiss();
        if (errorId == 0) {
            LinearLayout contentLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_criuse, null);
            BtnColorBK btnOne = contentLayout.findViewById(R.id.btn_keypad_1);
            BtnColorBK btnTwo = contentLayout.findViewById(R.id.btn_keypad_2);
            BtnColorBK btnThree = contentLayout.findViewById(R.id.btn_keypad_3);
            BtnColorBK btnStartCruise = contentLayout.findViewById(R.id.btn_start_cruise);
            BtnColorBK btnStopCruise = contentLayout.findViewById(R.id.btn_stop_cruise);

            if (tourBeans != null && !tourBeans.isEmpty()) {
                for (TourBean tourBean : tourBeans) {
                    if (tourBean != null) {
                        if (tourBean.Id == 252) {
                            btnOne.setText("1");
                        } else if (tourBean.Id == 253) {
                            btnTwo.setText("2");
                        } else if (tourBean.Id == 254) {
                            btnThree.setText("3");
                        }
                    }
                }
            }

            btnOne.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dealWithTourCtrl(252);
                }
            });

            btnOne.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dealWithDeleteTour(252);
                    return true;
                }
            });

            btnTwo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dealWithTourCtrl(253);
                }
            });

            btnTwo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dealWithDeleteTour(253);
                    return true;
                }
            });

            btnThree.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dealWithTourCtrl(254);
                }
            });

            btnThree.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dealWithDeleteTour(254);
                    return true;
                }
            });

            btnStartCruise.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.startTour(0);
                }
            });

            btnStopCruise.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.stopTour(0);
                }
            });

            PtzView ptzView = contentLayout.findViewById(R.id.ptz_view);
            ptzView.setOnPtzViewListener(new PtzView.OnPtzViewListener() {
                @Override
                public void onPtzDirection(int direction, boolean stop) {
                    presenter.devicePTZControl(presenter.getChnId(), direction, 4, stop);
                }
            });


            tourDlg = XMPromptDlg.onShow(this, contentLayout);
        }
    }

    @Override
    public void onTourCtrlResult(boolean isSuccess, int tourCmd, int errorId) {
        if (isSuccess) {
            Toast.makeText(this, getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG).show();
            if (tourCmd == PresetManager.PRESET_ADD_TOUR || tourCmd == PresetManager.PRESET_DELETE_TOUR) {
                tourDlg.dismiss();
                presenter.getTour(0);
            }
        } else {
            Toast.makeText(this, getString(R.string.libfunsdk_operation_failed) , Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTourEndResult() {
        Toast.makeText(this, R.string.tour_end, Toast.LENGTH_LONG).show();
    }

    /**
     * 处理巡航点操作
     */
    private void dealWithTourCtrl(int presetId) {
        List<TourBean> tourBeans = presenter.getTourBeans(0);
        if (tourBeans != null) {
            for (TourBean tourBean : tourBeans) {
                if (tourBean != null) {
                    if (tourBean.Id == presetId) {
                        presenter.turnToPreset(0, tourBean.Id);
                        return;
                    }
                }
            }
        }

        XMPromptDlg.onShow(DevMonitorActivity.this, "是否设置巡航点?", new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.addTour(0, presetId);
            }
        }, null);
    }

    /**
     * 处理删除巡航点操作
     *
     * @param presetId
     */
    private void dealWithDeleteTour(int presetId) {
        List<TourBean> tourBeans = presenter.getTourBeans(0);
        if (tourBeans != null) {
            for (TourBean tourBean : tourBeans) {
                if (tourBean != null) {
                    if (tourBean.Id == presetId) {//判断巡航点是否设置了
                        XMPromptDlg.onShow(DevMonitorActivity.this, "确定删除该巡航点?", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                presenter.deleteTour(0, presetId);
                            }
                        }, null);
                        return;
                    }
                }
            }
        }
    }

    private boolean isHomePress;//按了Home键，退到后台
    /**
     * 监听home键的按下
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loaderDialog.setMessage();
            presenter.loginDev();
        } else {
            finish();
        }
    }


    @Override
    public void onFeatureClicked(String featureName) {
        //Toast.makeText(this, "Feature clicked: " + featureName, Toast.LENGTH_SHORT).show();
        if(featureName.equals("battery")) {

            Intent i= new Intent(this, AovSettingActivity.class);
            i.putExtra("devId",presenter.getDevId());
            startActivity(i);
            overridePendingTransition(0, 0);

        }  else {
            dealWithMonitorFunction(Integer.parseInt(featureName), true);
        }

    }



    private final SparseArray<Fragment> fragments = new SparseArray<>();

    // ViewPager2 Adapter for Fragments
    private static class DeviceViewPagerAdapter extends FragmentStateAdapter {

        public DeviceViewPagerAdapter(@NonNull AppCompatActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            switch (position) {
                case 0:
                    return new RealTimeFragment();
                case 1:
                    return new PlaybackFragment();
                case 2:
                    return new MessageFragment();
                default:
                    return new RealTimeFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3; // Number of tabs
        }


    }


    public class MyHomeClickReceiver extends BroadcastReceiver {

        private static final String TAG = "MyHomeClickReceiver";
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private static final String SYSTEM_DIALOG_REASON_RECENTAPPS = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if ("com.cofe.solution.CUSTOM_ACTION".equals(intent.getAction())) {
                Log.d("MyHomeClickReceiver", "Custom action received.");
                // Handle custom logic
            }

            switch (action) {
                case Intent.ACTION_CLOSE_SYSTEM_DIALOGS:
                    String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                    if (SYSTEM_DIALOG_REASON_RECENTAPPS.equals(reason)) {
                        break;
                    }

                    if (!SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason) && !"fs_gesture".equals(reason)) {
                        return;
                    }

                    isHomePress = true;

                    break;
                default:
                    return;
            }

        }
    }



    @SuppressLint("ClickableViewAccessibility")
    private void showBottomSheet(View viewToAdd) {
        // Inflate the bottom sheet layout
        View bottomSheetView = getLayoutInflater().inflate(R.layout.popup_layout, null);
        RelativeLayout parentLL = bottomSheetView.findViewById(R.id.parent_RL);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Create a BottomSheetDialog
        ptZBottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        ptZBottomSheetDialog.setContentView(bottomSheetView);
        ptZBottomSheetDialog.getWindow().setDimAmount(0f);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentLL.addView(viewToAdd, layoutParams);

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());

        viewToAdd.setOnTouchListener((v, event) -> {
            behavior.setDraggable(true);  // Re-enable dragging
            return false;
        });


        viewToAdd.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                behavior.setDraggable(false);  // Disable dragging
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                behavior.setDraggable(true);  // Re-enable dragging
            }
            return false;
        });
        // Calculate the height of the target view
        int[] location = new int[2];
        mainButtonsLl.getLocationOnScreen(location);
        int targetViewHeight = location[1];

        // Set the peek height to match the target view's position
        behavior.setPeekHeight(targetViewHeight);

        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Add a custom BottomSheetCallback to control the sliding animation
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Prevent the Bottom Sheet from snapping to hidden state
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Control the slide-down speed
                bottomSheet.setAlpha(1 - (slideOffset * 0.3f));  // Adjust transparency if needed
            }
        });
        // Slowly animate the bottom sheet to expand to a specific height
        // Show the Bottom Sheet
        ptZBottomSheetDialog.show();

    }

    private void loadFragment() {
        DevAlarmMsgFragment fragment = new DevAlarmMsgFragment();
        fragment.setArgumentsFromActivity("devId", presenter.getDevId());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

    }

    void loadMoionFragment() {
        if (ptZBottomSheetDialog != null && ptZBottomSheetDialog.isShowing()) {
            Log.d("BottomSheetCheck", "BottomSheetDialog is visible.");
            ptZBottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        DetectTrackFragment fragment = new DetectTrackFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_up,  // Enter animation
                R.anim.slide_down,  // Exit animation
                R.anim.slide_up,   // Pop enter animation
                R.anim.slide_up  // Pop exit animation
        );
        fragment.setArgumentsFromActivity("devId", presenter.getDevId(), isAOVDevice);
        transaction.replace(R.id.fragment_container1, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }
    private void loadRecordFragment(Object[][] objects) {

        Bundle bundle = new Bundle();

        for (int i = 0; i < objects.length; i++) {
            String key = (String) objects[i][0];
            Object value = objects[i][1];

            if (value instanceof Integer) {
                bundle.putInt(key, (Integer) value);
            } else if (value instanceof String) {
                bundle.putString(key, (String) value);
            } else if (value instanceof Boolean) {
                bundle.putBoolean(key, (Boolean) value);
            } else if (value instanceof Double) {
                bundle.putDouble(key, (Double) value);
            } else if (value instanceof Float) {
                bundle.putFloat(key, (Float) value);
            }
        }
        bundle.putString("devId",presenter.getDevId());
        DevRecordFragment fragment = new DevRecordFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

    }


    public void turnToActivity1(Class _class,Object[][] objects) {
        Intent intent = new Intent(this,_class);
        if (objects != null) {
            for (int i = 0; i < objects.length; ++i) {
                if (objects[i][1] instanceof Integer) {
                    intent.putExtra((String) objects[i][0],(Integer) objects[i][1]);
                }else if (objects[i][1] instanceof String) {
                    intent.putExtra((String) objects[i][0],(String) objects[i][1]);
                }else if (objects[i][1] instanceof Boolean) {
                    intent.putExtra((String) objects[i][0],(Boolean) objects[i][1]);
                }else if (objects[i][1] instanceof Double) {
                    intent.putExtra((String) objects[i][0],(Double) objects[i][1]);
                }else if (objects[i][1] instanceof Float) {
                    intent.putExtra((String) objects[i][0],(Float) objects[i][1]);
                }
            }
        }
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                this, findViewById(R.id.buttons_ll), "sharedElement");

        if (presenter != null && !StringUtils.isStringNULL(presenter.getDevId())) {
            intent.putExtra("devId",presenter.getDevId());
        }
        startActivity(intent,options.toBundle());
    }

    /**
     * Create and show the BottomSheet*
     * Your layout containing the buttons
     * Your custom bottom sheet view
     */
    void showAudioCallPopup(View viewToAdd){

        /* View bottomSheetView = getLayoutInflater().inflate(R.layout.custom_bottom_sheet, null);
        RelativeLayout parentRv = bottomSheetView.findViewById(R.id.parent_rl);
        parentRv.addView(viewToAdd); */

        //audioDialog.setExternalView(viewToAdd);
        //audioDialog.setisFunIntercomeTrigger(viewToAdd);

        /*if(isFunIntercomeTrigger == false ) {
            audioDialog.show();
        } else {
            audioDialog.safeShow();
        }*/

        View bottomSheetView = getLayoutInflater().inflate(R.layout.custom_bottom_sheet, null);
        RelativeLayout parentLL = bottomSheetView.findViewById(R.id.parent_rl);

        // Create a BottomSheetDialog
        intercomeBottomSheetDialog.setContentView(bottomSheetView);
        intercomeBottomSheetDialog.getWindow().setDimAmount(0f);
        intercomeBottomSheetDialog.setCanceledOnTouchOutside(false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        try {
            ((ViewGroup)viewToAdd.getParent().getParent()).removeView((ViewGroup)viewToAdd.getParent());
            parentLL.removeView(viewToAdd);
            parentLL.addView(viewToAdd, layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ((ViewGroup)viewToAdd.getParent().getParent()).removeView((ViewGroup)viewToAdd.getParent());
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            if(viewToAdd.getParent() != null) {
                ((ViewGroup)viewToAdd.getParent()).removeView(viewToAdd); // <- fix
            }
            parentLL.removeView(viewToAdd);
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            parentLL.addView(viewToAdd, layoutParams);
        } catch (Exception e){
            e.printStackTrace();
        }

        intercomeBottomSheetInfoBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());

        viewToAdd.setOnTouchListener((v, event) -> {
            intercomeBottomSheetInfoBehavior.setDraggable(true);  // Re-enable dragging
            return false;
        });


        parentLL.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                intercomeBottomSheetInfoBehavior.setDraggable(false);  // Disable dragging
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                intercomeBottomSheetInfoBehavior.setDraggable(true);  // Re-enable dragging
            }
            return false;
        });
        // Calculate the height of the target view
        int[] location = new int[2];
        mainButtonsLl.getLocationOnScreen(location);
        int targetViewHeight = location[1];

        // Set the peek height to match the target view's position
        intercomeBottomSheetInfoBehavior.setPeekHeight(targetViewHeight);


        //intercomeBottomSheetInfoBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        intercomeBottomSheetInfoBehavior.setDraggable(false);

        // Add a custom BottomSheetCallback to control the sliding animation
        intercomeBottomSheetInfoBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Prevent the Bottom Sheet from snapping to hidden state
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    intercomeBottomSheetInfoBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Control the slide-down speed
                bottomSheet.setAlpha(1 - (slideOffset * 0.3f));  // Adjust transparency if needed
            }
        });
        intercomeBottomSheetInfoBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetView.findViewById(R.id.cancle_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /*intercomeBottomSheetInfoBehavior.setHideable(true);
                intercomeBottomSheetInfoBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);*/
                new Handler().postDelayed(() -> intercomeBottomSheetInfoBehavior.setState(BottomSheetBehavior.STATE_HIDDEN), 900);
                intercomeBottomSheetDialog.hide();
            }
        });

    }

    void showIntercomBottomSheetDialog() {
        intercomeBottomSheetDialog.show();
        intercomeBottomSheetInfoBehavior.setState(BottomSheetBehavior.STATE_EXPANDED); // Show

    }

    // camera volume icon change

    void showLowBatterDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.time_sync_popup_layout);
        dialog.setCancelable(false); // Prevent dismissal on outside touch
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getScreenWidth() * 0.9);
        dialog.getWindow().setAttributes(layoutParams);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        // Get references to the buttons
        Button btnYes = dialog.findViewById(R.id.btn_yes);
        LinearLayout btnNoLl = dialog.findViewById(R.id.btn_no_ll);
        TextView titleTxtv = dialog.findViewById(R.id.popup_title);
        View devider = dialog.findViewById(R.id.devider);

        btnNoLl.setVisibility(View.GONE);
        devider.setVisibility(View.GONE);
        titleTxtv.setText(getString(R.string.battery_low));

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }
    void showLowBatterToast(){
        lowBatteryHandler = new Handler();
        lowBatteryHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showToast(getString(R.string.battery_low), Toast.LENGTH_LONG);
            }
        }, 15000);
    }


    //called from motion fragment
    @Override
    public void onDemonButton(String featureNumber, boolean isActive) {
        if(isActive) {
            onFeatureClicked(FUN_APP_OBJ_EFFECT + "");
            onFeatureClicked(featureNumber);
        } else  {
            onFeatureClicked(featureNumber);
            onFeatureClicked(FUN_APP_OBJ_EFFECT + "");

        }
    }

    @Override
    public void motionFramentClose() {
        loaderDialog.setMessage();
        onRestart();
    }

}