package com.cofe.solution.ui.device.config.fisheyecontrol.view;

import android.os.Bundle;
import android.view.Window;

import com.cofe.solution.R;
import com.cofe.solution.ui.device.config.BaseConfigActivity;
import com.cofe.solution.ui.device.config.fisheyecontrol.listener.DevFishEyeSetContract;
import com.cofe.solution.ui.device.config.fisheyecontrol.presenter.DevFishEyeSetPresenter;
import io.reactivex.annotations.Nullable;

/**
 * 鱼眼灯泡控制,包括模式,类型及自动模式时间间隔
 * Created by jiangping on 2018-10-23.
 */
public class DevFishEyeSetActivity extends BaseConfigActivity<DevFishEyeSetPresenter> implements DevFishEyeSetContract.IDevFishEyeSetView {
    @Override
    public DevFishEyeSetPresenter getPresenter() {
        return new DevFishEyeSetPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_setup_camerafisheye);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_setup_camerafisheye));
        titleBar.setLeftClick(this);
    }

    @Override
    public void onUpdateView() {

    }
}
