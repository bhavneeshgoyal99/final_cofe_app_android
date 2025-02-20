package com.cofe.solution.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.R;

import org.w3c.dom.Text;

public class NetworkSettings extends AppCompatActivity {

    TextView tvTitleHeader;
    ImageView back_button;

    RecyclerView rvWifiList;
    TextView tvSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_network_settings);
        initUis();
    }

    private void initUis()
    {
        tvTitleHeader=findViewById(R.id.toolbar_title);
        back_button=findViewById(R.id.back_button);
        rvWifiList=findViewById(R.id.rvWifiList);
        tvSave=findViewById(R.id.tvSave);
        tvSave.setVisibility(View.VISIBLE);

        tvTitleHeader.setText(getString(R.string.network_settings));

        rvWifiList.setLayoutManager(new LinearLayoutManager(this));
        rvWifiList.setAdapter(new WifiListAdapter(this));

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }
}