package com.cofe.solution.ui.device.browse.view;

import android.os.Bundle;
import android.view.Window;

import com.xm.ui.widget.XTitleBar;

import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.device.browse.listener.DevBrowseFileContract;
import com.cofe.solution.ui.device.browse.presenter.DevBrowseFilePresenter;
import io.reactivex.annotations.Nullable;

/**
 * 查看设备文件界面,显示相关的列表菜单.
 * Created by jiangping on 2018-10-23.
 */
public class DevBrowseFileActivity extends DemoBaseActivity<DevBrowseFilePresenter> implements DevBrowseFileContract.IDevBrowseFileView {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dev_browser_file);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.guide_module_title_device_browse));
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(getClass().getName());
    }

    @Override
    public void onUpdateView() {

    }

    @Override
    public DevBrowseFilePresenter getPresenter() {
        return new DevBrowseFilePresenter(this);
    }
}
