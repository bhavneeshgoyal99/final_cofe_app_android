package com.cofe.solution.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.R;
import com.cofe.solution.ui.adapter.ChangeSharepermissionAdapter;
import com.cofe.solution.ui.adapter.ShareDevicePermissionGridAdapter;
import com.cofe.solution.ui.adapter.SharedUserAdapter;
import com.cofe.solution.ui.dialog.LoaderDialog;
import com.lib.sdk.bean.share.Permission;
import com.manager.account.share.ShareManager;
import com.manager.db.DevDataCenter;
import com.manager.device.DeviceManager;

import java.util.ArrayList;
import java.util.List;

public class ShareUserChangePermissionActivity extends AppCompatActivity {

    TextView tvTitleHeader;
    ImageView back_button;
    TextView viewChangePermission;
    List<String> itemList;
    List<Permission> permissions = new ArrayList<>();

    String permission_name="";
    String share_id="";

    SwitchCompat toggle_checkboxes;

    ChangeSharepermissionAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_share_user_change_permission);

        initUis();
    }
    private void initUis()
    {


        Intent intent = getIntent();
        if (intent != null) {

            ArrayList<String> permissionStrings = intent.getStringArrayListExtra("permissions_list");
            share_id = intent.getStringExtra("share_id");

            if (permissionStrings != null) {
                for (String permissionString : permissionStrings) {
                    String[] parts = permissionString.split(":");
                    String name = parts[0];
                    boolean isGranted = Boolean.parseBoolean(parts[1]);
                    Log.d("Permission name ", name);

// Create a new Permission object and set values using setters
                    Permission permission = new Permission();
                    permission.setLabel(name);        // Set the label
                    permission.setEnabled(isGranted);  // Set the enabled status

                    permissions.add(permission);
                }
            }


        }

        tvTitleHeader=findViewById(R.id.toolbar_title);
        back_button=findViewById(R.id.back_button);
        viewChangePermission=findViewById(R.id.viewChangePermission);
        toggle_checkboxes=findViewById(R.id.toggle_checkboxes);


        tvTitleHeader.setText(getString(R.string.sharing_permission));

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        itemList = new ArrayList();
        itemList.add("Change Device Config");
        itemList.add("Cloud Video");
        itemList.add("Intercom");
        itemList.add("PTZ");
        itemList.add("Local Storage");
        itemList.add("Push");

        List<Integer> itemDrawableList = new ArrayList();
        itemDrawableList.add(R.drawable.set_ic_ptz);
        itemDrawableList.add(R.drawable.ic_share);
        itemDrawableList.add(R.drawable.ic_micro_sd_card);
        itemDrawableList.add(R.drawable.ic_setting);
        itemDrawableList.add(R.drawable.ic_mute);
        itemDrawableList.add(R.drawable.ic_cloud);

        // Setup RecyclerView with GridLayoutManager (2 columns)
        adapter = new ChangeSharepermissionAdapter(this, itemList, itemDrawableList, permissions, new ChangeSharepermissionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String label) {
                permission_name=label;
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewChangePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateAllPermissions();

            }
        });

        toggle_checkboxes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.toggleAllCheckboxes(isChecked);
            }
        });



    }

   /* private void updateAllPermissions() {
        // Assuming 'adapter' is your instance of ChangeSharepermissionAdapter
        List<Permission> updatedPermissions = adapter.getPermissions();  // Get updated permission list from adapter
        Log.d("UPDATED List", String.valueOf(DevDataCenter.getInstance().isLoginByAccount()));


        for (Permission permission : updatedPermissions) {
            Log.d("UPDATED List", String.valueOf(permission.isEnabled()));
            Log.d("UPDATED List", String.valueOf(permission.getLabel()));
            ShareManager.getInstance(this).setDevSharePermission(
                    share_id,
                    permission.getLabel() // Update the permission via SDK
            );
        }

        Toast.makeText(this, "All permissions updated successfully!", Toast.LENGTH_SHORT).show();
        finish();  // Close the activity if needed
    }*/
  /*  private void updateAllPermissions() {
        List<Permission> updatedPermissions = adapter.getPermissions();  // Get updated permission list from adapter

        for (Permission permission : updatedPermissions) {
            Log.d("UPDATED List", "Permission: " + permission.getLabel() + " Enabled: " + permission.isEnabled());

            // Only call API if the permission is enabled
            if (permission.isEnabled()) {
                ShareManager.getInstance(this).setDevSharePermission(
                        share_id,
                        permission.getLabel()  // Enable permission
                );
            } else {
                // Optionally, disable permission if needed
               *//* ShareManager.getInstance(this).setDevSharePermission(
                        share_id,
                        permission.getLabel()  // Disable permission
                );*//*
            }
        }

        Toast.makeText(this, "All permissions updated successfully!", Toast.LENGTH_SHORT).show();
        finish();  // Close the activity if needed
    }*/

    private void updateAllPermissions() {
        List<Permission> updatedPermissions = adapter.getPermissions();
        StringBuilder permissionBuilder = new StringBuilder();

        // Build a comma-separated list of enabled permissions
        for (Permission permission : updatedPermissions) {
            if (permission.isEnabled()) {
                if (permissionBuilder.length() > 0) {
                    permissionBuilder.append(","); // Add a comma separator
                }
                permissionBuilder.append(permission.getLabel());
            }
        }

        // Send the combined permissions to the SDK in one call
        ShareManager.getInstance(this).setDevSharePermission(share_id, permissionBuilder.toString());

        Toast.makeText(this, getString(R.string.all_permissions_updated_successfully), Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this,MeSharingManagement.class);
        startActivity(intent);
    }



}