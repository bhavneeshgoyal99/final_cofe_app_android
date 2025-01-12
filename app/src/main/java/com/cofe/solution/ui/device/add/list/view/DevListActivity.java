package com.cofe.solution.ui.device.add.list.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cofe.solution.base.SharedPreference;
import com.cofe.solution.ui.device.add.sn.view.DevSnConnectActivity;
import com.cofe.solution.ui.device.picture.view.DevPictureActivity;
import com.cofe.solution.ui.device.preview.view.DevActivity;
import com.cofe.solution.ui.device.push.view.DevPushService;
import com.cofe.solution.ui.user.login.view.UserLoginActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.Firebase;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.lib.EFUN_ERROR;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.sdk.bean.share.OtherShareDevUserBean;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.account.XMAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.config.PwdErrorManager;
import com.utils.XUtils;
import com.xm.activity.device.devset.ability.view.XMDevAbilityActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.dialog.EditDialog;

import java.util.ArrayList;
import java.util.HashMap;

import com.cofe.solution.R;
import com.cofe.solution.base.CurvedBottomNavigationView;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.adapter.DevListAdapter;
import com.cofe.solution.ui.device.add.AddNewDeviceActivity;
import com.cofe.solution.ui.device.add.list.listener.DevListConnectContract;
import com.cofe.solution.ui.device.add.list.presenter.DevListConnectPresenter;
import com.cofe.solution.ui.device.add.qrcode.view.SetDevToRouterByQrCodeActivity;
import com.cofe.solution.ui.device.add.share.ShareFirstScren;
import com.cofe.solution.ui.device.add.share.view.DevShareManageActivity;
import com.cofe.solution.ui.device.add.share.view.ShareDevListActivity;
import com.cofe.solution.ui.device.add.share.view.ShareDevToOtherAccountActivity;
import com.cofe.solution.ui.device.alarm.view.DevAlarmMsgActivity;
import com.cofe.solution.ui.device.cloud.view.CloudStateActivity;
import com.cofe.solution.ui.device.config.DeviceSetting;
import com.cofe.solution.ui.device.config.interdevlinkage.view.InterDevLinkageActivity;
import com.cofe.solution.ui.device.config.shadow.view.DevShadowConfigActivity;
import com.cofe.solution.ui.device.config.simpleconfig.view.DevSimpleConfigActivity;
import com.cofe.solution.ui.device.preview.view.DevMonitorActivity;
import com.cofe.solution.ui.device.push.view.DevPushActivity;
import com.cofe.solution.ui.device.record.view.DevRecordActivity;
import com.cofe.solution.ui.widget.DividerItemDecoration;
import io.reactivex.annotations.Nullable;

import static com.google.gson.internal.$Gson$Types.arrayOf;
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
    int onUppdateDevStateCount  = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_list);
        initView();
        initData();

    }

    private void initView() {
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
        if (!DevDataCenter.getInstance().isLoginByAccount()) {
           // titleBar.setRightTitleText(getString(R.string.clear_dev_list));
            if(DevDataCenter.getInstance().getAccountUserName()!=null) {
                if(DevDataCenter.getInstance().getAccessToken()==null) {
                    AccountManager.getInstance().xmLogin(DevDataCenter.getInstance().getAccountUserName(), DevDataCenter.getInstance().getAccountPassword(), 1,
                            new BaseAccountManager.OnAccountManagerListener() {
                                @Override
                                public void onSuccess(int msgId) {
                                    Log.d("Access toekn" ," > "  +DevDataCenter.getInstance().getAccessToken());

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
        }

        listView = findViewById(R.id.listViewDevice);
        noDeviceContLl  = findViewById(R.id.no_device_cont_ll);
        textTxtv =  findViewById(R.id.text_txtv);
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
                Intent i =  new Intent(DevListActivity.this, AddNewDeviceActivity.class);
                startActivity(i);
            }
        });
        refreshTitle();

        ImageButton menuIcon = findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v); // Show menu popup
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
                if(XMAccountManager.getInstance().getUserName()==null) {
                    SharedPreference cookies =  new SharedPreference(getContext());
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

        SharedPreference cookies =  new SharedPreference(getContext());
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
                        Intent i =  new Intent(DevListActivity.this, AddNewDeviceActivity.class);
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

    private void checkExternalStoragePermission(){

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

    private void showPermissionExplanationPopup(String permissionName,String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("permissionName Permission Required")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Accept", (dialog, which) -> {
                    if(permissionName.equals("Camera")) {
                        // Request camera permission
                        // Request camera permission
                        ActivityCompat.requestPermissions(DevListActivity.this,
                                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQEST_CODE);
                    } else if(permissionName.equals("WRITE EXTERNAL STORAGE")) {
                        // Request camera permission
                        // Request camera permission
                        ActivityCompat.requestPermissions(DevListActivity.this,
                                new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO}, WRITE_EXTERNAL_STORAGE);
                        requestPermissions(new String[]{
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VIDEO
                        }, WRITE_EXTERNAL_STORAGE);

                    }

                    else {
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
                startService(new Intent(this, DevPushService.class));

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
        builder.setTitle(permisonName+ " Permission Required")
                .setMessage(permisonName +" permission is permanently denied. Please enable it in the app settings.")
                .setCancelable(false)
                .setPositiveButton("Open Settings", (dialog, which) -> openAppSettings())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    private void openCamera() {
        Toast.makeText(this, "Camera is now accessible.", Toast.LENGTH_SHORT).show();
        Intent j =  new Intent(DevListActivity.this, DevSnConnectActivity.class);
        startActivity(j);
    }


    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    private void initData() {
        showWaitDialog();
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
        if (adapter != null) {
            if(presenter!=null) {
                Bundle bundle = new Bundle();
                bundle.putString( "called", "onRestart");
                bundle.putBoolean("userLogged", DevDataCenter.getInstance().isLoginByAccount());
                bundle.putString("username", (DevDataCenter.getInstance().getAccountUserName()!=null)?DevDataCenter.getInstance().getAccountUserName():"blank");
                bundle.putString("token", (DevDataCenter.getInstance().getAccessToken()!=null)?DevDataCenter.getInstance().getAccessToken():"blank");
                bundle.putInt("device", presenter.getDevList().size());
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                if(DevDataCenter.getInstance().getAccountUserName()!=null) {
                    if(DevDataCenter.getInstance().getAccessToken()==null) {
                        AccountManager.getInstance().xmLogin(DevDataCenter.getInstance().getAccountUserName(), DevDataCenter.getInstance().getAccountPassword(), 1,
                                new BaseAccountManager.OnAccountManagerListener() {
                                    @Override
                                    public void onSuccess(int msgId) {
                                        Log.d("Access toekn" ," > "  +DevDataCenter.getInstance().getAccessToken());
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
        slRefresh.setRefreshing(false);

        Bundle bundle = new Bundle();
        bundle.putString( "called", "onUpdateDevListView");
        bundle.putBoolean("userLogged", DevDataCenter.getInstance().isLoginByAccount());
        bundle.putString("username", (DevDataCenter.getInstance().getAccountUserName()!=null)?DevDataCenter.getInstance().getAccountUserName():"blank");
        bundle.putString("token", (DevDataCenter.getInstance().getAccessToken()!=null)?DevDataCenter.getInstance().getAccessToken():"blank");
        bundle.putInt("device", (presenter!=null)?presenter.getDevList().size():0);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        Log.d("user Data", " username > " + DevDataCenter.getInstance().getAccountUserName());
        Log.d("user Data", " password > " + DevDataCenter.getInstance().getAccountPassword());
        Log.d("user Data", "Access Token > " + DevDataCenter.getInstance().getAccessToken());


        if(onUppdateDevStateCount<1) {
            onUppdateDevStateCount  = onUppdateDevStateCount+1;
            if (!DevDataCenter.getInstance().isLoginByAccount()) {
                // titleBar.setRightTitleText(getString(R.string.clear_dev_list));
                if (DevDataCenter.getInstance().getAccountUserName() != null) {
                    if (DevDataCenter.getInstance().getAccessToken() == null) {
                        AccountManager.getInstance().xmLogin(DevDataCenter.getInstance().getAccountUserName(), DevDataCenter.getInstance().getAccountPassword(), 1,
                                new BaseAccountManager.OnAccountManagerListener() {
                                    @Override
                                    public void onSuccess(int msgId) {
                                        Log.d("Access toekn", " > " + DevDataCenter.getInstance().getAccessToken());
                                        if(!initDataCalled) {
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
                        if(!initDataCalled) {
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
        hideWaitDialog();

        Bundle bundle = new Bundle();
        bundle.putString( "called", "onUpdateDevStateResult");
        bundle.putBoolean("userLogged", DevDataCenter.getInstance().isLoginByAccount());
        bundle.putString("username", (DevDataCenter.getInstance().getAccountUserName()!=null)?DevDataCenter.getInstance().getAccountUserName():"blank");
        bundle.putString("token", (DevDataCenter.getInstance().getAccessToken()!=null)?DevDataCenter.getInstance().getAccessToken():"blank");
        bundle.putInt("device", (presenter!=null)?presenter.getDevList().size():0);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        slRefresh.setRefreshing(false);
        if (isSuccess) {
            adapter.setData((ArrayList<HashMap<String, Object>>) presenter.getDevList());
        }
        if(presenter.getDevList()!=null) {
            if(presenter.getDevList().size()==0) {
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
                                showWaitDialog();
                                presenter.getChannelList();
                            }
                        }, false);
            } else if (resultId == EFUN_ERROR.EE_DVR_LOGIN_USER_NOEXIST) {
                XMDevInfo devInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
                XMPromptDlg.onShowPasswordErrorDialog(this, devInfo.getSdbDevInfo(),
                        0, getString(R.string.input_username_password), INPUT_TYPE_DEV_USER_PWD, true, new PwdErrorManager.OnRepeatSendMsgListener() {
                            @Override
                            public void onSendMsg(int msgId) {
                                showWaitDialog();
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

            showWaitDialog(getString(R.string.get_channel_info));
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
    }

    @Override
    public boolean onLongItemClick(final int position, XMDevInfo xmDevInfo) {
        XMPromptDlg.onShow(this, getString(R.string.is_sure_delete_dev), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                presenter.deleteDev(position);
            }
        }, null);
        return false;
    }

    /**
     * 跳转到报警消息
     * jump to alarm message
     *
     * @param position
     */
    @Override
    public void onTurnToAlarmMsg(int position) {// This is push messaging
        String devId = presenter.getDevId(position);
        presenter.setDevId(devId);
        turnToActivity(DevAlarmMsgActivity.class);
    }

    /**
     * 跳转到云服务
     * jump to alarm message
     *
     * @param position
     */
    @Override
    public void onTurnToCloudService(int position) {
        String devId = presenter.getDevId(position);
        presenter.setDevId(devId);
        turnToActivity(CloudStateActivity.class);
    }

    @Override
    public void openSettingActivity(int position, XMDevInfo xmDevInfo) {
        String devId = presenter.getDevId(position);
        presenter.setDevId(devId);

        Gson gson = new Gson();
        String personJson = gson.toJson(xmDevInfo);

        // Pass the JSON string to another activity via Intent
        Intent intent = new Intent(this, DeviceSetting.class);
        intent.putExtra("dev", personJson);
        startActivity(intent);
        //turnToActivity(DeviceSetting.class);
    }

    /**
     * 跳转到推送设置
     * Jump to push Settings
     *
     * @param position
     */
    @Override
    public void onTurnToPushSet(int position) {
        String devId = presenter.getDevId(position);
        presenter.setDevId(devId);
        turnToActivity(DevPushActivity.class);
    }

    /**
     * 修改设备名
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onModifyDevName(int position, XMDevInfo xmDevInfo) {
        XMPromptDlg.onShowEditDialog(this, getString(R.string.modify_dev_name), xmDevInfo.getDevName(), new EditDialog.OnEditContentListener() {
            @Override
            public void onResult(String devName) {
                presenter.modifyDevNameFromServer(position, devName);
            }
        });
    }

    /**
     * 分享管理
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onShareDevManage(int position, XMDevInfo xmDevInfo) {
        presenter.setDevId(xmDevInfo.getDevId());
        Gson gson = new Gson();
        String personJson = gson.toJson(xmDevInfo);

        // Pass the JSON string to another activity via Intent
        //Intent intent = new Intent(this, ShareFirstScren.class);
        Intent intent = new Intent(this, ShareFirstScren.class);
        intent.putExtra("dev", personJson);
        startActivity(intent);

        //turnToActivity(DevShareManageActivity.class);
    }

    /**
     * 跳转到设备本地用户名和密码页面
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onTurnToEditLocalDevUserPwd(int position, XMDevInfo xmDevInfo) {
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
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String devUserName = etDevUser.getText().toString().trim();
                String devPwd = etDevPwd.getText().toString().trim();
                presenter.editLocalDevUserPwd(
                        position,
                        xmDevInfo.getDevId(),
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
    }

    /**
     * 唤醒设备并开锁
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onWakeUpDev(int position, XMDevInfo xmDevInfo) {
        presenter.wakeUpDev(position, xmDevInfo.getDevId());
    }

    /**
     * 跳转到SD卡录像回放页面
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onTurnToSdPlayback(int position, XMDevInfo xmDevInfo) {
        presenter.setDevId(xmDevInfo.getDevId());
        turnToActivity(DevRecordActivity.class);
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
        presenter.setDevId(xmDevInfo.getDevId());
        turnToActivity(InterDevLinkageActivity.class, "data", bundle);
    }

    /**
     * 跳转到设备能力集页面
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onTurnToDevAbility(int position, XMDevInfo xmDevInfo) {
        presenter.setDevId(xmDevInfo.getDevId());
        turnToActivity(XMDevAbilityActivity.class);
    }

    /**
     * 从服务器获取设备Token
     *
     * @param position
     * @param xmDevInfo
     */
    @Override
    public void onToGetDevTokenFromServer(int position, XMDevInfo xmDevInfo) {
        presenter.getDevTokenFromServer(xmDevInfo.getDevId());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.release();
        }

        presenter.clear();
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
                showPermissionExplanationPopup("Push Notification ","This app needs your permission to send push notifications. Please grant the permission to stay updated.");
            }
        }
    }

    private void enablePushNotifications() {
        Toast.makeText(this, "Push notifications enabled.", Toast.LENGTH_SHORT).show();
        // Add your logic for handling push notifications here (e.g., subscribing to topics)
    }

    @Override
    protected void onResume(){

        if (!DevDataCenter.getInstance().isLoginByAccount()) {
            // titleBar.setRightTitleText(getString(R.string.clear_dev_list));

            if (DevDataCenter.getInstance().getAccountUserName() != null) {
                if (DevDataCenter.getInstance().getAccessToken() == null) {
                    AccountManager.getInstance().xmLogin(DevDataCenter.getInstance().getAccountUserName(), DevDataCenter.getInstance().getAccountPassword(), 1,
                            new BaseAccountManager.OnAccountManagerListener() {
                                @Override
                                public void onSuccess(int msgId) {
                                    Log.d("Access toekn", " > " + DevDataCenter.getInstance().getAccessToken());
                                    initData();

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
}
