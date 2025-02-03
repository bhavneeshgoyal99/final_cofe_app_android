package com.cofe.solution.ui.device.add.share.view;

import static com.lib.EFUN_ATTR.LOGIN_USER_ID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.lib.FunSDK;
import com.lib.sdk.bean.share.SearchUserInfoBean;
import com.manager.db.XMDevInfo;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.device.add.share.listener.DevShareConnectContract;
import com.cofe.solution.ui.device.add.share.presenter.DevShareConnectPresenter;
import io.reactivex.annotations.Nullable;

/**
 * Shared device interface, display related list menu
 */
public class ShareDevToOtherAccountActivity extends DemoBaseActivity<DevShareConnectPresenter> implements DevShareConnectContract.IDevShareConnectView {
    private EditText etShareAccount;
    private ListSelectItem lsiShareAccountInfo;
    private ImageView ivQrCode;//分享的二维码布局

    private TextView deviceName, sharedFrom, validTime;
    private ImageView deviceIcon, qrCode;
    private Button shareButton, saveToPhoneButton;
    XMDevInfo xmDevInfo;
    ArrayList<String> permissionList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dev_share_connect);

        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.device_share));
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Convert the JSON string back to a Person object
        String personJson = getIntent().getStringExtra("dev");
        if (personJson != null) {
            Gson gson = new Gson();
            xmDevInfo = gson.fromJson(personJson, XMDevInfo.class);
        }
        permissionList = getIntent().getStringArrayListExtra("permission");
        Log.d( getClass().getName(), "permissionEnabled > "  + permissionList);

        etShareAccount = findViewById(R.id.et_search_bar_input);
        lsiShareAccountInfo = findViewById(R.id.lsi_share_account);

        lsiShareAccountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.shareDevToOther(lsiShareAccountInfo.getTip());
            }
        });

        ivQrCode = findViewById(R.id.iv_qr_code);

        // Initialize UI components
        deviceName = findViewById(R.id.device_name);
        sharedFrom = findViewById(R.id.device_shared_from);
        validTime = findViewById(R.id.valid_time);
        deviceIcon = findViewById(R.id.device_icon);
        qrCode = findViewById(R.id.qr_code);
        shareButton = findViewById(R.id.btn_share);
        saveToPhoneButton = findViewById(R.id.btn_save_to_phone);


        String deviceNameText = "Device Name: "  ;
        String sharedFromText = "The device shared from : " ;

        // Set data to views
        deviceName.setText(deviceNameText);
        sharedFrom.setText(sharedFromText);


        // Set up button click listeners
        shareButton.setOnClickListener(view -> shareDeviceDetails());
        saveToPhoneButton.setOnClickListener(view -> saveQRCodeToPhone());

        initData();
    }

    private void shareDeviceDetails() {
        try {
            File cachePath1 = new File(getCacheDir(), "images");
            if (!cachePath1.exists()) {
                cachePath1.mkdirs(); // Create the directory if it does not exist
            }

            // Take a screenshot of the current view
            View rootView = getWindow().getDecorView().getRootView();
            rootView.setDrawingCacheEnabled(true);
            Bitmap screenshot = Bitmap.createBitmap(rootView.getDrawingCache());
            rootView.setDrawingCacheEnabled(false);

            // Save the screenshot temporarily to the cache directory
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs(); // Ensure the directory exists
            File screenshotFile = new File(cachePath, "screenshot.png");
            FileOutputStream outputStream = new FileOutputStream(screenshotFile);
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();

            // Get the URI of the screenshot file
            Uri screenshotUri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    screenshotFile
            );

            // Create a share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Show the share dialog
            startActivity(Intent.createChooser(shareIntent, "Share Screenshot"));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to share the screenshot", Toast.LENGTH_SHORT).show();
        }
    }



    private void saveQRCodeToPhone() {
        try {
            // Extract bitmap from the ImageView
            qrCode.setDrawingCacheEnabled(true);
            Bitmap qrBitmap = Bitmap.createBitmap(qrCode.getDrawingCache());
            qrCode.setDrawingCacheEnabled(false);

            // Save the bitmap to MediaStore
            String savedImageURL = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    qrBitmap,
                    "QRCode_" + System.currentTimeMillis(),
                    "QR code image saved by the app"
            );

            if (savedImageURL != null) {
                // Notify the user that the QR code has been saved
                Toast.makeText(this, "QR code saved to Gallery", Toast.LENGTH_SHORT).show();

                // Optionally, you can log or use the saved image URL
                System.out.println("Saved Image URL: " + savedImageURL);
            } else {
                // Notify the user if saving failed
                Toast.makeText(this, "Failed to save QR code", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "An error occurred while saving the QR code", Toast.LENGTH_SHORT).show();
        }
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    void getNext24Hr(){
        LocalDateTime currentTime = LocalDateTime.now();

        // Add 24 hours to the current time
        LocalDateTime next24Hours = currentTime.plusHours(24);

        // Format the output
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTimeStr = currentTime.format(formatter);
        String next24HoursStr = next24Hours.format(formatter);

        // Print the results
        System.out.println("Current Time: " + currentTimeStr);
        System.out.println("Next 24 Hours: " + next24HoursStr);
        String validTimeText = "Valid time: " +next24HoursStr;
        validTime.setText(validTimeText);
    }

    /**
     * 初始化数据
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initData() {
        presenter.setPermissionList(permissionList);
        presenter.setDevId(xmDevInfo.getDevId());
        Bitmap bitmap = presenter.getShareDevQrCode(this);
        //ivQrCode.setImageBitmap(bitmap);
        qrCode.setImageBitmap(bitmap);
        getNext24Hr();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onSearchShareAccountResult(boolean isSuccess, SearchUserInfoBean searchUserInfoBean) {
        if (isSuccess) {
            if (searchUserInfoBean != null) {
                lsiShareAccountInfo.setTitle(searchUserInfoBean.getAccount());
                lsiShareAccountInfo.setTip(searchUserInfoBean.getId());
            }
        } else {
            ToastUtils.showLong(R.string.not_found);
        }
    }

    @Override
    public void onShareDevResult(boolean isSuccess) {
        if (isSuccess) {
            turnToActivity(DevShareAccountListActivity.class);
        }

        ToastUtils.showLong(isSuccess ? getString(R.string.share_s) : getString(R.string.share_f));
    }

    @Override
    public void onUpdateView() {

    }

    @Override
    public DevShareConnectPresenter getPresenter() {
        return new DevShareConnectPresenter(this);
    }

    /**
     * 分享的账号查询
     * Shared account query
     *
     * @param view
     */
    public void onSearchAccount(View view) {
        presenter.searchShareAccount(etShareAccount.getText().toString().trim());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.release();
        }
    }
}
