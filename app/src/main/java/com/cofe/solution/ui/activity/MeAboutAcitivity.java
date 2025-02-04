package com.cofe.solution.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cofe.solution.R;
import com.cofe.solution.app.SDKDemoApplication;

public class MeAboutAcitivity extends AppCompatActivity {

    TextView tvTitleHeader;
    ImageView back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_me_about_acitivity);
        TextView imagtPathTextv = findViewById(R.id.path_path_txttv);
        TextView videotPathTextv = findViewById(R.id.video_path_txttv);
        imagtPathTextv.setText(SDKDemoApplication.PATH_PHOTO);
        videotPathTextv.setText(SDKDemoApplication.PATH_PHOTO);
        initUis();

    }

    private void initUis() {
        tvTitleHeader=findViewById(R.id.toolbar_title);
        back_button=findViewById(R.id.back_button);

        tvTitleHeader.setText("About");
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}