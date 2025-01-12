package com.cofe.solution.ui.device.config;

import static com.blankj.utilcode.util.ScreenUtils.getScreenWidth;

import android.app.Activity;
import android.app.Dialog;
import android.app.TaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.basic.G;
import com.cofe.solution.ui.activity.DeviceConfigActivity;
import com.cofe.solution.ui.activity.DeviceConfigPresenter;
import com.cofe.solution.ui.device.alarm.view.DevAlarmMsgActivity;
import com.cofe.solution.ui.device.config.about.presenter.DevAboutPresenter;
import com.cofe.solution.ui.device.config.about.view.DevAboutActivity;
import com.cofe.solution.ui.device.config.advance.view.DevAdvanceActivity;
import com.cofe.solution.ui.device.config.alarmconfig.view.DevAlarmSetActivity;
import com.cofe.solution.ui.device.preview.view.DevMonitorActivity;
import com.cofe.solution.ui.device.push.view.DevPushActivity;
import com.google.gson.Gson;
import com.lib.MsgContent;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.manager.account.BaseAccountManager;
import com.manager.account.XMAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.dialog.EditDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import com.cofe.solution.R;
import com.cofe.solution.ui.device.add.AddNewDeviceActivity;
import com.cofe.solution.ui.device.add.list.listener.DevListConnectContract;
import com.cofe.solution.ui.device.add.wifi.CameraConfigInstruction;
import com.cofe.solution.ui.device.add.wifi.WifiPowerOnCamer;
import com.cofe.solution.ui.device.aov.view.AovSettingActivity;

public class DeviceSetting extends BaseConfigActivity<DevAboutPresenter>  implements  DevListConnectContract.IDevListConnectView {
    XMDevInfo xmDevInfo;
    Context context;
    DevListConnectContract.IDevListConnectView presenter ;
    TextView dTxtv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);
        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.device_setting));


        String personJson = getIntent().getStringExtra("dev");

        // Convert the JSON string back to a Person object
        if (personJson != null) {
            Gson gson = new Gson();
            xmDevInfo = gson.fromJson(personJson, XMDevInfo.class);
        }

        context =  DeviceSetting.this;
        presenter =  DeviceSetting.this;

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set OnClickListeners for each item
        findViewById(R.id.device_name_item).setOnClickListener(v -> openDeviceNameSettings(v, false));
        findViewById(R.id.password_management_item).setOnClickListener(v -> openPasswordManagementSettings(v));
        findViewById(R.id.language_item).setOnClickListener(v -> openLanguageSettings());
        findViewById(R.id.battery_management_item).setOnClickListener(v -> openBatteryManagementSettings(v));
        findViewById(R.id.working_mode_item).setOnClickListener(v -> openWorkingModeSettings());
        findViewById(R.id.smart_alarm_item).setOnClickListener(v -> openSmartAlarmSettings());
        findViewById(R.id.cloud_storage_item).setOnClickListener(v -> openCloudStorageSettings());
        findViewById(R.id.add_to_desktop_item).setOnClickListener(v -> openAddToDesktopSettings());
        findViewById(R.id.about_device_item).setOnClickListener(v -> openAboutDeviceSettings());
        findViewById(R.id.push_device_item).setOnClickListener(v -> openPushNotificatioNSetting());
        findViewById(R.id.advanced_device_item).setOnClickListener(v -> openAdvanceSetting());
        findViewById(R.id.date_device_item).setOnClickListener(v -> syncDateTimeDevice());
        dTxtv = findViewById(R.id.dname_txtv);
        try {
            dTxtv.setText(xmDevInfo.getDevName());
        } catch ( Exception e ) {
            e.printStackTrace();
            Toast.makeText(getContext(),"device id not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setDimBackground(Activity activity, float dimAmount) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.alpha = dimAmount; // Set dim level (0.0f to 1.0f)
        window.setAttributes(layoutParams);
    }

    private void openDeviceNameSettings(View anchorView, Boolean isPsasword) {
        XMPromptDlg.onShowEditDialog(this, "Change Device Name",xmDevInfo.getDevName(), new EditDialog.OnEditContentListener() {
            @Override
            public void onResult(String devName) {
                XMAccountManager.getInstance().modifyDevName(xmDevInfo.getDevId(), devName, new BaseAccountManager.OnAccountManagerListener(){

                    @Override
                    public void onSuccess(int msgId) {
                        dTxtv.setText(devName);
                        presenter.onModifyDevNameFromServerResult(true);
                        Toast.makeText(DeviceSetting.this, "Device name changed successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int msgId, int errorId) {
                        presenter.onModifyDevNameFromServerResult(false);
                        Toast.makeText(DeviceSetting.this, "Failed to changed device name", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFunSDKResult(Message msg, MsgContent ex) {

                    }
                });

            }
        });

    }



    private void openPasswordManagementSettings(View anchorView) {

        // Inflate the custom popup layout
        View popupView = LayoutInflater.from(anchorView.getContext())
                .inflate(R.layout.edit_device_name, null);

        // Create the PopupWindow
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                (int) (anchorView.getContext().getResources().getDisplayMetrics().widthPixels * 0.8), // 80% of screen width
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Set focusable so the popup will close when touched outside
        popupWindow.setFocusable(true);

        // Dim the background
        setDimBackground((Activity) anchorView.getContext(), 0.5f);

        // Add a dismiss listener to restore the background
        popupWindow.setOnDismissListener(() -> setDimBackground((Activity) anchorView.getContext(), 1f));

        // Show the popup at the center of the screen
        View rootView = ((Activity) anchorView.getContext()).getWindow().getDecorView().getRootView();
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
        TextView label = popupView.findViewById(R.id.popup_label);

        label.setText(anchorView.getContext().getString(R.string.enter_password));
        EditText editText = popupView.findViewById(R.id.popup_edittext);
        EditText oldPwdTextView = popupView.findViewById(R.id.popup_oldPwdText);
        Button button = popupView.findViewById(R.id.popup_button);

        // Handle button click
        button.setOnClickListener(v -> {
            String userInput = editText.getText().toString().trim();
            String oldPwdText = oldPwdTextView.getText().toString().trim();
                //if (!oldPwdText.isEmpty() ) {
                    if (!userInput.isEmpty()) {

                        if (xmDevInfo != null) {
                            SDBDeviceInfo deviceInfo = xmDevInfo.getSdbDevInfo();
                            if (deviceInfo != null) {
                                String loginName = G.ToString(deviceInfo.st_4_loginName);
                                if (TextUtils.isEmpty(loginName)) {
                                    loginName = "admin";
                                }
                                DeviceManager.getInstance().modifyDevPwd(xmDevInfo.getDevId(), loginName, oldPwdText, userInput, new DeviceManager.OnDevManagerListener() {
                                    @Override
                                    public void onSuccess(String s, int i, Object o) {
                                        popupWindow.dismiss();
                                        Toast.makeText(anchorView.getContext(), "Password changes successfully", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailed(String s, int i, String s1, int errorId) {
                                        popupWindow.dismiss();
                                        Toast.makeText(anchorView.getContext(), "Failed to change the password", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    } else {
                        Toast.makeText(anchorView.getContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                    }
                /*} else {
                    Toast.makeText(anchorView.getContext(), "Please enter old password", Toast.LENGTH_SHORT).show();
                }*/


        });
    }

    private void openLanguageSettings() {
        //Toast.makeText(this, "Language Settings clicked", Toast.LENGTH_SHORT).show();
        // Add navigation logic
        Intent intent = new Intent();
        intent.setClass(DeviceSetting.this, DeviceConfigActivity.class);
        intent.putExtra("devId", xmDevInfo.getDevId());
        startActivity(intent);
    }

    private void openBatteryManagementSettings(View v) {
        Intent i= new Intent(this, AovSettingActivity.class);
        i.putExtra("devId",xmDevInfo.getDevId());
        startActivity(i);
    }

    private void openWorkingModeSettings() {
        //Toast.makeText(this, "Working Mode Settings clicked", Toast.LENGTH_SHORT).show();

        Intent i= new Intent(this, AovSettingActivity.class);
        i.putExtra("devId",xmDevInfo.getDevId());
        i.putExtra("working",xmDevInfo.getDevId());
        startActivity(i);

    }

    private void openSmartAlarmSettings() {
        //Toast.makeText(this, "Smart Alarm clicked", Toast.LENGTH_SHORT).show();
        // Add navigation logic
        Intent intent = new Intent(this, DevAlarmSetActivity.class);
        intent.putExtra("devId", xmDevInfo.getDevId());
        startActivity(intent);

    }

    private void openCloudStorageSettings() {
        //Toast.makeText(this, "Cloud Storage clicked", Toast.LENGTH_SHORT).show();
        // Add navigation logic

    }

    private void openAddToDesktopSettings() {
        //Toast.makeText(this, "Add to Desktop clicked", Toast.LENGTH_SHORT).show();
        // Add navigation logic
    }

    private void openAboutDeviceSettings() {

        Intent intent = new Intent(this, DevAboutActivity.class);
        intent.putExtra("firmwareType", "Mcu");
        intent.putExtra("devId", xmDevInfo.getDevId());
        startActivity(intent);
    }

    private void openPushNotificatioNSetting() {
        Intent intent = new Intent(DeviceSetting.this, DevPushActivity.class);
        intent.putExtra("devId", xmDevInfo.getDevId());
        startActivity(intent);
    }
     void openAdvanceSetting() {
        Intent intent = new Intent(DeviceSetting.this, DevAdvanceActivity.class);
        intent.putExtra("devId", xmDevInfo.getDevId());
        startActivity(intent);
    }

    @Override
    public void onUpdateDevListView() {

    }

    @Override
    public void onUpdateDevStateResult(boolean isSuccess) {

    }

    @Override
    public void onModifyDevNameFromServerResult(boolean isSuccess) {
        //hideWaitDialog();
        if (isSuccess) {
            //Toast.makeText(context, getString(R.string.TR_Modify_Dev_Name_S), Toast.LENGTH_LONG);

        } else {
           // Toast.makeText(context, getString(R.string.TR_Modify_Dev_Name_F), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onDeleteDevResult(boolean isSuccess) {

    }

    @Override
    public void onAcceptDevResult(boolean isSuccess) {

    }

    @Override
    public void onGetChannelListResult(boolean isSuccess, int resultId) {

    }
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    public void syncDateTimeDevice() {
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
        Button btnNo = dialog.findViewById(R.id.btn_no);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showWaitDialog();
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(calendar.getTime());
                getManager().syncDevTime(xmDevInfo.getDevId(), time, new DeviceManager.OnDevManagerListener() {
                    @Override
                    public void onSuccess(String devId, int operationType, Object result) {
                        hideWaitDialog();
                        showToast(getString(R.string.dev_time_sync_success), Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                        hideWaitDialog();
                    }
                });

            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform your action for No button
                dialog.dismiss(); // Close the dialog
            }
        });

        // Show the dialog
        dialog.show();

    }

    @Override
    public Context getContext() {
        return null;
    }
}