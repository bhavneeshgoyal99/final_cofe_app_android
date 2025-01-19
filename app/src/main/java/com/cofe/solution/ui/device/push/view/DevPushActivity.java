package com.cofe.solution.ui.device.push.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cofe.solution.ui.user.login.view.UserLoginActivity;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.alarm.AlarmGroup;
import com.lib.sdk.bean.alarm.AlarmInfo;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.device.DeviceManager;
import com.manager.device.alarm.DevAlarmInfoManager;
import com.manager.push.XMPushManager;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.device.push.listener.DevPushContract;
import com.cofe.solution.ui.device.push.presenter.DevPushPresenter;
import com.cofe.solution.ui.user.modify.view.UserModifyPwdActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

        if (!DevDataCenter.getInstance().isLoginByAccount()) {
            // titleBar.setRightTitleText(getString(R.string.clear_dev_list));
            if(DevDataCenter.getInstance().getAccountUserName()!=null) {
                if(DevDataCenter.getInstance().getAccessToken()==null) {
                    AccountManager.getInstance().xmLogin(DevDataCenter.getInstance().getAccountUserName(), DevDataCenter.getInstance().getAccountPassword(), 1,
                            new BaseAccountManager.OnAccountManagerListener() {
                                @Override
                                public void onSuccess(int msgId) {
                                    Log.d("Access toekn" ," > "  +DevDataCenter.getInstance().getAccessToken());

                                }

                                @Override
                                public void onFailed(int msgId, int errorId) {
                                }

                                @Override
                                public void onFunSDKResult(Message msg, MsgContent ex) {

                                }
                            });//LOGIN_BY_INTERNET（1）  Account login type

                }

            } else {
                Log.d("account not present " ," > "  +DevDataCenter.getInstance().getAccessToken());
                //finish();
                //startActivity(new Intent(this, UserLoginActivity.class));
            }
        } else {
            Log.d("customer not logged in  " ," > "  +DevDataCenter.getInstance().getAccountUserName());
        }

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
        XMPushManager manager = new XMPushManager(new XMPushManager.OnXMPushLinkListener(){

            @Override
            public void onPushInit(int pushType, int errorId) {

            }

            @Override
            public void onPushLink(int pushType, String devId, int seq, int errorId) {

            }

            @Override
            public void onPushUnLink(int pushType, String devId, int seq, int errorId) {

            }

            @Override
            public void onIsPushLinkedFromServer(int pushType, String devId, boolean isLinked) {
                Log.d(getClass().getName() , "onIsPushLinkedFromServer > "+pushType  + " |  dev id > " + devId  + "   | isLinked > "  + isLinked);
            }

            @Override
            public void onAlarmInfo(int pushType, String devId, Message msg, MsgContent ex) {

            }

            @Override
            public void onLinkDisconnect(int pushType, String devId) {

            }

            @Override
            public void onWeChatState(String devId, int state, int errorId) {

            }

            @Override
            public void onThirdPushState(String info, int pushType, int state, int errorId) {

            }

            @Override
            public void onAllUnLinkResult(boolean isSuccess) {

            }

            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {

            }
        } );
        manager.isAlarmLinked(presenter.getDevId());

        Log.d(getClass().getName(),  "Switch  > "+presenter.isPushOpen()  +"devId "  +presenter.getDevId());


        DevAlarmInfoManager devAlarmInfoManager = new DevAlarmInfoManager(new DevAlarmInfoManager.OnAlarmInfoListener() { // Callback of the query list
            @Override
            public void onSearchResult(List<AlarmGroup> list) {
                Log.d(getClass().getName(),  "onSearchResult  > "+list);
            }

            @Override
            public void onDeleteResult(boolean isSuccess, Message message, MsgContent msgContent, List<AlarmInfo> list) { // Remove callbacks for a single item
                if (isSuccess) {
                }
            }
        });

        devAlarmInfoManager.searchAlarmInfoAll(presenter.getDevId(),presenter.getChnId());
        final Calendar startDate = Calendar.getInstance();
        startDate.set(2020, Calendar.JANUARY, 10);
        final Calendar stopDate = Calendar.getInstance();
        stopDate.getTime();
        // Format the date as yyyy-MM-dd
        devAlarmInfoManager.searchAlarmInfoByTime(presenter.getDevId(), presenter.getChnId(),  startDate, stopDate, 1);


        lsiPushSwitch.setRightImage(lsiPushSwitch.getRightValue() == SDKCONST.Switch.Open
                ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);

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
