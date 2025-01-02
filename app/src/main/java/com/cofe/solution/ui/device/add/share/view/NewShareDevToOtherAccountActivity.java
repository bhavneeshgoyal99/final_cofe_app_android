package com.cofe.solution.ui.device.add.share.view;

import static com.lib.EFUN_ATTR.LOGIN_USER_ID;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.device.add.share.listener.DevShareConnectContract;
import com.cofe.solution.ui.device.add.share.presenter.DevShareConnectPresenter;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.sdk.bean.share.SearchUserInfoBean;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.account.XMAccountManager;
import com.manager.db.DevDataCenter;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import io.reactivex.annotations.Nullable;

/**
 * Shared device interface, display related list menu
 */
public class NewShareDevToOtherAccountActivity extends DemoBaseActivity<DevShareConnectPresenter> implements DevShareConnectContract.IDevShareConnectView {
    private EditText etShareAccount;
    private ListSelectItem lsiShareAccountInfo;
    private ImageView ivQrCode;//分享的二维码布局

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dev_share_connect_new);

        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.share_device_by_account));

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etShareAccount = findViewById(R.id.et_search_bar_input_1);
        lsiShareAccountInfo = findViewById(R.id.lsi_share_account);

        lsiShareAccountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.shareDevToOther(lsiShareAccountInfo.getTip());
            }
        });
        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchAccount(view);
            }
        });
        ivQrCode = findViewById(R.id.iv_qr_code);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Bitmap bitmap = presenter.getShareDevQrCode(this);
        ivQrCode.setImageBitmap(bitmap);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onSearchShareAccountResult(boolean isSuccess, SearchUserInfoBean searchUserInfoBean) {
        if (isSuccess) {
            if (searchUserInfoBean != null) {
                lsiShareAccountInfo.setTitle(searchUserInfoBean.getAccount());
                lsiShareAccountInfo.setTip(searchUserInfoBean.getId());
            }
        } else {
            ToastUtils.showLong(R.string.not_found);
        }
    }

    @Override
    public void onShareDevResult(boolean isSuccess) {
        if (isSuccess) {
            turnToActivity(DevShareAccountListActivity.class);
        }

        ToastUtils.showLong(isSuccess ? getString(R.string.share_s) : getString(R.string.share_f));
    }

    @Override
    public void onUpdateView() {

    }

    @Override
    public DevShareConnectPresenter getPresenter() {
        return new DevShareConnectPresenter(this);
    }

    /**
     * 分享的账号查询
     * Shared account query
     *
     * @param view
     */
    public void onSearchAccount(View view) {

        if(DevDataCenter.getInstance().getAccountUserName()!=null) {
            if(DevDataCenter.getInstance().getAccessToken()==null) {
                AccountManager.getInstance().xmLogin(DevDataCenter.getInstance().getAccountUserName(), DevDataCenter.getInstance().getAccountPassword(), 1,
                        new BaseAccountManager.OnAccountManagerListener() {
                            @Override
                            public void onSuccess(int msgId) {
                                Log.d("Access toekn" ," > "  +DevDataCenter.getInstance().getAccessToken());
                                presenter.searchShareAccount(etShareAccount.getText().toString().trim());


                            }

                            @Override
                            public void onFailed(int msgId, int errorId) {

                            }

                            @Override
                            public void onFunSDKResult(Message msg, MsgContent ex) {

                            }
                        });//LOGIN_BY_INTERNET（1）  Account login type

            } else {
                presenter.searchShareAccount(etShareAccount.getText().toString().trim());

            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.release();
        }
    }
}