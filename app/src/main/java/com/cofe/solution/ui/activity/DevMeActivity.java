package com.cofe.solution.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cofe.solution.R;
import com.cofe.solution.ui.device.add.list.view.DevListActivity;
import com.cofe.solution.ui.device.picture.view.DevPictureActivity;

public class DevMeActivity extends AppCompatActivity {

    ImageView ivProfileImage;
    ImageView ivBasicSettings;

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


    }
}