package com.cofe.solution.ui.user.modify.view;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.activity.AccountInformationActivity;
import com.cofe.solution.ui.activity.MeSharingManagement;
import com.cofe.solution.ui.device.add.list.view.DevListActivity;
import com.cofe.solution.ui.device.picture.view.DevPictureActivity;
import com.cofe.solution.ui.user.info.listener.UserInfoContract;
import com.cofe.solution.ui.user.info.presenter.UserInfoPresenter;
import com.cofe.solution.ui.user.info.view.BasicSettingsActivity;
import com.cofe.solution.ui.user.info.view.UserInfoActivity;
import com.cofe.solution.ui.user.login.view.UserLoginActivity;
import com.manager.db.DevDataCenter;

public class DevMeActivity extends DemoBaseActivity<UserInfoPresenter> implements  UserInfoContract.IUserInfoView {
    ImageView ivProfileImage;
    RelativeLayout ivProfileImageRl;
    ImageView ivBasicSettings;
    RelativeLayout rlAbout;
    RelativeLayout rlPermissionSettings;
    RelativeLayout rlTools;
    RelativeLayout rlSharingManagement;

    @Override
    public UserInfoPresenter getPresenter() {
        return new UserInfoPresenter(DevMeActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dev_me2);
        initUis();
    }

    private void initUis() {
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivProfileImageRl = findViewById(R.id.ivProfileImageRl);
        ivBasicSettings = findViewById(R.id.ivBasicSettings);
        rlPermissionSettings = findViewById(R.id.rlPermissionSettings);
        rlSharingManagement = findViewById(R.id.rlSharingManagement);
        rlTools = findViewById(R.id.rlTools);
        rlAbout = findViewById(R.id.rlAbout);

        ivProfileImageRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DevMeActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });

        ivBasicSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DevMeActivity.this, BasicSettingsActivity.class);
                startActivity(intent);
            }
        });
        rlAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DevMeActivity.this, AppAboutAcitivity.class);
                startActivity(intent);
            }
        });

        rlPermissionSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DevMeActivity.this, MePermissionSettingsAcitivity.class);
                startActivity(intent);
            }
        });
        rlTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DevMeActivity.this, METoolsActivity.class);
                startActivity(intent);
            }
        });
        rlSharingManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DevMeActivity.this, MeSharingManagement.class);
                startActivity(intent);
            }
        });

        if (DevDataCenter.getInstance().isLoginByAccount()) {
            loaderDialog.setMessage();
            //findViewById(R.id.layout_user_Info).setVisibility(View.VISIBLE);
            tryToGetUserInfo();
        }

        LinearLayout homeLL = findViewById(R.id.home_ll);

        homeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// Account logout
                Intent intent = new Intent(DevMeActivity.this, DevListActivity.class);
                startActivity(intent);
                finish();
            }
        });


        LinearLayout imageLl = findViewById(R.id.image_ll);
        imageLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// Account logout
                Intent intent = new Intent(DevMeActivity.this, DevPictureActivity.class);
                startActivity(intent);
                //finish();

            }
        });

        LinearLayout meLl = findViewById(R.id.me_ll);
        meLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // Account logout
                // turnToActivity(DevMeActivity.class);
                Intent intent = new Intent(DevMeActivity.this, DevMeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void tryToGetUserInfo() {
        loaderDialog.setMessage();
        if (!presenter.getInfo()) {
            showToast(getString(R.string.user_info_not_login), Toast.LENGTH_LONG);
            finish();
            Intent intent = new Intent();
            intent.setClass(this, UserLoginActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onUpdateView() {
        loaderDialog.dismiss();
        if (presenter != null) {
            TextView accountIdTxttv = findViewById(R.id.account_id_txtv);
            accountIdTxttv.setText(presenter.getUserName());
        }

    }
}