package com.cofe.solution.ui.device.config.cameralink.view;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;

import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.device.config.cameralink.listener.CameraLinkInitContract;
import com.cofe.solution.ui.device.config.cameralink.presenter.CameraLinkInitPresenter;

public class CameraLinkInitActivity extends DemoBaseActivity<CameraLinkInitPresenter> implements CameraLinkInitContract.ICameraLinkInitView {
    @Override
    public CameraLinkInitPresenter getPresenter() {
        return new CameraLinkInitPresenter(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void showWaitDlgShow(boolean show) {
        if(show){
            showProgress();
        }else {
            hideProgress();
        }
    }

    @Override
    public void initFailed() {
        finish();
    }
}
