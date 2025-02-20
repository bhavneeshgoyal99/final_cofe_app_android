package com.cofe.solution.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.cofe.solution.R;
import com.cofe.solution.ui.adapter.FromSharedDeviceAdapter;
import com.cofe.solution.ui.adapter.MuSharedDeviceAdapter;

public class PTZSettingsActivity extends AppCompatActivity {

    TextView tvTitleHeader;
    ImageView back_button;
    RelativeLayout rlPtzSpeed;
    LinearLayout llPtzOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ptzsettings);

        initUis();
    }

    private void initUis()
    {
        tvTitleHeader=findViewById(R.id.toolbar_title);
        back_button=findViewById(R.id.back_button);
        rlPtzSpeed=findViewById(R.id.rlPtzSpeed);
        llPtzOptions=findViewById(R.id.llPtzOptions);


        tvTitleHeader.setText(getString(R.string.ptz_settings));

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rlPtzSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llPtzOptions.getVisibility() == View.VISIBLE)
                {
                    llPtzOptions.setVisibility(View.GONE);
                }
                else{
                    llPtzOptions.setVisibility(View.VISIBLE);
                }
            }
        });




    }
}