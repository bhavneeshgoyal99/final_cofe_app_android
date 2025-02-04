package com.cofe.solution.ui.activity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.R;
import com.cofe.solution.ui.adapter.FromSharedDeviceAdapter;
import com.cofe.solution.ui.adapter.MuSharedDeviceAdapter;
import com.cofe.solution.ui.adapter.SharedUserAdapter;

public class MySharedUserActivity extends AppCompatActivity {
    TextView tvTitleHeader;
    ImageView back_button;
    RecyclerView rvSharedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_shared_user);

        initUis();
    }

    private void initUis()
    {
        tvTitleHeader=findViewById(R.id.toolbar_title);
        back_button=findViewById(R.id.back_button);
        rvSharedUser=findViewById(R.id.rvSharedUser);

        tvTitleHeader.setText("Shared Users");


        rvSharedUser.setLayoutManager(new LinearLayoutManager(this));
        rvSharedUser.setAdapter(new SharedUserAdapter(this, new SharedUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick() {
                showActionDialog();
            }
        }));

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void showActionDialog() {
        // Create and configure the dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_action);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // Find views in the custom layout

        ImageView ivCancel = dialog.findViewById(R.id.ivCancel);

        // Set up click listeners
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        // Adjust the dialog width and apply margins
        if (dialog.getWindow() != null) {
            int margin = (int) (20 * getResources().getDisplayMetrics().density); // Convert 20dp to pixels
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = getResources().getDisplayMetrics().widthPixels - (2 * margin);
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        // Show the dialog
        dialog.show();
    }
}