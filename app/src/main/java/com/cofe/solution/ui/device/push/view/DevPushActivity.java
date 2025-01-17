package com.cofe.solution.ui.device.push.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lib.SDKCONST;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.device.push.listener.DevPushContract;
import com.cofe.solution.ui.device.push.presenter.DevPushPresenter;
import com.cofe.solution.ui.user.modify.view.UserModifyPwdActivity;

/**
 * @author hws
 * @class 设备推送设置
 * @time 2020/7/24 16:44
 */
public class DevPushActivity extends DemoBaseActivity<DevPushPresenter> implements DevPushContract.IDevPushView {
    private ListSelectItem lsiPushSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_push);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.push_set));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        titleBar.setBottomTip(DevPushActivity.class.getName());

        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.push_set));
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        lsiPushSwitch = findViewById(R.id.lsi_push_switch);
        lsiPushSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lsiPushSwitch.setRightImage(lsiPushSwitch.getRightValue() == SDKCONST.Switch.Open
                        ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
                if (lsiPushSwitch.getRightValue() == SDKCONST.Switch.Open) {
                    presenter.openPush();
                }else {
                    presenter.closePush();
                }
            }
        });
    }

    private void initData() {
        lsiPushSwitch.setRightImage(presenter.isPushOpen() ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
    }

    @Override
    public DevPushPresenter getPresenter() {
        return new DevPushPresenter(this);
    }

    @Override
    public void onPushStateResult(boolean isPushOpen) {
        lsiPushSwitch.setRightImage(isPushOpen ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
    }
}
