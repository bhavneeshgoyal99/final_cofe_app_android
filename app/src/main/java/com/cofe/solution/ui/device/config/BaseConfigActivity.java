package com.cofe.solution.ui.device.config;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.cofe.solution.ui.dialog.DialogWaiting;
import com.cofe.solution.ui.activity.lib.XMBaseActivity;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.widget.XTitleBar;

import java.util.Locale;

/**
 * @author hws
 * @name XMFunSDKDemo_Android
 * @class name：com.cofe.solution.ui.device.config
 * @class describe
 * @time 2018-10-27 15:15
 */
public abstract class BaseConfigActivity<T extends XMBasePresenter> extends XMBaseActivity<T> {
    public static final String androidJsonDoc = "https://docs.jftech.com/docs?menusId=ab0ed73834f54368be3e375075e27fb2&siderId=2386446054664a5ba49e139ad10fb12c&lang=" + Locale.getDefault().getLanguage();
    protected XTitleBar titleBar;

    @Override
    public T getPresenter() {
        return null;
    }

    private DialogWaiting mWaitDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (titleBar != null) {
            titleBar.setBottomTip(getClass().getName());
        }
    }

    public void openBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void showProgress() {
        mWaitDialog = new DialogWaiting(this);
        mWaitDialog.show();
    }

    public void hideProgress() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
        }
    }
}
