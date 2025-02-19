package com.cofe.solution.ui.device.add.sn.presenter;

import static com.manager.db.Define.LOGIN_NONE;

import android.os.Message;
import android.text.TextUtils;

import com.basic.G;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;

import com.manager.device.DeviceManager;
import com.xm.activity.base.XMBasePresenter;

import com.cofe.solution.ui.device.add.sn.listener.DevSnConnectContract;

/**
 * Connect the device with the serial number, and login to the device according to the serial number
 * and account password of the device type, or the IP device port and account password.
 * <p>
 * Created by jiangping on 2018-10-23.
 */
public class DevSnConnectPresenter extends XMBasePresenter<AccountManager> implements DevSnConnectContract.IDevSnConnectPresenter {

    private DevSnConnectContract.IDevSnConnectView iDevSnConnectView;

    public DevSnConnectPresenter(DevSnConnectContract.IDevSnConnectView iDevSnConnectView) {
        this.iDevSnConnectView = iDevSnConnectView;
    }

    @Override
    protected AccountManager getManager() {
        return AccountManager.getInstance();
    }

    /**
     * Add device
     *
     * @param devId    serial number
     * @param userName login userName
     * @param pwd      login password
     * @param devType  device Type
     */

    /*序列号连接设备*/
    /*Connect the device by its sequence number*/
    @Override
    public void addDev(String devId, String userName, String pwd, String devToken, int devType, String pid) {
        SDBDeviceInfo deviceInfo = new SDBDeviceInfo();
        G.SetValue(deviceInfo.st_0_Devmac, devId);//设备序列号
        G.SetValue(deviceInfo.st_1_Devname, devId);
        G.SetValue(deviceInfo.st_4_loginName, userName);
        deviceInfo.st_7_nType = devType;
        XMDevInfo xmDevInfo = new XMDevInfo();
        xmDevInfo.setDevPassword(pwd);
        xmDevInfo.setDevUserName(userName);
        xmDevInfo.sdbDevInfoToXMDevInfo(deviceInfo);
        if (!StringUtils.isStringNULL(devToken)) {
            xmDevInfo.setDevToken(devToken);
        }

        if (!TextUtils.isEmpty(pid)) {
            xmDevInfo.setPid(pid);
        }

        manager.xmLogin(userName, pwd, 1,new  BaseAccountManager.OnAccountManagerListener(){

            @Override
            public void onSuccess(int msgId) {

            }

            @Override
            public void onFailed(int msgId, int errorId) {

            }

            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {

            }
        });//LOGIN_BY_INTERNET（1）  Account login type


        if (DevDataCenter.getInstance().getLoginType() == LOGIN_NONE) {
            DevDataCenter.getInstance().addDev(xmDevInfo);
            FunSDK.AddDevInfoToDataCenter(G.ObjToBytes(xmDevInfo.getSdbDevInfo()), 0, 0, "");
            if (iDevSnConnectView != null) {
                iDevSnConnectView.onAddDevResult(true, 0);
            }
        } else {
            manager.addDev(xmDevInfo, new BaseAccountManager.OnAccountManagerListener() {  //The WiFi-paired addDev is missing a parameter to unbind the current device
                @Override
                public void onSuccess(int i) {
                    if (iDevSnConnectView != null) {
                        iDevSnConnectView.onAddDevResult(true, 0);
                    }
                }

                @Override
                public void onFailed(int i, int errorId) {
                    if (iDevSnConnectView != null) {
                        iDevSnConnectView.onAddDevResult(false, errorId);
                    }
                }

                @Override
                public void onFunSDKResult(Message message, MsgContent msgContent) {

                }
            });
        }
    }
}

