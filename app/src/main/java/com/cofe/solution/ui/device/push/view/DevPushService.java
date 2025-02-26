package com.cofe.solution.ui.device.push.view;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.basic.G;
import com.cofe.solution.ui.activity.SplashScreen;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lib.Mps.SMCInitInfo;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.alarm.AlarmGroup;
import com.lib.sdk.bean.alarm.AlarmInfo;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.account.XMAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.device.alarm.DevAlarmInfoManager;
import com.manager.image.BaseImageManager;
import com.manager.image.CloudImageManager;
import com.manager.push.XMPushManager;
import com.utils.XUtils;
import com.xm.ui.dialog.XMPromptDlg;

import static com.manager.push.XMPushManager.MSG_TYPE_FAMILY_CALL;
import static com.manager.push.XMPushManager.MSG_TYPE_VIDEO_TALK;
import static com.manager.push.XMPushManager.PUSH_TYPE_XM;
import static com.manager.push.XMPushManager.TYPE_DOOR_BELL;
import static com.manager.push.XMPushManager.TYPE_FORCE_DISMANTLE;
import static com.manager.push.XMPushManager.TYPE_LOCAL_ALARM;
import static com.manager.push.XMPushManager.TYPE_REMOTE_CALL_ALARM;

import static com.cofe.solution.app.SDKDemoApplication.PATH_PHOTO;
import static com.cofe.solution.app.SDKDemoApplication.PATH_PHOTO_TEMP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cofe.solution.R;
import com.cofe.solution.app.SDKDemoApplication;
import com.cofe.solution.ui.entity.AlarmTranslationIconBean;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;


/**
 * @author hws
 * @class
 * @time 2020/8/13 18:53
 */
public class DevPushService extends Service implements DevAlarmInfoManager.OnAlarmInfoListener {
    private XMPushManager xmPushManager;
    private DevAlarmInfoManager devAlarmInfoManager;
    int count = 0;	
 private static final String CHANNEL_ID = "custom_channel";
    private static final String CHANNEL_NAME = "Custom Notifications";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(DevDataCenter.getInstance().getAccountUserName()!=null) {
            if(DevDataCenter.getInstance().getAccessToken()==null) {
                AccountManager.getInstance().xmLogin(DevDataCenter.getInstance().getAccountUserName(), DevDataCenter.getInstance().getAccountPassword(), 1,
                        new BaseAccountManager.OnAccountManagerListener() {
                            @Override
                            public void onSuccess(int msgId) {
                                if (DevDataCenter.getInstance().isLoginByAccount()) {
                                    initPush();
                                    initAlarmInfo();
                                    Toast.makeText(getApplicationContext(), R.string.start_push_service, Toast.LENGTH_LONG).show();
                                } else {
                                    stopSelf();
                                    System.out.println(getString(R.string.start_push_service_error_tips));
                                }

                            }

                            @Override
                            public void onFailed(int msgId, int errorId) {
                                if (DevDataCenter.getInstance().isLoginByAccount()) {
                                    initPush();
                                    initAlarmInfo();
                                    Toast.makeText(getApplicationContext(), R.string.start_push_service, Toast.LENGTH_LONG).show();
                                } else {
                                    stopSelf();
                                    System.out.println(getString(R.string.start_push_service_error_tips));
                                }

                            }

                            @Override
                            public void onFunSDKResult(Message msg, MsgContent ex) {

                            }
                        });//LOGIN_BY_INTERNET（1）  Account login type

            }
            }


    }

    /**
     * 初始化推送
     */
    private void initPush() {
        xmPushManager = new XMPushManager(xmPushLinkResult);
        String pushToken = XUtils.getPushToken(this);
        if (!StringUtils.isStringNULL(pushToken)) {
            SMCInitInfo info = new SMCInitInfo();
            G.SetValue(info.st_0_user, XMAccountManager.getInstance().getAccountName());
            G.SetValue(info.st_1_password, XMAccountManager.getInstance().getPassword());
            G.SetValue(info.st_2_token, pushToken);
            xmPushManager.initFunSDKPush(this, info, PUSH_TYPE_XM);
        }
    }

    /**
     * 获取报警消息翻译内容和图标
     */
    private void initAlarmInfo() {
        // 使用Map直接传参
        Map<String, Object> dataMap = new HashMap<>();

        // 添加参数到Map
        dataMap.put("msg", "get_translate_icon");
        dataMap.put("timeout", 8000);


        ArrayList ltList = new ArrayList();
        ltList.add("ZH");
        dataMap.put("app", XUtils.getPackageName(this));
        dataMap.put("st", "AND");
        dataMap.put("appvs", XUtils.getVersion(this));
        dataMap.put("lt", ltList);
        devAlarmInfoManager = new DevAlarmInfoManager(this);
        devAlarmInfoManager.getAlarmMsgTranslationAndIcon(new Gson().toJson(dataMap), 0);
    }

    /**
     * 用URL初始化报警消息订阅
     * 使用Url初始化报警推送,主要修改: appType设置”Third:url”
     */
    private void initPushByUrl() {
        xmPushManager = new XMPushManager(xmPushLinkResult);
        String pushToken = XUtils.getPushToken(this);
        if (!StringUtils.isStringNULL(pushToken)) {
            SMCInitInfo info = new SMCInitInfo();
            G.SetValue(info.st_0_user, XMAccountManager.getInstance().getAccountName());
            G.SetValue(info.st_1_password, XMAccountManager.getInstance().getPassword());
            G.SetValue(info.st_2_token, pushToken);
            G.SetValue(info.st_5_appType, "Third:http://xxxxx xxxx");
            //(例：Third:http(s)://xmey.net:80/xxx/xxx 或 Third:ip:80 中间用“:”做分隔符)
            xmPushManager.initFunSDKPush(this, info, PUSH_TYPE_XM);
        }
    }

    private XMPushManager.OnXMPushLinkListener xmPushLinkResult = new XMPushManager.OnXMPushLinkListener() {
        @Override
        public void onPushInit(int pushType, int errorId) {
            if (errorId >= 0) {
                System.out.println("推送初始化成功:" + pushType);
            } else {
                System.out.println("推送初始化失败:" + errorId);
            }
        }

        @Override
        public void onPushLink(int pushType, String devId, int seq, int errorId) {
            if (errorId >= 0) {
                System.out.println("推送订阅成功:" + devId);
            } else {
                System.out.println("推送订阅失败:" + devId );
            }
        }

        @Override
        public void onPushUnLink(int pushType, String devId, int seq, int errorId) {
            if (errorId >= 0) {
                System.out.println("取消订阅成功:" + devId);
            } else {
                System.out.println("取消订阅失败:" + devId );
            }
        }

        @Override
        public void onIsPushLinkedFromServer(int pushType, String devId, boolean isLinked) {

        }

        @Override
        public void onAlarmInfo(int pushType, String devId, Message message, MsgContent msgContent) {
            String pushMsg = G.ToString(msgContent.pData);
            System.out.println("接收到报警消息:" + pushMsg);
            parseAlarmInfo(devId, pushMsg);
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
        public void onFunSDKResult(Message message, MsgContent msgContent) {

        }
    };

    /**
     * 解析报警消息
     * Parse alarm message
     *
     * @param devId   设备序列号 Device serial number
     * @param pushMsg 推送消息 Push message
     */
    private void parseAlarmInfo(String devId, String pushMsg) {
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.onParse(pushMsg);
        Toast.makeText(getApplicationContext(), getString(R.string.received_alarm_message) +
                XMPushManager.getAlarmName(getApplicationContext(), alarmInfo.getEvent()) + ":" +
                alarmInfo.getStartTime(), Toast.LENGTH_LONG).show();

       NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel (required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Create a PendingIntent to open an activity when the notification is tapped
        Intent intent = new Intent(getApplicationContext(), SplashScreen.class); // Replace with your target activity
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app icon
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.received_alarm_message) +
                XMPushManager.getAlarmName(getApplicationContext(), alarmInfo.getEvent()) + ":" +
                alarmInfo.getStartTime())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        count =  count +1 ;
        notificationManager.notify(count, builder.build());
        //如果是来电消息才需要弹出来电页面
        // Show incoming call page only if it's a call message
        if (isCallAlarm(alarmInfo.getEvent(), alarmInfo.getMsgType(), devId)) {
            Intent intent1 = new Intent(DevPushService.this, DevIncomingCallActivity.class);
            intent1.putExtra("devId", devId);
            intent1.putExtra("alarmTime", alarmInfo.getStartTime());
            intent1.putExtra("alarm_msg_type", alarmInfo.getMsgType());
            intent1.setFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        }
    }

    /**
     * 是否是来电通知
     * Whether it is a call notification
     *
     * @param alarmEvent 报警事件 Alarm event
     * @param msgType    报警类型 Alarm type
     * @param devId      设备序列号 Device serial number
     * @return
     */
    private boolean isCallAlarm(String alarmEvent, String msgType, String devId) {
        switch (alarmEvent) {
            case TYPE_LOCAL_ALARM:
                return DevDataCenter.getInstance().isLowPowerDev(devId);
            case TYPE_DOOR_BELL:
            case TYPE_FORCE_DISMANTLE:
            case TYPE_REMOTE_CALL_ALARM: //监控设备呼叫
                return true;
            case MSG_TYPE_VIDEO_TALK: // 带屏摇头机来电呼叫
                if (StringUtils.contrastIgnoreCase(msgType, MSG_TYPE_FAMILY_CALL)) {
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }

    @Override
    public void onSearchResult(List<AlarmGroup> list) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess, Message msg, MsgContent ex, List<AlarmInfo> deleteAlarmInfos) {

    }

    @Override
    public void onAlarmMsgTranslationAndIconResult(boolean isSuccess, Message msg, MsgContent ex, String resultJson) {
        System.out.println("resultJson:" + resultJson);
        AlarmTranslationIconBean alarmTranslationIconBean = new AlarmTranslationIconBean();
        ((SDKDemoApplication) getApplication()).setAlarmTranslationIconBean(alarmTranslationIconBean);
        JSONObject jsonObject = JSON.parseObject(resultJson);
        if (jsonObject != null && jsonObject.containsKey("ifs")) {
            JSONObject ifsObj = jsonObject.getJSONObject("ifs");
            if (ifsObj != null) {
                for (Map.Entry entry : ifsObj.entrySet()) {
                    HashMap<String, AlarmTranslationIconBean.AlarmLanIconInfo> alarmLanIconInfoHashMap = new HashMap<>();
                    JSONArray list = (JSONArray) entry.getValue();
                    String lanKey = (String) entry.getKey();
                    for (int i = 0; i < list.size(); ++i) {

                        AlarmTranslationIconBean.AlarmLanIconInfo alarmLanIconInfo = new AlarmTranslationIconBean.AlarmLanIconInfo();
                        JSONObject obj = (JSONObject) list.get(i);
                        String eventKey = obj.getString("et");
                        alarmLanIconInfo.setEt(obj.getString("et"));
                        alarmLanIconInfo.setTl(obj.getString("tl"));
                        alarmLanIconInfo.setUrl(obj.getString("url"));
                        alarmLanIconInfoHashMap.put(eventKey, alarmLanIconInfo);
                    }

                    alarmTranslationIconBean.getLanguageInfo().put(lanKey, alarmLanIconInfoHashMap);
                }
            }
        }
    }
}
