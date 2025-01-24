package com.cofe.solution.ui.device.add.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.ui.adapter.ShareDevicePermissionGridAdapter;
import com.cofe.solution.ui.device.add.share.view.NewShareDevToOtherAccountActivity;
import com.cofe.solution.ui.device.push.view.DevPushActivity;
import com.google.gson.Gson;
import com.manager.db.XMDevInfo;

import com.cofe.solution.R;
import com.cofe.solution.ui.device.add.share.view.DevShareAccountListActivity;
import com.cofe.solution.ui.device.add.share.view.ShareDevToOtherAccountActivity;
import com.cofe.solution.ui.device.config.DeviceSetting;

import java.util.ArrayList;
import java.util.List;

public class ShareFirstScren extends AppCompatActivity {
    XMDevInfo xmDevInfo;
    Context context;
    ShareDevicePermissionGridAdapter adapter;
    List<String> itemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_deviec_frist_screen);
        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.device_setting));
        SwitchCompat toggleSwitch = findViewById(R.id.toggle_checkboxes);

        String personJson = getIntent().getStringExtra("dev");

        // Convert the JSON string back to a Person object
        if (personJson != null) {
            Gson gson = new Gson();
            xmDevInfo = gson.fromJson(personJson, XMDevInfo.class);
        }

        context = ShareFirstScren.this;

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        adapter = new ShareDevicePermissionGridAdapter(this, itemList, itemDrawableList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);

        // Enable all checkboxes when button is clicked
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.toggleAllCheckboxes(isChecked);
            }
        });

        // Get checked items

        // Permissions IDs
        LinearLayout ptzIcon = findViewById(R.id.ptz_ll); // Add ID in XML if missing
        LinearLayout intercomIcon = findViewById(R.id.share_ll); // Add ID in XML if missing
        LinearLayout localStorageIcon = findViewById(R.id.storage_ll); // Add ID in XML if missing
        LinearLayout changeDeviceConfigIcon = findViewById(R.id.config_ll); // Add ID in XML if missing
        LinearLayout pushIcon = findViewById(R.id.push_ll); // Add ID in XML if missing
        LinearLayout viewCloudVideoIcon = findViewById(R.id.cloud_ll); // Add ID in XML if missing

        // Sharing Methods
        LinearLayout shareQRCodeIcon = findViewById(R.id.qr_ll); // Add ID in XML if missing
        LinearLayout accountShareIcon = findViewById(R.id.account_ll); // Add ID in XML if missing

        TextView footerLink = findViewById(R.id.view_share_management);


        // Set OnClickListener for icons
        /*ptzIcon.setOnClickListener(v -> showToast("PTZ selected"));
        intercomIcon.setOnClickListener(v -> showToast("Intercom selected"));
        localStorageIcon.setOnClickListener(v -> showToast("Local Storage selected"));
        changeDeviceConfigIcon.setOnClickListener(v -> showToast("Change Device Config selected"));
        pushIcon.setOnClickListener(v -> showToast("Push selected"));
        viewCloudVideoIcon.setOnClickListener(v -> showToast("View Cloud Video selected"));*/

        // Set OnClickListener for sharing methods
        shareQRCodeIcon.setOnClickListener(v ->

                sharQR("Share QR Code selected")

        );
        accountShareIcon.setOnClickListener(v ->showToast("Account Share selected"));

        // Set OnClickListener for footer link
        footerLink.setOnClickListener(v -> showToast("View Share Management clicked"));
    }

    // Helper method to display a Toast message
    private void sharQR(String message) {
        Gson gson = new Gson();
        String personJson = gson.toJson(xmDevInfo);
        List<Integer> checkedPositions = adapter.getCheckedItems();
        ArrayList<String> permissionEnabled =  new ArrayList<>();

        for (int i = 0; i < checkedPositions.size(); i++) {
            permissionEnabled.add(itemList.get(checkedPositions.get(i)));
        }

        Intent intent = new Intent(this, ShareDevToOtherAccountActivity.class);
        intent.putExtra("dev", personJson);
        intent.putStringArrayListExtra("permission", permissionEnabled);
        startActivity(intent);
    }

    void showToast(String text) {
        Gson gson = new Gson();
        String personJson = gson.toJson(xmDevInfo);

        Intent intent = new Intent(this, NewShareDevToOtherAccountActivity.class);
        intent.putExtra("dev", personJson);
        startActivity(intent);

    }
}