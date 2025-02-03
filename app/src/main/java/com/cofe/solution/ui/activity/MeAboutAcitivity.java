package com.cofe.solution.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cofe.solution.R;

public class MeAboutAcitivity extends AppCompatActivity {

    TextView tvTitleHeader;
    ImageView back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_me_about_acitivity);

        initUis();

    }

    private void initUis()
    {
        tvTitleHeader=findViewById(R.id.tvTitleHeader);
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