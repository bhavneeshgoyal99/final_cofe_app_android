package com.cofe.solution.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import androidx.appcompat.app.AppCompatActivity;

import com.cofe.solution.R;
import com.cofe.solution.ui.user.modify.view.METoolsActivity;
import com.cofe.solution.ui.user.modify.view.AppAboutAcitivity;
import com.cofe.solution.ui.user.modify.view.MePermissionSettingsAcitivity;

public class DevMeActivity extends AppCompatActivity {

    ImageView ivProfileImage;
    ImageView ivBasicSettings;
    RelativeLayout rlAbout;
    RelativeLayout rlPermissionSettings;
    RelativeLayout rlTools;
    RelativeLayout rlSharingManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dev_me2);

        initUis();
    }

    private void initUis() {
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivBasicSettings = findViewById(R.id.ivBasicSettings);
        rlPermissionSettings = findViewById(R.id.rlPermissionSettings);
        rlSharingManagement = findViewById(R.id.rlSharingManagement);
        rlTools = findViewById(R.id.rlTools);
        rlAbout = findViewById(R.id.rlAbout);

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DevMeActivity.this, AccountInformationActivity.class);
                startActivity(intent);
            }
        });
        ivBasicSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(DevMeActivity.this, BasicSettingsActivity.class);
                startActivity(intent);*/
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


    }
}