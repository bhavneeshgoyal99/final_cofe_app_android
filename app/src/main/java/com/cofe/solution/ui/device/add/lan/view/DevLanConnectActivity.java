package com.cofe.solution.ui.device.add.lan.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.basic.G;
import com.bumptech.glide.Glide;
import com.lib.FunSDK;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.manager.db.XMDevInfo;
import com.xm.ui.widget.XTitleBar;

import java.util.ArrayList;
import java.util.List;

import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.device.add.AddNewDeviceActivity;
import com.cofe.solution.ui.device.add.lan.listener.DevLanConnectContract;
import com.cofe.solution.ui.device.add.lan.presenter.DevLanConnectPresenter;
import com.cofe.solution.ui.device.add.list.view.DevListActivity;
import com.cofe.solution.ui.device.add.qrcode.view.SetDevToRouterByQrCodeActivity;
import io.reactivex.annotations.Nullable;

/**
 * 局域网连接设备界面,显示设备列表菜单,包括名称,类型,mac,sn,ip,状态,以及是否在线
 * LAN connection device interface, display device list menu, including name,
 * type,mac,sn,ip, status, and whether online
 * Created by jiangping on 2018-10-23.
 */
public class DevLanConnectActivity extends DemoBaseActivity<DevLanConnectPresenter>
        implements DevLanConnectContract.IDevLanConnectView,AdapterView.OnItemClickListener {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    LinearLayout noDeviceContLl;
    Button addDevice;
    TextView text;
    ImageView loadImg;
    private Handler handler = new Handler();
    Runnable runnable;
    private int count = 0; // Timer count

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_lan);
        noDeviceContLl =  findViewById(R.id.no_device_cont_ll);
        titleBar = findViewById(R.id.layoutTop);



        titleBar.setTitleText(getString(R.string.guide_module_title_device_lan));
        titleBar.setRightBtnResource(R.mipmap.icon_refresh_normal,R.mipmap.icon_refresh_pressed);
        titleBar.setLeftClick(this);
        titleBar.setRightIvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {//Searching for Devices
                showWaitDialogHere();
                presenter.searchLanDevice();
            }
        });

        titleBar.setBottomTip(getClass().getName());
        listView = findViewById(R.id.list_lan);
        listView.setOnItemClickListener(this);


        loadImg = findViewById(R.id.load_img);
        addDevice = findViewById(R.id.add_btn);
        text = findViewById(R.id.text_txtv);
        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialogHere();
                presenter.searchLanDevice();

            }
        });

        noDeviceContLl.setVisibility(View.GONE);
        showWaitDialogHere();
        presenter.searchLanDevice();

    }

    void showWaitDialogHere(){
        loadImg.setVisibility(View.VISIBLE);
        Glide.with(DevLanConnectActivity.this)
                .asGif()
                .load(R.drawable.radar) // Place GIF in res/drawable
                .into(loadImg);

        runnable = new Runnable() {
            @Override
            public void run() {
                loadImg.setVisibility(View.GONE);

            }
        };

        handler.postDelayed(runnable, 3000);
        noDeviceContLl.setVisibility(View.GONE);
    }

    void deviceFound(boolean isDeviceFound) {
        if(isDeviceFound){
            noDeviceContLl.setVisibility(View.GONE);
        } else {
            noDeviceContLl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUpdateView() {   //Gets the callback for the list of LAN links
        hideProgress();
        List<XMDevInfo> localDevList = presenter.getLanDevList();
        if (localDevList != null && !localDevList.isEmpty()) {
            List<String> devinfoList = new ArrayList<>();
            for (XMDevInfo xmDevInfo : localDevList) {
                devinfoList.add(xmDevInfo.toString() + "\nPID:" + xmDevInfo.getPid());
            }
            adapter = new ArrayAdapter<>(this, R.layout.adapter_simple_list, devinfoList);
            listView.setAdapter(adapter);
        } else {

            deviceFound(false);
            text.setText(getString(R.string.Add_Dev_Failed));
            addDevice.setText(getString(R.string.try_to_connect_the_device_again));

            Toast.makeText(this, FunSDK.TS("No_device"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAddDevResult(boolean isSuccess,int errorId) { //Add direct jump successfully
        if (isSuccess) {
            deviceFound(true);


            turnToActivity(DevListActivity.class);
        } else {
            addDevice.setText(getString(R.string.try_to_connect_the_device_again));
            text.setText(getString(R.string.Add_Dev_Failed));
            deviceFound(false);

            showToast(getString(R.string.Add_Dev_Failed) ,Toast.LENGTH_LONG);
        }
    }

    @Override
    public DevLanConnectPresenter getPresenter() {
        return new DevLanConnectPresenter(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        XMDevInfo xmDevInfo = presenter.getLanDevInfo(i);
        if (xmDevInfo != null) {
            showProgress();
            presenter.addDeviceToAccount(xmDevInfo); // Just click and it will be added to the current account
        }
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        handler.removeCallbacks(null);

        super.onDestroy();

    }

}
