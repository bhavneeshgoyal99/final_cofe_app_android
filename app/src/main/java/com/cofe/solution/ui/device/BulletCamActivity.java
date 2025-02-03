package com.cofe.solution.ui.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cofe.solution.R;

public class BulletCamActivity extends AppCompatActivity {
    TextView tvTitleHeader;
    ImageView back_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bullet_cam);

        initUis();
    }

    private void initUis()
    {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        tvTitleHeader=findViewById(R.id.tvTitleHeader);
        back_button=findViewById(R.id.back_button);


        tvTitleHeader.setText(title);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });







    }
}