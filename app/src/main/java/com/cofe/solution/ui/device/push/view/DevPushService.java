package com.cofe.solution.ui.device.push.view;


import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.basic.G;
import com.cofe.solution.R;
import com.cofe.solution.app.SDKDemoApplication;
import com.cofe.solution.ui.activity.SplashScreen;
import com.cofe.solution.ui.entity.AlarmTranslationIconBean;
import com.cofe.solution.utils.BatteryOptimizationHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lib.Mps.SMCInitInfo;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.alarm.AlarmGroup;
import com.lib.sdk.bean.alarm.AlarmInfo;
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hws
 * @class
 * @time 2020/8/13 18:53
 */
public class DevPushService extends Service implements DevAlarmInfoManager.OnAlarmInfoListener {
    private XMPushManager xmPushManager;
    private DevAlarmInfoManager devAlarmInfoManager;
    Runnable runnable = null;
    Handler handler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(() -> BatteryOptimizationHelper.checkAndRequestBatteryOptimization(this)).start();
        handler=  new Handler();
        Log.d("service is created now" , "Service onCreate called");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "push_channel",
                    "Push Notifications",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }

            Notification notification = new NotificationCompat.Builder(this, "push_channel")
                    .setContentTitle("Push Service")
                    .setContentText("started...")
                    .setSmallIcon(R.drawable.ic_launcher_)
                    .build();

            startForeground(1, notification);

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 (API 34)
                startForeground(1, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC);
            } else {
                startForeground(1, notification);
            }*/

            // Delay stopping foreground to allow the service to start properly
            //new Handler(Looper.getMainLooper()).postDelayed(() -> stopForeground(STOP_FOREGROUND_REMOVE), 3000);

            //new Handler(Looper.getMainLooper()).postDelayed(() -> stopForeground(true), 3000);
        }

        if (DevDataCenter.getInstance().isLoginByAccount()) {
            initPush();
            initAlarmInfo();
            Toast.makeText(getApplicationContext(), R.string.start_push_service, Toast.LENGTH_LONG).show();
        } else {
            stopSelf();
            System.out.println(getString(R.string.start_push_service_error_tips));
        }
        Handler handler1=  new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getApplicationContext(), "Service is running", Toast.LENGTH_SHORT).show();
                Log.d("service" , "Service is running");
                handler1.postDelayed(this, 5000); // Repeat every 5 seconds
            }
        };
        handler1.postDelayed(runnable, 5000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
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
                System.out.println("推送订阅失败:" + devId + ":" + errorId);
            }
        }

        @Override
        public void onPushUnLink(int pushType, String devId, int seq, int errorId) {
            if (errorId >= 0) {
                System.out.println("取消订阅成功:" + devId);
            } else {
                System.out.println("取消订阅失败:" + devId + ":" + errorId);
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
        //如果是来电消息才需要弹出来电页面
        // Show incoming call page only if it's a call message
        if (isCallAlarm(alarmInfo.getEvent(), alarmInfo.getMsgType(), devId)) {
            Intent intent = new Intent(DevPushService.this, DevIncomingCallActivity.class);
            intent.putExtra("devId", devId);
            intent.putExtra("alarmTime", alarmInfo.getStartTime());
            intent.putExtra("alarm_msg_type", alarmInfo.getMsgType());
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        Intent intent = new Intent(this, SplashScreen.class);
        intent.putExtra("devId",devId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "default_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_) // Replace with your own icon
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.received_alarm_message) +
                                alarmInfo.getDevName()+ " " +  XMPushManager.getAlarmName(getApplicationContext(), alarmInfo.getEvent()) + ":" +
                                alarmInfo.getStartTime() )
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        int notificationId = (int) System.currentTimeMillis(); // Unique ID for each notification
        notificationManager.notify(notificationId, notificationBuilder.build());

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("servie" , "Service is end  ondestroy ");
        if (handler != null && runnable != null) {
            Log.d("servie" , "Service is end  ondestroy ");
            handler.removeCallbacks(runnable);
        }
    }
}