package com.cofe.solution.ui.device.fragment;


import static android.view.View.VISIBLE;
import static com.cofe.solution.base.DemoConstant.LAST_CHANGE_SCALE_TIMES;
import static com.cofe.solution.base.DemoConstant.MULTI_LENS_THREE_SENSOR;
import static com.cofe.solution.base.DemoConstant.MULTI_LENS_TWO_SENSOR;
import static com.cofe.solution.base.DemoConstant.SENSOR_MAX_TIMES;
import static com.cofe.solution.base.DemoConstant.SUPPORT_SCALE_THREE_LENS;
import static com.cofe.solution.base.DemoConstant.SUPPORT_SCALE_TWO_LENS;
import static com.cofe.solution.base.FunError.EE_DVR_ACCOUNT_PWD_NOT_VALID;
import static com.manager.db.Define.LOGIN_NONE;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_MEDIA_DISCONNECT;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_PlAY;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_SAVE_PIC_FILE_S;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_SAVE_RECORD_FILE_S;
import static com.manager.device.media.audio.XMAudioManager.SPEAKER_TYPE_MAN;
import static com.manager.device.media.audio.XMAudioManager.SPEAKER_TYPE_NORMAL;
import static com.manager.device.media.audio.XMAudioManager.SPEAKER_TYPE_WOMAN;
import static com.xm.ui.dialog.PasswordErrorDlg.INPUT_TYPE_DEV_USER_PWD;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseFragment;
import com.cofe.solution.base.HistoryTracker;
import com.cofe.solution.base.SharedPreference;
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
import com.cofe.solution.ui.device.preview.view.DevActivity;
import com.cofe.solution.ui.device.preview.view.DevMonitorActivity;
import com.cofe.solution.ui.device.preview.view.PresetListActivity;
import com.cofe.solution.ui.device.preview.view.VideoIntercomActivity;
import com.cofe.solution.ui.device.preview.view.fragment_list.MessageFragment;
import com.cofe.solution.ui.device.preview.view.fragment_list.PlaybackFragment;
import com.cofe.solution.ui.device.preview.view.fragment_list.RealTimeFragment;
import com.cofe.solution.ui.device.record.presenter.DevRecordPresenter;
import com.cofe.solution.ui.device.record.view.DevRecordActivity;
import com.cofe.solution.utils.SPUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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
import com.xm.ui.widget.dialog.EditDialog;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.ptzview.PtzView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DevMonitorFragment extends DemoBaseFragment<DevMonitorPresenter> implements DevMonitorContract.IDevMonitorView, View.OnClickListener, SensorChangeContract.ISensorChangeView, RealTimeFragment.OnFeatureClickListener {
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
    ImageView microphoneImg, rotateScreen, openSetting, backImage;
    TextView dName;
    ActivityResultLauncher<Intent> resultLauncher;
    Boolean isPlayBackOpen = false;
    String devId;
    int chId;
    Button btnLayout1, btnLayout2, btnLayout3;
    LinearLayout layout1, layout2, layout3;
    private PopupWindow popupWindow;

    Handler handler;

    public static final String CUSTOM_HOME_ACTION = "com.cofe.solution.HOME_ACTION";
    MyHomeClickReceiver mHomeClickReceiver;
    LinearLayout battery;
    LinearLayout icon_ptz;
    LinearLayout mainButtonsLl;

    DevRecordPresenter devRecordPresenter;
    private DevActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        View fragmentView = inflater.inflate(R.layout.fragment_dev_monitor, container, false);
        checkUserLogin(fragmentView);

        // Initialize buttons and layouts
        btnLayout1 = fragmentView.findViewById(R.id.btn_layout1);
        btnLayout2 = fragmentView.findViewById(R.id.btn_layout2);
        btnLayout3 = fragmentView.findViewById(R.id.btn_layout3);

        layout1 = fragmentView.findViewById(R.id.layout1);
        layout2 = fragmentView.findViewById(R.id.layout2);
        layout3 = fragmentView.findViewById(R.id.layout3);

        // Set up button click listeners
        //btnLayout1.setOnClickListener(v -> showLayout(1));
        //btnLayout2.setOnClickListener(v -> showLayout(2));
        //btnLayout3.setOnClickListener(v -> showLayout(3));

        // Initialize default state
        showLayout(1, fragmentView);

//        devRecordPresenter = new DevMonitorPresenter(this);

        return fragmentView;

    }

    private void showLayout(int layoutNumber, View fragmentView) {
        // Reset all layouts to GONE

        // Reset button colors to dim
        btnLayout1.setBackgroundTintList(activity.getColorStateList(R.color.dim_color));
        btnLayout2.setBackgroundTintList(activity.getColorStateList(R.color.dim_color));
        btnLayout3.setBackgroundTintList(activity.getColorStateList(R.color.dim_color));

        // Show the selected layout and highlight the corresponding button
        switch (layoutNumber) {
            case 1:
                layout1.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);

                btnLayout3.setBackground(ContextCompat.getDrawable(activity, R.drawable.squaer_button_background_ubselected));
                btnLayout2.setBackground(ContextCompat.getDrawable(activity, R.drawable.squaer_button_background_ubselected));
                btnLayout1.setBackground(ContextCompat.getDrawable(activity, R.drawable.squaer_button_background));

                break;
            case 2:
//                onFeatureClicked(FUN_PLAYBACK + "");
                break;
            case 3:
                /*layout3.setVisibility(View.VISIBLE);
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);
                btnLayout3.setBackground(ContextCompat.getDrawable(activity, R.drawable.squaer_button_background));
                btnLayout2.setBackground(ContextCompat.getDrawable(activity, R.drawable.squaer_button_background_ubselected));
                btnLayout1.setBackground(ContextCompat.getDrawable(activity, R.drawable.squaer_button_background_ubselected));*/
                presenter.getDevId();
//                turnToActivity(DevAlarmMsgActivity.class);
                break;


        }


        // Initialize icons
        LinearLayout cloudStorageIcon = fragmentView.findViewById(R.id.icon_cloud_storage);
        LinearLayout aiIcon = fragmentView.findViewById(R.id.icon_ai);
        LinearLayout alarmIcon = fragmentView.findViewById(R.id.icon_alarm);

        LinearLayout icon_motion_tracking = fragmentView.findViewById(R.id.icon_motion_tracking);
        icon_ptz = fragmentView.findViewById(R.id.icon_ptz);
        LinearLayout icon_favorites = fragmentView.findViewById(R.id.icon_favorites);

        LinearLayout icon_med_tracking = fragmentView.findViewById(R.id.icon_med_tracking);
        LinearLayout icon_ptz_reverse = fragmentView.findViewById(R.id.icon_ptz_reverse);
        LinearLayout icon_sd_card_alb = fragmentView.findViewById(R.id.icon_sd_card_alb);
        LinearLayout zoom = fragmentView.findViewById(R.id.zoom);
        LinearLayout imageListLl = fragmentView.findViewById(R.id.image_list_ll);
        icon_sd_card_alb.setVisibility(View.GONE);

        battery = fragmentView.findViewById(R.id.battery);

        // Set click listeners
        cloudStorageIcon.setOnClickListener(v -> {
            onFeatureClicked(cloudStorageIcon.getTag().toString());
        });

        aiIcon.setOnClickListener(v -> {
            onFeatureClicked(aiIcon.getTag().toString());
        });

        alarmIcon.setOnClickListener(v -> {
            onFeatureClicked(alarmIcon.getTag().toString());
        });

        icon_motion_tracking.setOnClickListener(v -> {
            onFeatureClicked(icon_motion_tracking.getTag().toString());
        });

        icon_ptz.setOnClickListener(v -> {
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
        checkAOVBattery();

    }

    void checkUserLogin(View fragmentView) {
        Log.d(" checkUserLogin .getAccountUserName()", " > " + DevDataCenter.getInstance().getAccountUserName());
        Log.d(" checkUserLogin .getAccesstoken()", " > " + DevDataCenter.getInstance().getAccessToken());
        Log.d(" checkUserLogin .AccountManager.getInstance()", " > " + AccountManager.getInstance());
        Log.d(" checkUserLogin .DevDataCenter.getInstance()", " > " + DevDataCenter.getInstance());
        Log.d(" checkUserLogin .DevDataCenter.getInstance()", " > " + DevDataCenter.getInstance());

        if (DevDataCenter.getInstance().getAccountUserName() != null) {
            if (DevDataCenter.getInstance().getAccessToken() == null) {
                AccountManager.getInstance().xmLogin(DevDataCenter.getInstance().getAccountUserName(), DevDataCenter.getInstance().getAccountPassword(), 1,
                        new BaseAccountManager.OnAccountManagerListener() {
                            @Override
                            public void onSuccess(int msgId) {
                                Log.d("checkUserLogin Access toekn", " > " + DevDataCenter.getInstance().getAccessToken());
                                initView(fragmentView);
                                initData();

                            }

                            @Override
                            public void onFailed(int msgId, int errorId) {
                                Log.d("checkUserLogin onFailed", " > " + errorId);
                                Log.d("checkUserLogin onFailed", " > " + msgId);

                            }

                            @Override
                            public void onFunSDKResult(Message msg, MsgContent ex) {
                                Log.d("checkUserLogin onFunSDKResult Access msg", " msg > " +msg);
                                Log.d("checkUserLogin onFunSDKResult Access msg", " msg > " + ex);

                            }
                        });//LOGIN_BY_INTERNET（1）  Account login type

            } else {
                Log.d("checkUserLogin access toen ", " is not null> ");
                initView(fragmentView);
                initData();
            }

        } else {
            Log.d("checkUserLogin username is ", " not  null >");
            initView(fragmentView);
            initData();
        }
    }

    private void initView(View fragmentView) {
        /*titleBar = fragmentView.findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_preview));
        titleBar.setRightBtnResource(R.mipmap.icon_setting_normal, R.mipmap.icon_setting_pressed);
        titleBar.setLeftClick(this);
        titleBar.setRightIvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                Intent intent = new Intent();
                intent.setClass(activity, DeviceConfigActivity.class);
                intent.putExtra("devId", presenter.getDevId());
                activity.startActivity(intent);
            }
        });

        titleBar.setBottomTip(DevMonitorActivity.class.getName());*/
        playWndLayout = fragmentView.findViewById(R.id.layoutPlayWnd);
        rvMonitorFun = fragmentView.findViewById(R.id.rv_monitor_fun);
        wndLayout = fragmentView.findViewById(R.id.wnd_layout);

        tvBatteryState = fragmentView.findViewById(R.id.tv_battery_state);
        tvWiFiState = fragmentView.findViewById(R.id.tv_wifi_state);
        tvRate = fragmentView.findViewById(R.id.tv_rate);
        initPresetLayout();

        autoHideManager = new AutoHideManager();
        autoHideManager.addView(fragmentView.findViewById(R.id.ll_dev_state));
        autoHideManager.show();

        // Initialize TabLayout and ViewPager2
        tabLayout = fragmentView.findViewById(R.id.tab_layout);
        viewPager = fragmentView.findViewById(R.id.pager);

        cameraImg = fragmentView.findViewById(R.id.camera);
        videoImg = fragmentView.findViewById(R.id.video);
        soundImg = fragmentView.findViewById(R.id.sound);
        sdImag = fragmentView.findViewById(R.id.sd);
        microphoneImg = fragmentView.findViewById(R.id.microphone);
        rotateScreen = fragmentView.findViewById(R.id.rotate_icon);
        openSetting = fragmentView.findViewById(R.id.settings_icon);
        backImage = fragmentView.findViewById(R.id.settings_icon);
        dName = fragmentView.findViewById(R.id.device_name);
        mainButtonsLl = fragmentView.findViewById(R.id.main_buttons_ll);

        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealWithMonitorFunction(Integer.parseInt(cameraImg.getTag().toString()), true);
            }
        });
        videoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealWithMonitorFunction(Integer.parseInt(videoImg.getTag().toString()), true);
            }
        });
        soundImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealWithMonitorFunction(Integer.parseInt(soundImg.getTag().toString()), true);
            }
        });
        sdImag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealWithMonitorFunction(Integer.parseInt(sdImag.getTag().toString()), true);
            }
        });

        microphoneImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealWithMonitorFunction(Integer.parseInt(microphoneImg.getTag().toString()), true);
            }
        });

        rotateScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    dealWithMonitorFunction(9, true);
                } else {
                    screenOrientationManager.portraitScreen(activity, true);
                }
            }
        });

        openSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, DeviceSetting.class);
                startActivity(i);
            }
        });
        // Set up the ViewPager2 Adapter
        viewPager.setAdapter(new DeviceViewPagerAdapter(activity));
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


        mHomeClickReceiver = new MyHomeClickReceiver();

        initSensorView(fragmentView);
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
        playViews = playWndLayout.setViewCount(wndCount);

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
    private void initSensorView(View fragmentView) {
        sbVideoScale = fragmentView.findViewById(R.id.sb_video_scale);
        sbVideoScale.setOnScaleSeekBarListener(new XMScaleSeekBar.OnScaleSeekBarListener() {
            @Override
            public void onItemSelected(int progress, boolean isTouchUp) {
                if (isTouchUp) {
                    MonitorManager monitorManager = presenter.getCurSelMonitorManager(playWndLayout.getSelectedId());
                    if (monitorManager != null) {
                        SPUtil.getInstance(activity).setSettingParam(LAST_CHANGE_SCALE_TIMES + presenter.getDevId(), (float) (progress * ((float) 1 / sbVideoScale.getSmallSubCount())));
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
        Intent intent = activity.getIntent();
        if (intent == null) {
            activity.finish();
            return;
        }

        chnCount = intent.getIntExtra("chnCount", 1);
//        titleBar.setTitleText(getString(R.string.channel) + ":" + presenter.getChnId());
        presenter.setChnId(0);
        devId = presenter.getDevId();
        chId = presenter.getChnId();

        initPlayWnd();

        for (int i = 0; i < chnCount && i < playViews.length; ++i) {
            presenter.initMonitor(i, playViews[i]);
        }

        monitorFunAdapter = new MonitorFunAdapter();
        rvMonitorFun.setLayoutManager(new GridLayoutManager(activity, 4));
        rvMonitorFun.setAdapter(monitorFunAdapter);

        screenOrientationManager = ScreenOrientationManager.getInstance();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.registerReceiver(mHomeClickReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            activity.registerReceiver(mHomeClickReceiver, filter);
        }
        sensorChangePresenter = new SensorChangePresenter(this);
    }


    /**
     * onResume->manager.loginDev（）->iDevMonitorView.onLoginResult（）-> true ?  startMonitor(): manager.loginDev or toast
     * onResume->manager.loginDev（）-> initData()-> manager.getDevAbility() -> getDevWiFiSignalLevel() ->iDevMonitorView.onWiFiSignalLevelResult() -> Setting the wifi Level
     **/
    @Override
    public void onResume() {
        super.onResume();
        if (!isHomePress) {
//            showProgress();
//            presenter.loginDev();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        for (int i = 0; i < chnCount && i < playViews.length; ++i) {
            presenter.stopMonitor(i);
        }

        isFirstGetVideoStream = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for (int i = 0; i < chnCount && i < playViews.length; ++i) {
            presenter.destroyMonitor(i);
        }

        if (screenOrientationManager != null) {
            screenOrientationManager.release(activity);
        }

        //如果是低功耗设备，退出预览的时候需要发起休眠命令
        if (DevDataCenter.getInstance().isLowPowerDev(presenter.getDevId())) {
            IDRManager.sleepNow(activity, presenter.getDevId());
        }

        presenter.release();

        if (mHomeClickReceiver != null) {
            activity.unregisterReceiver(mHomeClickReceiver);
        }
        if (handler != null) {
            handler.removeCallbacks(null);
            handler = null;
        }

    }

    @Override
    public DevMonitorPresenter getPresenter() {
        Log.d("DevMonitorPresenter ", "getPresenter > " +new DevMonitorPresenter(this));
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
        Log.d("onLoginResult ", "isSuccess > " +isSuccess);
        Log.d("onLoginResult ", "errorId > " +errorId);
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
                XMPromptDlg.onShowPasswordErrorDialog(activity, devInfo.getSdbDevInfo(), 0, new PwdErrorManager.OnRepeatSendMsgListener() {
                    @Override
                    public void onSendMsg(int msgId) {
//                        showProgress();
                        presenter.loginDev();
                    }
                });
            } else if (errorId < 0) {
                //showToast(getString(R.string.open_video_f) + errorId, Toast.LENGTH_LONG);
                showToast(getString(R.string.open_video_f), Toast.LENGTH_LONG);
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
//        hideProgress();
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
            SPUtil.getInstance(activity).setSettingParam(SUPPORT_SCALE_TWO_LENS + presenter.getDevId(), systemFunctionBean.OtherFunction.SupportScaleTwoLens);
            SPUtil.getInstance(activity).setSettingParam(SUPPORT_SCALE_THREE_LENS + presenter.getDevId(), systemFunctionBean.OtherFunction.SupportScaleThreeLens);

            //将双目或者三目设备 是否需要APP支持变倍的能力保存到本地
            SPUtil.getInstance(activity).setSettingParam(MULTI_LENS_TWO_SENSOR + presenter.getDevId(), systemFunctionBean.OtherFunction.MultiLensTwoSensor);
            SPUtil.getInstance(activity).setSettingParam(MULTI_LENS_THREE_SENSOR + presenter.getDevId(), systemFunctionBean.OtherFunction.MultiLensThreeSensor);
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
                XMPromptDlg.onShowPasswordErrorDialog(activity, devInfo.getSdbDevInfo(), 0, new PwdErrorManager.OnRepeatSendMsgListener() {
                    @Override
                    public void onSendMsg(int msgId) {
//                        showProgress();
                        presenter.startMonitor(chnId);
                    }
                });
            } else if (errorId == EFUN_ERROR.EE_DVR_LOGIN_USER_NOEXIST) {
                XMDevInfo devInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
                XMPromptDlg.onShowPasswordErrorDialog(activity, devInfo.getSdbDevInfo(), 0, getString(R.string.input_username_password), INPUT_TYPE_DEV_USER_PWD, true, new PwdErrorManager.OnRepeatSendMsgListener() {
                    @Override
                    public void onSendMsg(int msgId) {
//                        showProgress();
                        presenter.startMonitor(chnId);
                    }
                }, false);
            } else if (errorId < 0) {
                showToast(getString(R.string.open_video_f), Toast.LENGTH_LONG);
            }
        }

        if (state == E_STATE_SAVE_RECORD_FILE_S) {
            showToast(getString(R.string.record_s), Toast.LENGTH_LONG);
        } else if (state == E_STATE_SAVE_PIC_FILE_S) {
            showToast(getString(R.string.capture_s), Toast.LENGTH_LONG);
        } else if (state == E_STATE_MEDIA_DISCONNECT) {
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

        monitorFunAdapter.changeBtnState(FUN_CHANGE_STREAM, presenter.getStreamType(attribute.getChnnel()) == SDKCONST.StreamType.Main);
    }

    @Override
    public void onUpdateFaceFrameView(final FaceFeature[] faceFeatures, final int width, final int height) {
    }

    @Override
    public void onLightCtrlResult(String jsonData) {
        XMPromptDlg.onShow(activity, jsonData, null);
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

        tvBatteryState.setText(String.format(getString(R.string.battery_state_show), electCapacityBean.percent, electCapacityBean.devStorageStatus, electCapacityBean.electable));
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

            tvWiFiState.setText(String.format(getString(R.string.wifi_state_show), wifiRouteInfo.getSignalLevel()));
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
        ToastUtils.showLong(isSuccess ? getString(R.string.libfunsdk_operation_success) : getString(R.string.libfunsdk_operation_failed));
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
        return activity;
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

        SPUtil.getInstance(activity).setSettingParam(SENSOR_MAX_TIMES + devId, sbVideoScale.getSubCount() + 1);
        boolean scaleTwo = SPUtil.getInstance(activity).getSettingParam(SUPPORT_SCALE_TWO_LENS + devId, false);
        boolean scaleThree = SPUtil.getInstance(activity).getSettingParam(SUPPORT_SCALE_THREE_LENS + devId, false);
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
        boolean scaleTwo = SPUtil.getInstance(activity).getSettingParam(SUPPORT_SCALE_TWO_LENS + devId, false);
        boolean scaleThree = SPUtil.getInstance(activity).getSettingParam(SUPPORT_SCALE_THREE_LENS + devId, false);
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
            boolean scaleTwo = SPUtil.getInstance(activity).getSettingParam(SUPPORT_SCALE_TWO_LENS + playerAttribute.getDevId(), false);
            //三目设备变倍
            boolean scaleThree = SPUtil.getInstance(activity).getSettingParam(SUPPORT_SCALE_THREE_LENS + playerAttribute.getDevId(), false);
            int streamType = monitorManager.getStreamType();

            //如果当前的码流类型匹配的话，将当前码流信息帧获取到的倍数保存到本地
            if (multiLensParam.st_3_scaleStream == streamType) {
                SPUtil.getInstance(activity).setSettingParam(LAST_CHANGE_SCALE_TIMES + playerAttribute.getDevId(), multiLensParam.st_4_times);
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
                    SPUtil.getInstance(activity).setSettingParam("IS_TURN_AROUND_SPEED_MULTI" + playerAttribute.getDevId(), curScale);
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
        public MonitorFunAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MonitorFunAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_monitor_fun, null));
        }

        @Override
        public void onBindViewHolder(@NonNull MonitorFunAdapter.ViewHolder holder, int position) {
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
                btnMonitorFun.setOnClickListener(new View.OnClickListener() {
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
                    presenter.stopRecord(presenter.getChnId());
                } else {
                    presenter.startRecord(presenter.getChnId());
                }
                Glide.with(this).
                        load((isSelected)
                                ? getResources().getDrawable(R.drawable.video_icon, null)
                                : getResources().getDrawable(R.drawable.active_video, null)
                        )
                        .into(videoImg);

                return true;
            case FUN_PTZ://云台控制
            {
                PtzView contentLayout = (PtzView) LayoutInflater.from(activity).inflate(R.layout.view_ptz, null);
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

                showBottomSheet(contentLayout);
                //XMPromptDlg.onShow(activity, contentLayout);
                break;
            }
            case FUN_PTZ_CALIBRATION://云台校正
//                showProgress();
                presenter.ptzCalibration();
                break;
            case FUN_INTERCOM: //单向对讲，按下去说话，放开后听到设备端的声音，对话框消失后 对讲结束
            {
                View layout = LayoutInflater.from(activity).inflate(R.layout.view_intercom, null);
                RippleButton rippleButton = layout.findViewById(R.id.btn_talk);

                ListSelectItem lsiDoubleTalkSwitch = layout.findViewById(R.id.lsi_double_talk_switch);
                ListSelectItem lsiTalkBroadcast = layout.findViewById(R.id.lsi_double_talk_broadcast);
                lsiTalkBroadcast.setOnClickListener(new View.OnClickListener() {
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
                                    } else {
                                        //当前对讲未开启，则需要开启对讲 (如果设备的通道数(chnCount)大于1，比如NVR设备的话，那么要使用通道对讲)
                                        boolean isTalkBroadcast = lsiTalkBroadcast.getRightValue() == SDKCONST.Switch.Open;
                                        presenter.startDoubleIntercom(presenter.getChnId(), isTalkBroadcast); //Turn on the two-way intercom
                                        rippleButton.setUpGestureEnable(false);
                                        //对讲开启的时候，视频伴音会随之关闭
                                        monitorFunAdapter.changeBtnState(FUN_VOICE, false);
                                    }

                                } else {
                                    //(如果设备的通道数(chnCount)大于1，比如NVR设备的话，那么要使用通道对讲)
                                    boolean isTalkBroadcast = lsiTalkBroadcast.getRightValue() == SDKCONST.Switch.Open;
                                    presenter.startSingleIntercomAndSpeak(presenter.getChnId(), isTalkBroadcast);//Enable a one-way intercom
                                    rippleButton.setUpGestureEnable(true);
                                    //对讲开启的时候，视频伴音会随之关闭
                                    monitorFunAdapter.changeBtnState(FUN_VOICE, false);
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                if (lsiDoubleTalkSwitch.getRightValue() == SDKCONST.Switch.Close) {
                                    presenter.stopSingleIntercomAndHear(presenter.getChnId());
                                }
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

                lsiDoubleTalkSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lsiDoubleTalkSwitch.setRightImage(lsiDoubleTalkSwitch.getRightValue() == SDKCONST.Switch.Close ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
                        if (lsiDoubleTalkSwitch.getRightValue() == SDKCONST.Switch.Open) {
                            rippleButton.setTabText(getString(R.string.click_to_open_two_way_talk));
                        } else {
                            rippleButton.setTabText(getString(R.string.long_press_open_one_way_talk));
                        }

                        presenter.stopIntercom(presenter.getChnId());
                    }
                });

                ListSelectItem lsiChooseVoice = layout.findViewById(R.id.lsi_choose_voice);
                lsiChooseVoice.getExtraSpinner().initData(new String[]{getString(R.string.speaker_type_normal), getString(R.string.speaker_type_man), getString(R.string.speaker_type_woman)}, new Integer[]{SPEAKER_TYPE_NORMAL, SPEAKER_TYPE_MAN, SPEAKER_TYPE_WOMAN});
                lsiChooseVoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lsiChooseVoice.toggleExtraView();
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

                XMPromptDlg.onShow(activity, layout, true, new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        presenter.stopIntercom(presenter.getChnId());
                    }
                });
            }
            break;
            case FUN_PLAYBACK://录像回放
//                turnToActivity(DevRecordActivity.class, new Object[][]{{"devId", presenter.getDevId()}, {"chnId", presenter.getChnId()}});

                break;
            case FUN_CHANGE_STREAM://主副码流切换      The primary stream is clearer and the secondary stream is blurry
                //如果支持多目镜头变倍切换的，等镜头切换或者变倍后再切换码流
                if (sensorChangePresenter.getSensorCount() > 1 && sbVideoScale.getVisibility() == VISIBLE) {
                    MonitorManager monitorManager = presenter.getCurSelMonitorManager(presenter.getChnId());
                    boolean scaleTwo = SPUtil.getInstance(activity).getSettingParam(SUPPORT_SCALE_TWO_LENS + presenter.getDevId(), false);
                    boolean scaleThree = SPUtil.getInstance(activity).getSettingParam(SUPPORT_SCALE_THREE_LENS + presenter.getDevId(), false);

                    int afterChangeStreamType = monitorManager.getStreamType() == SDKCONST.StreamType.Main ? SDKCONST.StreamType.Extra : SDKCONST.StreamType.Main;
                    //是否支持设备端变倍
                    if (scaleTwo || scaleThree) {
                        sensorChangePresenter.ctrlVideoScale(presenter.getDevId(), afterChangeStreamType, sbVideoScale.getProgress(), -1, 1);
                    } else {
                        sensorChangePresenter.switchSensor(presenter.getDevId(), -1, afterChangeStreamType, -1);
                    }

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
//                showProgress();
                presenter.getTour(presenter.getChnId());
                break;
            }
            case FUN_LANDSCAPE://全屏
                screenOrientationManager.landscapeScreen(activity, true);
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
                XMPromptDlg.onShow(activity, getString(R.string.not_all_dev_support), new View.OnClickListener() {
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
//                showProgress();
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
//                        hideProgress();
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
                XMPromptDlg.onShowEditDialog(activity, getString(R.string.food_portions), "3", new EditDialog.OnEditContentListener() {
                    @Override
                    public void onResult(String content) {
                        if (StringUtils.isStringNULL(content)) {
                            ToastUtils.showLong(R.string.input_food_portions);
                        } else {
                            XMPromptDlg.onShow(activity, getString(R.string.please_sel_feet_type), getString(R.string.feed_the_dog), getString(R.string.feed_the_fish), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    presenter.feedTheDog(Integer.parseInt(content));
                                }
                            }, new View.OnClickListener() {
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
                View layout = LayoutInflater.from(activity).inflate(R.layout.view_ircut, null);
                ListSelectItem lsiIrCut = layout.findViewById(R.id.lsi_ircut);
                lsiIrCut.setOnClickListener(new View.OnClickListener() {
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

                XMPromptDlg.onShow(activity, layout, true, new DialogInterface.OnDismissListener() {
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
                XMPromptDlg.onShow(activity, getString(R.string.factory_reset), getString(R.string.only_factory_reset), getString(R.string.factory_reset_and_delete_dev), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.factoryReset(new DeviceManager.OnDevManagerListener() {
                            @Override
                            public void onSuccess(String devId, int operationType, Object result) {
                                showToast(getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG);
                            }

                            @Override
                            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                                showToast(getString(R.string.libfunsdk_operation_failed), Toast.LENGTH_LONG);
                            }
                        });
                    }
                }, new View.OnClickListener() {
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
                                    activity.finish();
                                } else {
                                    AccountManager.getInstance().deleteDev(presenter.getDevId(), new BaseAccountManager.OnAccountManagerListener() {
                                        @Override
                                        public void onSuccess(int msgId) {
                                            showToast(getString(R.string.delete_s), Toast.LENGTH_LONG);
                                            activity.finish();
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
                                showToast(getString(R.string.libfunsdk_operation_failed), Toast.LENGTH_LONG);
                            }
                        });
                    }
                });
                break;
            case FUN_POINT://指哪看那
                //如果支持指哪看哪的功能，需要将预览画布的触摸事件设置成false，传递给上层控件
                if (!isShowAppMoreScreen) {
                    isOpenPointPtz = !isOpenPointPtz;
                    if (isOpenPointPtz) {
                        XMPromptDlg.onShow(activity, getString(R.string.camera_point_ptz_tips), null);
                    }

                    changePlayViewSize();
                } else {
                    XMPromptDlg.onShow(activity, getString(R.string.is_app_more_screen_not_support_this_function), null);
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
                        Intent intent = new Intent(activity, WhiteLightActivity.class);
                        intent.putExtra("devId", presenter.getDevId());
                        intent.putExtra("supportLightSwitch", false);
                        startActivity(intent);
                        break;

                    //支持白光灯
                    case WHITE_LIGHT_CAMERA:
                        Intent whiteLightIntent = new Intent(activity, WhiteLightActivity.class);
                        whiteLightIntent.putExtra("devId", presenter.getDevId());
                        whiteLightIntent.putExtra("supportLightSwitch", true);
                        startActivity(whiteLightIntent);
                        break;
                    //支持双光
                    case DOUBLE_LIGHT_CAMERA:
                        Intent doubleLightIntent = new Intent(activity, DoubleLightActivity.class);
                        doubleLightIntent.putExtra("devId", presenter.getDevId());
                        doubleLightIntent.putExtra("supportLightSwitch", true);
                        startActivity(doubleLightIntent);
                        break;
                    //支持音乐灯
                    case MUSIC_LIGHT_CAMERA:
                        Intent musicLightIntent = new Intent(activity, MusicLightActivity.class);
                        musicLightIntent.putExtra("devId", presenter.getDevId());
                        musicLightIntent.putExtra("supportLightSwitch", true);
                        startActivity(musicLightIntent);
                        break;
                    //支持庭院双光灯
                    case GARDEN_DOUBLE_LIGHT_CAMERA:
                        Intent GardenDoubleLightIntent = new Intent(activity, GardenDoubleLightActivity.class);
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
                    XMPromptDlg.onShow(activity, getString(R.string.this_function_only_glsurfaceview), null);
                    break;
                }

                isShowAPPZooming = !isShowAPPZooming;
                if (isShowAPPZooming) {
                    sbVideoScale.setVisibility(VISIBLE);
                    //推荐真实倍数3倍，显示放大倍数6倍，最好可以整除，显示倍数大于真实倍数
                    int maxTimes = 3;
                    int maxTimesShow = 6;
                    float scale = SPUtil.getInstance(activity).getSettingParam(LAST_CHANGE_SCALE_TIMES + presenter.getDevId(), 0f);
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
                    XMPromptDlg.onShow(activity, getString(R.string.is_open_point_ptz_not_support_this_function), null);
                }
                return true;
            default:
                Toast.makeText(activity, getString(R.string.not_support_tip), Toast.LENGTH_LONG).show();
                break;
        }

        return false;
    }

    /**
     * 改变播放布局大小
     */
    private void changePlayViewSize() {
        ViewGroup.LayoutParams layoutParams = wndLayout.getLayoutParams();
        if (isOpenPointPtz || isShowAppMoreScreen) {
            layoutParams.height = XUtils.getScreenWidth(activity) * 18 / 16;
        } else {
            layoutParams.height = XUtils.getScreenWidth(activity) * 9 / 16;
        }

        wndLayout.setLayoutParams(layoutParams);
        presenter.setPlayViewTouchable(0, false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
//            titleBar.setVisibility(View.GONE);
            rvMonitorFun.setVisibility(View.GONE);
            portraitWidth = wndLayout.getWidth();
            portraitHeight = wndLayout.getHeight();
            ViewGroup.LayoutParams layoutParams = wndLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            wndLayout.requestLayout();
            presenter.setPlayViewTouchable(0, true);
            SharedPreference cookies = new SharedPreference(activity.getApplication());
            cookies.retrievDevName();
            dName.setText(cookies.retrievDevName());

            //如果是假多目，横屏的时候需要更改画布
            if (isShowAppMoreScreen) {
                playViews = playWndLayout.setViewCount(2, 2);
                presenter.changePlayView(playViews);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            //titleBar.setVisibility(VISIBLE);
            //rvMonitorFun.setVisibility(VISIBLE);
            ViewGroup.LayoutParams layoutParams = wndLayout.getLayoutParams();
            layoutParams.width = portraitWidth;
            layoutParams.height = portraitHeight;
            wndLayout.requestLayout();
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
            if (isShowAppMoreScreen) {
                playViews = playWndLayout.setViewCount(2, 1);
                presenter.changePlayView(playViews);
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    /*@Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenOrientationManager.portraitScreen(this, true);
            super.onBackPressed();
        } else {
            activity.finish();
            super.onBackPressed();
        }
    }*/

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

            btnTurnToPresetList.setOnClickListener(new View.OnClickListener() {
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

    /*@Override
    protected void onRestart() {
        super.onRestart();
        if (isHomePress) {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);
            if (intent != null) {
                startActivityForResult(intent, 0);
            }
        }
    }*/

    @Override
    public void onChangeManualAlarmResult(boolean isSuccess, int errorId) {
        if (isSuccess) {
            showToast(getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.libfunsdk_operation_failed), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onPtzCalibrationResult(boolean isSuccess, int errorId) {
        if (isSuccess) {
            showToast(getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.libfunsdk_operation_failed), Toast.LENGTH_LONG);
        }

//        hideProgress();
    }

    /**
     * 新建预置点ID取值252、253、254，将这三个预置点作为巡航点
     *
     * @param tourBeans
     * @param errorId
     */
    @Override
    public void onShowTour(List<TourBean> tourBeans, int errorId) {
//        hideProgress();
        if (errorId == 0) {
            LinearLayout contentLayout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.view_criuse, null);
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

            btnOne.setOnClickListener(new View.OnClickListener() {
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

            btnTwo.setOnClickListener(new View.OnClickListener() {
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

            btnThree.setOnClickListener(new View.OnClickListener() {
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

            btnStartCruise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.startTour(0);
                }
            });

            btnStopCruise.setOnClickListener(new View.OnClickListener() {
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


            tourDlg = XMPromptDlg.onShow(activity, contentLayout);
        }
    }

    @Override
    public void onTourCtrlResult(boolean isSuccess, int tourCmd, int errorId) {
        if (isSuccess) {
            Toast.makeText(activity, getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG).show();
            if (tourCmd == PresetManager.PRESET_ADD_TOUR || tourCmd == PresetManager.PRESET_DELETE_TOUR) {
                tourDlg.dismiss();
                presenter.getTour(0);
            }
        } else {
            Toast.makeText(activity, getString(R.string.libfunsdk_operation_failed), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTourEndResult() {
        Toast.makeText(activity, R.string.tour_end, Toast.LENGTH_LONG).show();
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

        XMPromptDlg.onShow(activity, "是否设置巡航点?", new View.OnClickListener() {
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
                        XMPromptDlg.onShow(activity, "确定删除该巡航点?", new View.OnClickListener() {
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

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            showProgress();
            presenter.loginDev();
        } else {
            activity.finish();
        }
    }*/
    @Override
    public void onFeatureClicked(String featureName) {
        //Toast.makeText(activity, "Feature clicked: " + featureName, Toast.LENGTH_SHORT).show();
        if (featureName.equals("battery")) {
            Intent i = new Intent(activity, AovSettingActivity.class);
            i.putExtra("devId", presenter.getDevId());
            startActivity(i);

        } else {
            dealWithMonitorFunction(Integer.parseInt(featureName), true);
        }

    }


    void checkAOVBattery() {
        DeviceManager.getInstance().getDevAllAbility(presenter.getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                Log.d("Device id ", " presenter.getDevId() > " + presenter.getDevId());
                Log.d("Device id ", " result.OtherFunction.AovMode > " + result.OtherFunction.AovMode);
                Log.d("Device id ", " result.OtherFunction.BatteryManager > " + result.OtherFunction.BatteryManager);

                /*if(getContext()!=null) {
                    if (battery != null) {
                        if(result.OtherFunction.BatteryManager) {
                            battery.setVisibility(VISIBLE);
                        } else {
                            battery.setVisibility(View.INVISIBLE);
                        }
                    }
                }*/
                try {
                    if (result.OtherFunction.AovMode) {
                        battery.setVisibility(VISIBLE);
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
               // Log.d("onFailed > ", " errorId > " + errorId);
               // Log.d("onFailed > ", " jsonName > " + jsonName);
            }
        });

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
//                    return DevRecordFragment.newInstance();
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


    private void showBottomSheet(View viewToAdd) {
        // Inflate the bottom sheet layout
        View bottomSheetView = getLayoutInflater().inflate(R.layout.popup_layout, null);
        RelativeLayout parentLL = bottomSheetView.findViewById(R.id.parent_RL);

        // Create a BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, R.style.CustomBottomSheetDialog);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.getWindow().setDimAmount(0f);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentLL.addView(viewToAdd, layoutParams);

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());

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

        // Show the Bottom Sheet
        bottomSheetDialog.show();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        this.activity = (DevActivity) activity;
        super.onAttach(activity);
    }
}