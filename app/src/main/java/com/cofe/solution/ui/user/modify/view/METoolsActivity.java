package com.cofe.solution.ui.user.modify.view;


import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cofe.solution.R;

public class METoolsActivity extends AppCompatActivity {

    TextView tvTitleHeader;
    ImageView back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_metools);
        initUis();
    }

    private void initUis()
    {
        tvTitleHeader=findViewById(R.id.toolbar_title);
        back_button=findViewById(R.id.back_button);
        tvTitleHeader.setText("Tools");

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}