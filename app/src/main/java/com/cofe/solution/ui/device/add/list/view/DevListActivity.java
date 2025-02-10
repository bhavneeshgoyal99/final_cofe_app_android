package com.cofe.solution.ui.device.add.list.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cofe.solution.base.SharedPreference;
import com.cofe.solution.ui.device.add.sn.view.DevSnConnectActivity;
import com.cofe.solution.ui.device.picture.view.DevPictureActivity;
import com.cofe.solution.ui.device.push.view.DevPushService;
import com.cofe.solution.ui.dialog.LoaderDialog;
import com.cofe.solution.ui.user.login.view.UserLoginActivity;
import com.cofe.solution.ui.user.modify.view.DevMeActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.lib.EFUN_ERROR;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.sdk.bean.share.OtherShareDevUserBean;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.account.XMAccountManager;
import com.manager.account.share.ShareManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.manager.device.config.PwdErrorManager;
import com.utils.XUtils;
import com.xm.activity.device.devset.ability.view.XMDevAbilityActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.dialog.EditDialog;

import java.util.ArrayList;
import java.util.HashMap;

import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.adapter.DevListAdapter;
import com.cofe.solution.ui.device.add.AddNewDeviceActivity;
import com.cofe.solution.ui.device.add.list.listener.DevListConnectContract;
import com.cofe.solution.ui.device.add.list.presenter.DevListConnectPresenter;
import com.cofe.solution.ui.device.add.share.ShareFirstScren;
import com.cofe.solution.ui.device.add.share.view.ShareDevListActivity;
import com.cofe.solution.ui.device.alarm.view.DevAlarmMsgActivity;
import com.cofe.solution.ui.device.cloud.view.CloudStateActivity;
import com.cofe.solution.ui.device.config.DeviceSetting;
import com.cofe.solution.ui.device.config.interdevlinkage.view.InterDevLinkageActivity;
import com.cofe.solution.ui.device.config.shadow.view.DevShadowConfigActivity;
import com.cofe.solution.ui.device.config.simpleconfig.view.DevSimpleConfigActivity;
import com.cofe.solution.ui.device.preview.view.DevMonitorActivity;
import com.cofe.solution.ui.device.push.view.DevPushActivity;
import com.cofe.solution.ui.device.record.view.DevRecordActivity;

import io.reactivex.annotations.Nullable;

import static com.manager.account.share.ShareInfo.SHARE_NOT_YET_ACCEPT;
import static com.manager.db.Define.LOGIN_BY_LOCAL;
import static com.xm.ui.dialog.PasswordErrorDlg.INPUT_TYPE_DEV_USER_PWD;


/**
 * 设备界面,显示相关的列表菜单
 * Device interface, showing the relevant list menu
 * Created by jiangping on 2018-10-23.
 */
public class DevListActivity extends DemoBaseActivity<DevListConnectPresenter>
        implements DevListConnectContract.IDevListConnectView,
        XTitleBar.OnRightClickListener,
        DevListAdapter.OnItemDevClickListener {
    String TAG = "DevListActivity";
    int CAMERA_PERMISSION_REQEST_CODE = 111;
    int WRITE_EXTERNAL_STORAGE = 333;
    int NOTIFICATION_PERMISSION_REQUEST_CODE = 222;
    private RecyclerView listView;
    private DevListAdapter adapter;
    private SwipeRefreshLayout slRefresh;
    LinearLayout noDeviceContLl;
    TextView textTxtv;
    ImageView add_img;
    LinearLayout logoutLl;
    int onUpdateCount = 0;
    int onUppdateDevStateCount = 0;

    boolean isGridLayout = false; // Default is Linear

    LoaderDialog loaderDialog;

    // simran declare
    int popup_state = 0;
    String thumbnails_text = "Thumbnail Mode";
    SharedPreference cookies;
    Handler handler;
    boolean isPopupMenuOpen = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_list);
        cookies = new SharedPreference(getApplicationContext());
        checkLoginStatus();
    }


    void checkLoginStatus(){
        if (!DevDataCenter.getInstance().isLoginByAccount()) {
            if (DevDataCenter.getInstance().getAccountUserName() != null) {
                if (DevDataCenter.getInstance().getAccessToken() == null) {
                    AccountManager.getInstance().xmLogin(DevDataCenter.getInstance().getAccountUserName(), DevDataCenter.getInstance().getAccountPassword(), 1,
                            new BaseAccountManager.OnAccountManagerListener() {
                                @Override
                                public void onSuccess(int msgId) {
                                    Log.d("Access toekn", " > " + DevDataCenter.getInstance().getAccessToken());
                                    if (DevDataCenter.getInstance().isLoginByAccount()) {
                                        initView();
                                        initData();

                                        ShareManager manager = ShareManager.getInstance(getApplicationContext());
                                    } else {
                                        Log.d(TAG,"ShareManager > manager is null or values not set ");
                                    }
                                }

                                @Override
                                public void onFailed(int msgId, int errorId) {
                                }

                                @Override
                                public void onFunSDKResult(Message msg, MsgContent ex) {

                                }
                            });//LOGIN_BY_INTERNET（1）  Account login type

                }

            } else {
                finish();
                startActivity(new Intent(this, UserLoginActivity.class));
            }
        } else {
            initView();
            initData();

        }
    }
    private void initView() {
        loaderDialog = new LoaderDialog(this);
        loaderDialog.setMessage("Please wait...");


        /*titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.set_list));
        //titleBar.setRightTitleText(getString(R.string.share_dev_list));
        //titleBar.setBottomTip(DevListActivity.class.getName());
        titleBar.setLeftClick(this);
        titleBar.setRightIvClick(this);
        titleBar.setRightTvClick(this);*/
        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.set_list));
        add_img = findViewById(R.id.add_img);
        //如果不是账号登录，需要隐藏分享功能改成批量删除设备功能


        listView = findViewById(R.id.listViewDevice);
        noDeviceContLl = findViewById(R.id.no_device_cont_ll);
        textTxtv = findViewById(R.id.text_txtv);
        LinearLayoutManager llManager = new LinearLayoutManager(this);
        listView.setLayoutManager(llManager);

        //listView.addItemDecoration(new DividerItemDecoration(this, llManager.getOrientation()));
//        listView.removeItemDecorationAt(0); // Remove any existing decoration

        slRefresh = findViewById(R.id.sl_refresh);
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.updateDevState();
                slRefresh.setRefreshing(true);
            }
        });
        noDeviceContLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DevListActivity.this, AddNewDeviceActivity.class);
                startActivity(i);
            }
        });
        refreshTitle();

        ImageButton menuIcon = findViewById(R.id.menu_icon);
        ImageButton ibThumbnail = findViewById(R.id.ibThumbnail);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v); // Show menu popup
            }
        });

        ibThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showThumbnailsPopupMenu(ibThumbnail);
                isPopupMenuOpen = true;
                //showPopupMenu(v); // Show menu popup
            }
        });

        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        // Example: Add a listener to log scroll changes
        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling down
                    appBarLayout.setExpanded(false, true);
                } else {
                    // Scrolling up
                    appBarLayout.setExpanded(true, true);
                }
            }
        });
        appBarLayout.setElevation(0);
        appBarLayout.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        checkPushNotificationPermission();

        logoutLl = findViewById(R.id.logout_ll);

        logoutLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// Account logout

                XMAccountManager.getInstance().logout();
                if (XMAccountManager.getInstance().getUserName() == null) {
                    cookies.saveLoginStatus(1);

                    Intent intent = new Intent(DevListActivity.this, UserLoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        LinearLayout homeLL = findViewById(R.id.home_ll);
        homeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// Account logout
                Intent intent = new Intent(DevListActivity.this, DevListActivity.class);
                startActivity(intent);
                finish();
            }
        });


        LinearLayout imageLl = findViewById(R.id.image_ll);
        imageLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// Account logout
                turnToActivity(DevPictureActivity.class);
            }
        });

        LinearLayout meLl = findViewById(R.id.me_ll);
        meLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // Account logout
                // turnToActivity(DevMeActivity.class);
                Intent intent = new Intent(DevListActivity.this, DevMeActivity.class);
                startActivity(intent);
            }
        });

        cookies.saveLoginStatus(0);
        checkExternalStoragePermission();
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.toolbar_menu, popupMenu.getMenu()); // Inflate menu XML
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.option1:
                        Intent i = new Intent(DevListActivity.this, AddNewDeviceActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.option2:
                        checkCameraPermission();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();

    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            openCamera();
        } else {
            // Show permission explanation popup
            showPermissionExplanationPopup("Camera", "This app needs camera access to take photos. Please grant the permission to proceed.");
        }
    }

    private void checkExternalStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {

                showPermissionExplanationPopup("WRITE EXTERNAL STORAGE", " Require to save the screenshot and videos from  device to your mobile phone. Please grant the permission to proceed.");
            }
        } else {
            // Lower versions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                showPermissionExplanationPopup("WRITE EXTERNAL STORAGE", " Require to save the screenshot and videos from  device to your mobile phone. Please grant the permission to proceed.");
            }
        }
    }

    private void showPermissionExplanationPopup(String permissionName, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(permissionName + " Permission Required")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Accept", (dialog, which) -> {
                    if (permissionName.equals("Camera")) {
                        // Request camera permission
                        // Request camera permission
                        ActivityCompat.requestPermissions(DevListActivity.this,
                                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQEST_CODE);
                    } else if (permissionName.equals("WRITE EXTERNAL STORAGE")) {
                        // Request camera permission
                        // Request camera permission
                        ActivityCompat.requestPermissions(DevListActivity.this,
                                new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO}, WRITE_EXTERNAL_STORAGE);
                        requestPermissions(new String[]{
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VIDEO
                        }, WRITE_EXTERNAL_STORAGE);

                    } else {
                        // Request camera permission
                        ActivityCompat.requestPermissions(
                                DevListActivity.this,
                                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                NOTIFICATION_PERMISSION_REQUEST_CODE
                        );

                    }
                })
                .setNegativeButton("Reject", (dialog, which) -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                openCamera();
            } else {
                // Permission denied
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    // Show the explanation again
                    Toast.makeText(this, "Permission is required to use the camera.", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission denied with "Do Not Ask Again"
                    showSettingsRedirectPopup("Camera");
                }
            }
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                enablePushNotifications();

            } else {
                // Permission denied
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                    // Show the explanation again
                    Toast.makeText(this, "Permission is required for push notifications.", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission denied with "Do Not Ask Again"
                    showSettingsRedirectPopup("Push Notification ");
                }
            }
        } else if (requestCode == WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "Permission is required to save screenshot and videos from  the camera device to your mobile phone.", Toast.LENGTH_SHORT).show();
                } else {
                    showSettingsRedirectPopup("Write External Storage");
                }
            }
        }

    }

    private void showSettingsRedirectPopup(String permisonName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(permisonName + " Permission Required")
                .setMessage(permisonName + " permission is permanently denied. Please enable it in the app settings.")
                .setCancelable(false)
                .setPositiveButton("Open Settings", (dialog, which) -> openAppSettings())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    private void openCamera() {
        Toast.makeText(this, "Camera is now accessible.", Toast.LENGTH_SHORT).show();
        Intent j = new Intent(DevListActivity.this, DevSnConnectActivity.class);
        startActivity(j);
    }


    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    private void initData() {
        loaderDialog.setMessage("");
        handler =  new Handler();
        //showWaitDialog();
        adapter = new DevListAdapter(getApplication(), listView, (ArrayList<HashMap<String, Object>>) presenter.getDevList(), this);
        listView.setAdapter(adapter);
        presenter.updateDevState();//Update the status of the list
    }


    @Override
    public DevListConnectPresenter getPresenter() {
        return new DevListConnectPresenter(this);
    }

    private void refreshTitle() {
        if (null != titleBar) {
            if (DevDataCenter.getInstance().isLoginByAccount()) {

            } else {
                int loginType = DevDataCenter.getInstance().getLoginType();
                if (loginType == LOGIN_BY_LOCAL) {

                }
            }
        }
    }

    @Override
    public void onRightClick() {
        if (DevDataCenter.getInstance().isLoginByAccount()) {
            turnToActivity(ShareDevListActivity.class);
        } else {
            XMPromptDlg.onShow(this, getString(R.string.is_sure_delete_all_devices), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.deleteAllDevs();
                }
            }, null);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart");
        if (adapter != null) {
            if (presenter != null) {
                Bundle bundle = new Bundle();
                bundle.putString("called", "onRestart");
                bundle.putBoolean("userLogged", DevDataCenter.getInstance().isLoginByAccount());
                bundle.putString("username", (DevDataCenter.getInstance().getAccountUserName() != null) ? DevDataCenter.getInstance().getAccountUserName() : "blank");
                bundle.putString("token", (DevDataCenter.getInstance().getAccessToken() != null) ? DevDataCenter.getInstance().getAccessToken() : "blank");
                bundle.putInt("device", presenter.getDevList().size());
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                if (DevDataCenter.getInstance().getAccountUserName() != null) {
                    if (DevDataCenter.getInstance().getAccessToken() == null) {
                        AccountManager.getInstance().xmLogin(DevDataCenter.getInstance().getAccountUserName(), DevDataCenter.getInstance().getAccountPassword(), 1,
                                new BaseAccountManager.OnAccountManagerListener() {
                                    @Override
                                    public void onSuccess(int msgId) {
                                        Log.d("Access toekn", " > " + DevDataCenter.getInstance().getAccessToken());
                                        if (presenter.getDevList() != null) {
                                            if (presenter.getDevList().size() > 0) {
                                                if (noDeviceContLl != null) {
                                                    noDeviceContLl.setVisibility(View.GONE);
                                                }
                                            }
                                        }

                                    }

                                    @Override
                                    public void onFailed(int msgId, int errorId) {
                                        if (presenter.getDevList() != null) {
                                            if (presenter.getDevList().size() > 0) {
                                                if (noDeviceContLl != null) {
                                                    noDeviceContLl.setVisibility(View.GONE);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFunSDKResult(Message msg, MsgContent ex) {

                                    }
                                });//LOGIN_BY_INTERNET（1）  Account login type

                    } else {
                        if (presenter.getDevList() != null) {
                            if (presenter.getDevList().size() > 0) {
                                if (noDeviceContLl != null) {
                                    noDeviceContLl.setVisibility(View.GONE);
                                }
                            }
                        }


                    }

                } else {
                    finish();
                    startActivity(new Intent(this, UserLoginActivity.class));
                }


            }
            adapter.setData((ArrayList<HashMap<String, Object>>) presenter.getDevList());
        }
    }

    boolean initDataCalled = false;

    @Override
    public void onUpdateDevListView() {
        Log.d(TAG,"onUpdateDevListView");
        cookies.saveDevList(presenter.getDevList());
        slRefresh.setRefreshing(false);

        Bundle bundle = new Bundle();
        bundle.putString("called", "onUpdateDevListView");
        bundle.putBoolean("userLogged", DevDataCenter.getInstance().isLoginByAccount());
        bundle.putString("username", (DevDataCenter.getInstance().getAccountUserName() != null) ? DevDataCenter.getInstance().getAccountUserName() : "blank");
        bundle.putString("token", (DevDataCenter.getInstance().getAccessToken() != null) ? DevDataCenter.getInstance().getAccessToken() : "blank");
        bundle.putInt("device", (presenter != null) ? presenter.getDevList().size() : 0);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        Log.d("user Data", " username > " + DevDataCenter.getInstance().getAccountUserName());
        Log.d("user Data", " password > " + DevDataCenter.getInstance().getAccountPassword());
        Log.d("user Data", "Access Token > " + DevDataCenter.getInstance().getAccessToken());


        if (onUppdateDevStateCount < 1) {
            onUppdateDevStateCount = onUppdateDevStateCount + 1;
            if (!DevDataCenter.getInstance().isLoginByAccount()) {
                // titleBar.setRightTitleText(getString(R.string.clear_dev_list));
                if (DevDataCenter.getInstance().getAccountUserName() != null) {
                    if (DevDataCenter.getInstance().getAccessToken() == null) {
                        AccountManager.getInstance().xmLogin(DevDataCenter.getInstance().getAccountUserName(), DevDataCenter.getInstance().getAccountPassword(), 1,
                                new BaseAccountManager.OnAccountManagerListener() {
                                    @Override
                                    public void onSuccess(int msgId) {
                                        Log.d("Access toekn", " > " + DevDataCenter.getInstance().getAccessToken());
                                        if (!initDataCalled) {
                                            initData();
                                            initDataCalled = true;
                                        } else {
                                            if (presenter.getDevList() != null) {
                                                if (presenter.getDevList().size() == 0) {
                                                    add_img.setVisibility(View.VISIBLE);
                                                    noDeviceContLl.setVisibility(View.VISIBLE);
                                                    textTxtv.setText(getString(R.string.add_dev));

                                                } else {
                                                    noDeviceContLl.setVisibility(View.GONE);
                                                }
                                            }
                                        }

                                    }

                                    @Override
                                    public void onFailed(int msgId, int errorId) {
                                        finish();
                                        startActivity(new Intent(DevListActivity.this, UserLoginActivity.class));
                                    }

                                    @Override
                                    public void onFunSDKResult(Message msg, MsgContent ex) {

                                    }
                                });

                    } else {
                        if (!initDataCalled) {
                            initData();
                            initDataCalled = true;
                        } else {
                            if (presenter.getDevList() != null) {
                                if (presenter.getDevList().size() == 0) {
                                    add_img.setVisibility(View.VISIBLE);
                                    noDeviceContLl.setVisibility(View.VISIBLE);
                                    textTxtv.setText(getString(R.string.add_dev));

                                } else {
                                    noDeviceContLl.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                } else {
                    finish();
                    startActivity(new Intent(this, UserLoginActivity.class));
                }
            }
        } else {
            finish();
            startActivity(new Intent(this, UserLoginActivity.class));
        }


        adapter.setData((ArrayList<HashMap<String, Object>>) presenter.getDevList());
    }

    @Override
    public void onUpdateDevStateResult(boolean isSuccess) {//Repeated the walk many times
        Log.d(TAG,"onUpdateDevStateResult");
        cookies.saveDevList(presenter.getDevList());

        //hideWaitDialog();
        loaderDialog.dismiss();

        Bundle bundle = new Bundle();
        bundle.putString("called", "onUpdateDevStateResult");
        bundle.putBoolean("userLogged", DevDataCenter.getInstance().isLoginByAccount());
        bundle.putString("username", (DevDataCenter.getInstance().getAccountUserName() != null) ? DevDataCenter.getInstance().getAccountUserName() : "blank");
        bundle.putString("token", (DevDataCenter.getInstance().getAccessToken() != null) ? DevDataCenter.getInstance().getAccessToken() : "blank");
        bundle.putInt("device", (presenter != null) ? presenter.getDevList().size() : 0);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        slRefresh.setRefreshing(false);
        if (isSuccess) {
            adapter.setData((ArrayList<HashMap<String, Object>>) presenter.getDevList());
        }
        if (presenter.getDevList() != null) {
            if (presenter.getDevList().size() == 0) {
                add_img.setVisibility(View.VISIBLE);
                noDeviceContLl.setVisibility(View.VISIBLE);
            } else {
                noDeviceContLl.setVisibility(View.GONE);
                textTxtv.setText(getString(R.string.Refresh_Dev_Status_F));
            }
        }
    }

    @Override
    public void onModifyDevNameFromServerResult(boolean isSuccess) {
        hideWaitDialog();
        if (isSuccess) {
            showToast(getString(R.string.TR_Modify_Dev_Name_S), Toast.LENGTH_LONG);
            adapter.setData((ArrayList<HashMap<String, Object>>) presenter.getDevList());
        } else {
            showToast(getString(R.string.TR_Modify_Dev_Name_F), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onDeleteDevResult(boolean isSuccess) {
        hideWaitDialog();
        adapter.setData((ArrayList<HashMap<String, Object>>) presenter.getDevList());
        if (presenter.getDevList() != null) {
            if (presenter.getDevList().size() == 0) {
                add_img.setVisibility(View.VISIBLE);
                noDeviceContLl.setVisibility(View.VISIBLE);
                textTxtv.setText(getString(R.string.add_dev));

            } else {
                noDeviceContLl.setVisibility(View.GONE);
            }
        }

        if (isSuccess) {
            showToast(getString(R.string.delete_s), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.delete_f), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onAcceptDevResult(boolean isSuccess) {
        hideWaitDialog();
        adapter.setData((ArrayList<HashMap<String, Object>>) presenter.getDevList());
        if (isSuccess) {
            showToast(getString(R.string.accept_share_s), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.accept_share_f), Toast.LENGTH_LONG);
        }
    }

    /**
     * 获取通道列表回调
     * get the channel list callback
     *
     * @param isSuccess
     * @param resultId
     */
    @Override
    public void onGetChannelListResult(boolean isSuccess, int resultId) {
        hideWaitDialog();
        if (isSuccess) {
            //如果返回的数据是通道数并且大于1就跳转到通道列表
            /*If the number of channels returned is greater than 1, jump to the list of channels*/
            if (resultId > 1) {
                turnToActivity(ChannelListActivity.class);
            } else {

                //turnToActivity(DevActivity.class);

                turnToActivity(DevMonitorActivity.class);
            }
        } else {
            if (resultId == EFUN_ERROR.EE_DVR_PASSWORD_NOT_VALID) {
                XMDevInfo devInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
                XMPromptDlg.onShowPasswordErrorDialog(this, devInfo.getSdbDevInfo(),
                        0, new PwdErrorManager.OnRepeatSendMsgListener() {
                            @Override
                            public void onSendMsg(int msgId) {
                                loaderDialog.setMessage("");


                                //showWaitDialog();
                                presenter.getChannelList();
                            }
                        }, false);
            } else if (resultId == EFUN_ERROR.EE_DVR_LOGIN_USER_NOEXIST) {
                XMDevInfo devInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
                XMPromptDlg.onShowPasswordErrorDialog(this, devInfo.getSdbDevInfo(),
                        0, getString(R.string.input_username_password), INPUT_TYPE_DEV_USER_PWD, true, new PwdErrorManager.OnRepeatSendMsgListener() {
                            @Override
                            public void onSendMsg(int msgId) {
                                loaderDialog.setMessage("");

                                //showWaitDialog();
                                presenter.getChannelList();
                            }
                        }, false);
            } else if (resultId < 0) {
                showToast(getString(R.string.login_dev_failed) + resultId, Toast.LENGTH_LONG);
                //turnToActivity(DevMonitorActivity.class);
            }
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onItemClick(int position, XMDevInfo xmDevInfo) {
        xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) presenter.getDevList().get(position).get("devId"));
        if (xmDevInfo.getDevState() != 0) {


            //判断是否为分享设备
            SharedPreference cookies = new SharedPreference(getApplication());
            cookies.saveDevName(xmDevInfo.getDevName());

            if (xmDevInfo.isShareDev()) {
                OtherShareDevUserBean otherShareDevUserBean = xmDevInfo.getOtherShareDevUserBean();
                if (otherShareDevUserBean != null) {
                    int iShareState = otherShareDevUserBean.getShareState();
                    if (iShareState == SHARE_NOT_YET_ACCEPT) {
                        XMPromptDlg.onShow(getContext(), getString(R.string.is_accept_share_dev), getString(R.string.reject_share), getString(R.string.accept_share), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                presenter.rejectShare(otherShareDevUserBean);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                presenter.acceptShare(otherShareDevUserBean);
                            }
                        });
                        return;
                    }
                }
            }

            //判断设备是否在线
            if (xmDevInfo.getDevState() != XMDevInfo.OFF_LINE) {
                if (xmDevInfo.getDevState() == XMDevInfo.SLEEP_UNWAKE) {
                    showToast(getString(R.string.dev_unwake), Toast.LENGTH_LONG);
                    presenter.setDevId(xmDevInfo.getDevId());
                    turnToActivity(DevShadowConfigActivity.class);
                    return;
                }
                loaderDialog.setMessage("");


                //showWaitDialog(getString(R.string.get_channel_info));
                String devId = presenter.getDevId(position);
                presenter.setDevId(devId);

                //低功耗设备不需要获取通道列表，直接跳转到预览页面
                /*Low power devices do not need to get the list of channels and jump directly to the preview page*/
                if (DevDataCenter.getInstance().isLowPowerDev(xmDevInfo.getDevType())) {
                    turnToActivity(DevMonitorActivity.class);
                } else {
                    presenter.getChannelList();
                }
            } else {
                showToast(FunSDK.TS(getString(R.string.dev_offline)), Toast.LENGTH_LONG);
                presenter.setDevId(xmDevInfo.getDevId());
                //turnToActivity(DevShadowConfigActivity.class);
            }
        } else {
            showToast(getString(R.string.dev_offline), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public boolean onLongItemClick(final int position, XMDevInfo xmDevInfo) {
        xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) presenter.getDevList().get(position).get("devId"));
        //if(xmDevInfo.getDevState() !=0) {

        XMPromptDlg.onShow(this, getString(R.string.is_sure_delete_dev), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                presenter.deleteDev(position);
            }
        }, null);
        return false;
        /*} else {
            showToast(getString(R.string.dev_offline), Toast.LENGTH_SHORT);
            return false;
        }*/
    }

    /**
     * 跳转到报警消息
     * jump to alarm message
     *
     * @param position
     */
    @Override
    public void onTurnToAlarmMsg(int position, XMDevInfo xmDevInfo) {// This is push messaging
        xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) presenter.getDevList().get(position).get("devId"));

        if (xmDevInfo.getDevState() != 0) {

            String devId = presenter.getDevId(position);
            presenter.setDevId(devId);
            turnToActivity(DevAlarmMsgActivity.class);
        } else {
            showToast(getString(R.string.dev_offline), Toast.LENGTH_SHORT);
        }
    }

    /**
     * 跳转到云服务
     * jump to alarm message
     *
     * @param position
     */
    @Override
    public void onTurnToCloudService(int position, XMDevInfo xmDevInfo) {
        xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) presenter.getDevList().get(position).get("devId"));
        if (xmDevInfo.getDevState() != 0) {
            String devId = presenter.getDevId(position);
            presenter.setDevId(devId);
            turnToActivity(CloudStateActivity.class);
        } else {
            showToast(getString(R.string.dev_offline), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void openSettingActivity(int position, XMDevInfo xmDevInfo) {
        xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) presenter.getDevList().get(position).get("devId"));
        if (xmDevInfo.getDevState() != 0) {

            String devId = presenter.getDevId(position);
            presenter.setDevId(devId);

            checkDeviceLoginStatus(xmDevInfo.getDevId(),xmDevInfo);
        } else {
            showToast(getString(R.string.dev_offline), Toast.LENGTH_SHORT);
        }
    }


    void checkDeviceLoginStatus(String devId, XMDevInfo xmDevInfo){
        DeviceManager.getInstance().loginDev(devId, new DeviceManager.OnDevManagerListener() {

            /**
             *Successful callback
             *
             *@ param devId Device Type
             *@ param operationType operation type
             *@ param result result
             */

            @Override
            public void onSuccess(String devId, int operationType, Object o) {
                Gson gson = new Gson();
                String personJson = gson.toJson(xmDevInfo);

                // Pass the JSON string to another activity via Intent
                Intent intent = new Intent(DevListActivity.this, DeviceSetting.class);
                intent.putExtra("dev", personJson);
                startActivity(intent);
                //turnToActivity(DeviceSetting.class);
            }

            /**
             *Failed callback
             *
             *@ param devId device serial number
             *@ param msgId Message ID
             * @param jsonName
             *@ param errorId error code
             */

            @Override

            public void onFailed(String devId, int msgId, String jsonName, int i1) {
                Log.d(TAG, "device login failed  > " + i1  +"  | jsonName > " +jsonName);
            }

        });


    }
    /**
     * 跳转到推送设置
     * Jump to push Settings
     *
     * @param position
     */
    @Override
    public void onTurnToPushSet(int position, XMDevInfo xmDevInfo) {
        xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) presenter.getDevList().get(position).get("devId"));
        if (xmDevInfo.getDevState() != 0) {

            /*String devId = presenter.getDevId(position);
            presenter.setDevId(devId);
            turnToActivity(DevPushActivity.class);*/
            DevConfigManager devConfigManager = DeviceManager.getInstance().getDevConfigManager(xmDevInfo.getDevId());
            boolean isManualAlarmOpen = true;
            //将开启/关闭 bool状态转成16进制字符串
            // Convert the bool state of opening/closing to hexadecimal string.
            String manualAlarmSwitch = isManualAlarmOpen ? "0x00000001" : "0x00000000";
            DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String devId, int msgId, Object result) {
                    //result是json数据
                    //result is JSON data
                    Log.d(TAG ,"manuall notification true");
                    showToast(getString(R.string.manuall_notification_Active), Toast.LENGTH_SHORT);
                }

                @Override
                public void onFailed(String devId, int msgId, String s1, int errorId) {
                    //获取失败，通过errorId分析具体原因
                    //Failed to retrieve, analyze the specific reason through errorId
                    Log.d(TAG, "manuall notification false");
                    showToast(getString(R.string.manuall_notification_failed), Toast.LENGTH_SHORT);

                }
            });

            devConfigInfo.setJsonName("OPRemoteCtrl");
            devConfigInfo.setChnId(-1);
            devConfigInfo.setCmdId(4000);

            HashMap<String, Object> sendMap = new HashMap<>();
            HashMap<String, Object> remoteCtrlMap = new HashMap<>();
            remoteCtrlMap.put("Type", "ManuIntelAlarm");
            remoteCtrlMap.put("msg", manualAlarmSwitch);
            remoteCtrlMap.put("P1", "0x00000000");
            remoteCtrlMap.put("P2", "0x00000000");

            sendMap.put("Name", "OPRemoteCtrl");
            sendMap.put("OPRemoteCtrl", remoteCtrlMap);
            sendMap.put("SessionID", "0x0000001b");

            //设置保存的Json数据
            //Set the saved JSON data.
            devConfigInfo.setJsonData(new Gson().toJson(sendMap));
            devConfigManager.setDevCmd(devConfigInfo);


        } else {
            showToast(getString(R.string.dev_offline), Toast.LENGTH_SHORT);

        }
    }

    /**
     * 修改设备名
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onModifyDevName(int position, XMDevInfo xmDevInfo) {
        xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) presenter.getDevList().get(position).get("devId"));
        if (xmDevInfo.getDevState() != 0) {

            XMPromptDlg.onShowEditDialog(this, getString(R.string.modify_dev_name), xmDevInfo.getDevName(), new EditDialog.OnEditContentListener() {
                @Override
                public void onResult(String devName) {
                    presenter.modifyDevNameFromServer(position, devName);
                }
            });
        } else {
            showToast(getString(R.string.dev_offline), Toast.LENGTH_SHORT);

        }
    }

    /**
     * 分享管理
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onShareDevManage(int position, XMDevInfo xmDevInfo) {
        xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) presenter.getDevList().get(position).get("devId"));
        if (xmDevInfo.getDevState() != 0) {

            presenter.setDevId(xmDevInfo.getDevId());
            Gson gson = new Gson();
            String personJson = gson.toJson(xmDevInfo);

            // Pass the JSON string to another activity via Intent
            //Intent intent = new Intent(this, ShareFirstScren.class);
            Intent intent = new Intent(this, ShareFirstScren.class);
            intent.putExtra("dev", personJson);
            startActivity(intent);

            //turnToActivity(DevShareManageActivity.class);
        } else {
            showToast(getString(R.string.dev_offline), Toast.LENGTH_SHORT);

        }
    }

    /**
     * 跳转到设备本地用户名和密码页面
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onTurnToEditLocalDevUserPwd(int position, XMDevInfo xmDevInfo) {
        xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) presenter.getDevList().get(position).get("devId"));
        if (xmDevInfo.getDevState() != 0) {

            View layout = LayoutInflater.from(this).inflate(R.layout.dialog_local_dev_user_pwd, null);
            TextView tvDevId = layout.findViewById(R.id.tv_devid);
            tvDevId.setText(xmDevInfo.getDevId());
            EditText etDevUser = layout.findViewById(R.id.et_local_dev_user);
            etDevUser.setText(xmDevInfo.getDevUserName());
            EditText etDevPwd = layout.findViewById(R.id.et_local_dev_pwd);
            etDevPwd.setText(xmDevInfo.getDevPassword());

            Dialog dialog = XMPromptDlg.onShow(this, layout,
                    (int) (XUtils.getScreenWidth(this) * 0.8), 0, false, null);

            dialog.show();

            Button btnOk = layout.findViewById(R.id.btn_ok);
            XMDevInfo finalXmDevInfo = xmDevInfo;
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String devUserName = etDevUser.getText().toString().trim();
                    String devPwd = etDevPwd.getText().toString().trim();
                    presenter.editLocalDevUserPwd(
                            position,
                            finalXmDevInfo.getDevId(),
                            devUserName,
                            devPwd);
                    dialog.dismiss();
                }
            });

            Button btnCancel = layout.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        } else {
            showToast(getString(R.string.dev_offline), Toast.LENGTH_SHORT);
        }
    }

    /**
     * 唤醒设备并开锁
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onWakeUpDev(int position, XMDevInfo xmDevInfo) {
        xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) presenter.getDevList().get(position).get("devId"));
        if (xmDevInfo.getDevState() != 0) {
            presenter.wakeUpDev(position, xmDevInfo.getDevId());
        } else {
            showToast(getString(R.string.dev_offline), Toast.LENGTH_SHORT);
        }
    }

    /**
     * 跳转到SD卡录像回放页面
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onTurnToSdPlayback(int position, XMDevInfo xmDevInfo) {
        xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) presenter.getDevList().get(position).get("devId"));
        if (xmDevInfo.getDevState() != 0) {
            presenter.setDevId(xmDevInfo.getDevId());
            turnToActivity(DevRecordActivity.class);
        } else {
            showToast(getString(R.string.dev_offline), Toast.LENGTH_SHORT);
        }
    }

    /**
     * 跳转到设备之间联动
     *
     * @param position
     * @param xmDevInfo
     * @param bundle
     */
    @Override
    public void onTurnToInterDevLinkage(int position, XMDevInfo xmDevInfo, Bundle bundle) {
        xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) presenter.getDevList().get(position).get("devId"));
        if (xmDevInfo.getDevState() != 0) {
            presenter.setDevId(xmDevInfo.getDevId());
            turnToActivity(InterDevLinkageActivity.class, "data", bundle);
        } else {
            showToast(getString(R.string.dev_offline), Toast.LENGTH_SHORT);
        }
    }

    /**
     * 跳转到设备能力集页面
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onTurnToDevAbility(int position, XMDevInfo xmDevInfo) {
        xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) presenter.getDevList().get(position).get("devId"));
        if (xmDevInfo.getDevState() != 0) {
            presenter.setDevId(xmDevInfo.getDevId());
            turnToActivity(XMDevAbilityActivity.class);
        } else {
            showToast(getString(R.string.dev_offline), Toast.LENGTH_SHORT);
        }
    }

    /**
     * 从服务器获取设备Token
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onToGetDevTokenFromServer(int position, XMDevInfo xmDevInfo) {
        xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) presenter.getDevList().get(position).get("devId"));
        if (xmDevInfo.getDevState() != 0) {
            presenter.getDevTokenFromServer(xmDevInfo.getDevId());
        } else {
            showToast(getString(R.string.dev_offline), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onPingTest(int position, XMDevInfo xmDevInfo) {
        turnToActivity(DevSimpleConfigActivity.class, new Object[][]{{"devId", xmDevInfo.getDevId()}, {"jsonName", "Ping"}, {"configName", "Ping"}, {"cmdId", 1052}, {"jsonData", "{\n" +
                "    \"Ping\": {\n" +
                "        \"URL\": \"\",\n" +
                "        \"Num\": 10,\n" +
                "        \"Timeout\": 5\n" +
                "    },\n" +
                "    \"Name\": \"Ping\"\n" +
                "}"}});
    }


    // 2 times device name show > preview does not come
    // preview
    // app open me > user id > logout > device  >
    // first step scan > share icon doe snot show >
    // after login account again > device is able to delete and share icon shows
    // qr scan > white screen > device name > loading >
    //

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.release();
        }
        if(presenter!=null){
            presenter.clear();

        }
    }


    private void checkPushNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // Notifications are auto-enabled for Android 12 and below
            enablePushNotifications();
            startService(new Intent(this, DevPushService.class));

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // Permission already granted
                enablePushNotifications();
                startService(new Intent(this, DevPushService.class));

            } else {
                // Show permission explanation popup
                showPermissionExplanationPopup("Push Notification ", "This app needs your permission to send push notifications. Please grant the permission to stay updated.");
            }
        }
    }

    private void enablePushNotifications() {
        Toast.makeText(this, "Push notifications enabled.", Toast.LENGTH_SHORT).show();
        // Add your logic for handling push notifications here (e.g., subscribing to topics)
        startService(new Intent(this, DevPushService.class));

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        if (!DevDataCenter.getInstance().isLoginByAccount()) {
            // titleBar.setRightTitleText(getString(R.string.clear_dev_list));

            if (DevDataCenter.getInstance().getAccountUserName() != null) {
                if (DevDataCenter.getInstance().getAccessToken() == null) {
                    AccountManager.getInstance().xmLogin(DevDataCenter.getInstance().getAccountUserName(), DevDataCenter.getInstance().getAccountPassword(), 1,
                            new BaseAccountManager.OnAccountManagerListener() {
                                @Override
                                public void onSuccess(int msgId) {
                                    Log.d("Access toekn", " > " + DevDataCenter.getInstance().getAccessToken());
                                    Log.d(TAG, "onResume > isPopupMenuOpen > " +isPopupMenuOpen);
                                        if(isPopupMenuOpen) {
                                            initData();
                                        }
                                }

                                @Override
                                public void onFailed(int msgId, int errorId) {
                                }

                                @Override
                                public void onFunSDKResult(Message msg, MsgContent ex) {

                                }
                            });
                }
            }
        }
        super.onResume();
    }

    // popu menu for thumnails
    public void showThumbnailsPopupMenu(View anchorView) {

        // Inflate the popup layout
        LayoutInflater inflater = LayoutInflater.from(anchorView.getContext());
        View popupView = inflater.inflate(R.layout.popup_thumbnails, null);

        LinearLayout llThumbnails = popupView.findViewById(R.id.llThumbnails);
        LinearLayout llDefault = popupView.findViewById(R.id.llDefault);
        LinearLayout llOnline = popupView.findViewById(R.id.llOnline);
        LinearLayout llOffline = popupView.findViewById(R.id.llOfline);

        LinearLayout llPopupThumbnails = popupView.findViewById(R.id.llPopupThumbnails);
        TextView tvOnlineOffline = popupView.findViewById(R.id.tvOnlineOffline);
        TextView tvDefault = popupView.findViewById(R.id.tvDefault);
        TextView tvThumbnails = popupView.findViewById(R.id.tvThumbnails);


        // Create a PopupWindow
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT ,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );

        RecyclerView.LayoutManager currentLayoutManager = listView.getLayoutManager();

        if (currentLayoutManager instanceof GridLayoutManager) {
            tvThumbnails.setText("Large Image View");
        } else {
            tvThumbnails.setText("Thumbnail Mode");
        }
        // popup_state 0 means default 1 means thumbnails and 2 means online offline
        if (popup_state == 0) {
            tvOnlineOffline.setTextColor(getResources().getColor(R.color.other_black));
            tvDefault.setTextColor(getResources().getColor(R.color.theme));
            tvThumbnails.setTextColor(getResources().getColor(R.color.other_black));
        }

        else if (popup_state == 2) {
            tvOnlineOffline.setTextColor(getResources().getColor(R.color.theme));
            tvDefault.setTextColor(getResources().getColor(R.color.other_black));
            tvThumbnails.setTextColor(getResources().getColor(R.color.other_black));
        }

        llThumbnails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_state = 1;
                sowLoader();
               /* tvOnlineOffline.setTextColor(getResources().getColor(R.color.other_black));
                tvDefault.setTextColor(getResources().getColor(R.color.other_black));
                tvThumbnails.setTextColor(getResources().getColor(R.color.other_black));
*/
                RecyclerView.LayoutManager currentLayoutManager = listView.getLayoutManager();
                //LinearLayoutManager llManager = new LinearLayoutManager(this);
                // listView.setLayoutManager(new GridLayoutManager(DevListActivity.this, 2));
                //adapter.notifyDataSetChanged();

                if (currentLayoutManager instanceof GridLayoutManager) {
                    tvThumbnails.setText("Thumbnail mode");

                    // Switch to LinearLayoutManager
                    listView.setLayoutManager(new LinearLayoutManager(DevListActivity.this));
                    adapter.notifyDataSetChanged();
                    isGridLayout = false;
                } else {
                    tvThumbnails.setText("Large Image View");
                    // Switch to GridLayoutManager with 2 columns
                    listView.setLayoutManager(new GridLayoutManager(DevListActivity.this, 2));
                    isGridLayout = true;
                }
                // adapter.setData((ArrayList<HashMap<String, Object>>) presenter.getDevList());
                popupWindow.dismiss();
            }
        });
        llPopupThumbnails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sowLoader();

                popupWindow.dismiss();
            }
        });

        llOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sowLoader();
                popup_state = 2;

                tvThumbnails.setTextColor(getResources().getColor(R.color.other_black));

                tvOnlineOffline.setTextColor(getResources().getColor(R.color.theme));
                tvDefault.setTextColor(getResources().getColor(R.color.other_black));
                filterDeviceList(1); // Show only online devices
                popupWindow.dismiss();
            }
        });

        llOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sowLoader();
                popup_state = 2;
                tvThumbnails.setTextColor(getResources().getColor(R.color.other_black));

                tvOnlineOffline.setTextColor(getResources().getColor(R.color.theme));
                tvDefault.setTextColor(getResources().getColor(R.color.other_black));
                filterDeviceList(0); // Show only online devices
                popupWindow.dismiss();
            }
        });

        llDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sowLoader();
                popup_state = 0;

                tvOnlineOffline.setTextColor(getResources().getColor(R.color.other_black));
                tvDefault.setTextColor(getResources().getColor(R.color.theme));
                tvThumbnails.setTextColor(getResources().getColor(R.color.other_black));
                popupWindow.dismiss();
                // LinearLayoutManager llManager = new LinearLayoutManager(DevListActivity.this);
                // listView.setLayoutManager(llManager);
                adapter.setData((ArrayList<HashMap<String, Object>>) presenter.getDevList());
                // adapter.notifyDataSetChanged();
            }
        });
        // Moves arrow above popup content
        // Set the background to ensure shadow visibility
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
// Show the popup
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(20);
        }

       /* // Get the screen height and calculate the height below the anchor view
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int location[] = new int[2];
        anchorView.getLocationOnScreen(location);
        int anchorY = location[1]; // Y position of the anchor view

        int availableHeight = screenHeight - anchorY; // Space remaining below the anchor

        // Set the popup height to available space below the anchor
        popupWindow.setHeight(availableHeight);*/
        // Show popup below the anchor with slight offset
        popupWindow.showAsDropDown(anchorView);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isPopupMenuOpen = false;
                loaderDialog.dismiss();
            }
        });
    }


    // simran Method to filter the list based on Online/Offline state
    private void filterDeviceList(int state) {
        ArrayList<HashMap<String, Object>> filteredList = new ArrayList<>();

        for (HashMap<String, Object> device : presenter.getDevList()) {
            int devState = (int) device.get("devState"); // Get device state
            Log.d("SIMRAN", String.valueOf(devState));

            if (devState == state) {
                filteredList.add(device);
            }
        }

        // Update existing adapter with new filtered data
        adapter.setData(filteredList);
    }

    void sowLoader() {
        loaderDialog.setMessage("");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loaderDialog.dismiss();

            }
        }, 2000);

    }

    void maunalNotification() {

    }

    @Override
    public void logout(){
        finish();
        startActivity(new Intent(this, UserLoginActivity.class));
    }

}