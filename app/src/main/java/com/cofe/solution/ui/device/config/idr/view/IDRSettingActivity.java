package com.cofe.solution.ui.device.config.idr.view;

import android.os.Bundle;
import android.view.View;

import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.listselectitem.extra.view.ExtraSpinner;

import com.cofe.solution.R;
import com.cofe.solution.ui.device.config.BaseConfigActivity;
import com.cofe.solution.ui.device.config.door.contract.DoorSettingContract;
import com.cofe.solution.ui.device.config.door.presenter.DoorSettingPresenter;
import com.cofe.solution.ui.device.config.door.view.DoorSettingContactActivity;
import com.cofe.solution.ui.device.config.simpleconfig.view.DevSimpleConfigActivity;

/**
 * 低功耗设备相关配置
 */
public class IDRSettingActivity extends BaseConfigActivity {
    /**
     * PIR报警配置
     */
    private ListSelectItem lsiPirSettings;

    @Override
    public XMBasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idr_setting);
        initView();
    }

    private void initView() {
        titleBar = findViewById(R.id.xb_idr_setting);
        titleBar.setTitleText(getString(R.string.idr_setting));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });

        lsiPirSettings = findViewById(R.id.lsi_idr_pir_settings);
        lsiPirSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnToActivity(DevSimpleConfigActivity.class, new Object[][]{{"jsonName", "Alarm.PIR"}, {"configName", "PIR报警设置\nPIR Alarm Settings"}, {"cmdId", 1042}});
            }
        });
    }
}
