package com.cofe.solution.ui.adapter;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.constant.DeviceConstant;
import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.share.OtherShareDevUserBean;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.config.mqtt.DevStateNotifyMqttManager;
import com.manager.device.config.shadow.DevShadowManager;
import com.manager.device.config.shadow.OnDevShadowManagerListener;
import com.manager.device.config.shadow.ShadowConfigEnum;
import com.utils.LogUtils;
import com.xm.ui.widget.ListSelectItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cofe.solution.R;

import static com.constant.SDKLogConstant.APP_WEB_SOCKET;
import static com.manager.account.share.ShareInfo.SHARE_ACCEPT;
import static com.manager.account.share.ShareInfo.SHARE_NOT_YET_ACCEPT;
import static com.manager.account.share.ShareInfo.SHARE_REJECT;

/**
 * Created by hws on 2018-10-10.
 */

public class DevListAdapter extends RecyclerView.Adapter<DevListAdapter.ViewHolder> implements
        DevStateNotifyMqttManager.OnDevStateNotifyListener {
    private RecyclerView recyclerView;
    private List<HashMap<String, Object>> data;
    private HashMap<String, Bundle> isSupportInterDevLink = new HashMap<>();//缓存是否支持设备之间联动
    private DevStateNotifyMqttManager devStateNotifyManager;
    public static final String[] DEV_STATE = new String[]{
            FunSDK.TS("Offline"),//离线
            FunSDK.TS("Online"),//在线
            FunSDK.TS("Sleep"),//休眠
            FunSDK.TS("WakeUp"),//唤醒中
            FunSDK.TS("Wake"),//已唤醒
            FunSDK.TS("Not awakened"),//不可唤醒
            FunSDK.TS("Ready to sleep")//准备休眠中
    };

    public DevListAdapter(Application application, RecyclerView recyclerView, ArrayList<HashMap<String, Object>> data, OnItemDevClickListener ls) {
        this.recyclerView = recyclerView;
        this.data = data;
        this.onItemDevClickListener = ls;
        devStateNotifyManager = DevStateNotifyMqttManager.getInstance(application);
        devStateNotifyManager.addNotifyListener(this);
        devStateNotifyManager.connectMqtt();//连接MQTT
        DevShadowManager.getInstance().addDevShadowListener(onDevShadowManagerListener);
    }


    /**
     * 影子服务配置
     */
    private OnDevShadowManagerListener onDevShadowManagerListener = new OnDevShadowManagerListener() {
        @Override
        public void onDevShadowConfigResult(String devId, String configData, int errorId) {
            if (errorId >= 0 && configData != null) {
                //解析影子服务器返回的数据，如果有NetWork.LANLinkageBindInfo返回，表示支持联动
                HashMap hashMap = JSON.parseObject(configData, HashMap.class);
                JSONObject jsonObject = (JSONObject) hashMap.get("data");
                if (jsonObject != null && jsonObject.containsKey(ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName())) {
                    jsonObject = jsonObject.getJSONObject(ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
                    Integer otherDevBindAttr = 0;
                    String linkSn = null;
                    String linkPin = null;
                    boolean isBind = false;
                    if (jsonObject != null) {
                        if (jsonObject.containsKey("BindAttr")) {
                            //自身绑定属性
                            //0：无  1：主设备（摇头机）2：从设备（门锁）
                            otherDevBindAttr = jsonObject.getInteger("BindAttr");
                        }

                        if (jsonObject.containsKey("BindList")) {
                            //绑定设备列表
                            JSONArray jsonArray = jsonObject.getJSONArray("BindList");
                            if (jsonArray != null && jsonArray.size() > 0) {
                                jsonObject = jsonArray.getJSONObject(0);
                                if (jsonObject != null && jsonObject.containsKey("SN")) {
                                    linkSn = jsonObject.getString("SN");//发给摇头机时：门锁序列号前十二位 发给门锁时：摇头机序列号前十二位
                                    linkPin = jsonObject.getString("PIN");//六位PIN码

                                    //如果linkSn不是NoBound就表示已经被关联了
                                    if (!StringUtils.isStringNULL(linkSn) && !StringUtils.contrast(linkSn, "NoBound")) {
                                        isBind = true;
                                    }
                                }
                            }
                        }

                        Bundle bundle = new Bundle();
                        bundle.putInt("bindAttr", otherDevBindAttr);
                        bundle.putString("bindSn", linkSn);
                        bundle.putString("pin", linkPin);
                        bundle.putBoolean("isBind", isBind);
                        isSupportInterDevLink.put(devId, bundle);
                        if (recyclerView != null) {
                            Button btnInterDevLinkage = recyclerView.findViewWithTag("inter_dev_linkage:" + devId);
                            if (btnInterDevLinkage != null) {
                                btnInterDevLinkage.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }

        }

        @Override
        public void onSetDevShadowConfigResult(String devId, boolean isSuccess, int errorId) {
        }

        @Override
        public void onLinkShadow(String s, int i) {

        }

        @Override
        public void onUnLinkShadow(String s, int i) {

        }

        @Override
        public void onLinkShadowDisconnect() {

        }
    };

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_dev_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String devId = (String) data.get(position).get("devId");
        String devName = (String) data.get(position).get("devName");
        holder.lsiDevInfo.setTip(devId);
        holder.lsiDevInfo.setRightText(DEV_STATE[(int) data.get(position).get("devState")]);

        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(devId);
        String strShareState = "";
        if (xmDevInfo != null) {
            if (xmDevInfo.isShareDev()) {
                Context context = holder.lsiDevInfo.getContext();
                strShareState = context.getString(R.string.from_share_dev);
                OtherShareDevUserBean otherShareDevUserBean = xmDevInfo.getOtherShareDevUserBean();
                if (otherShareDevUserBean != null) {
                    int iShareState = otherShareDevUserBean.getShareState();
                    if (iShareState == SHARE_ACCEPT) {
                        strShareState = strShareState + "[" + context.getString(R.string.share_accept) + "]";
                    } else if (iShareState == SHARE_NOT_YET_ACCEPT) {
                        strShareState = strShareState + "[" + context.getString(R.string.share_not_yet_accept) + "]";
                    } else if (iShareState == SHARE_REJECT) {
                        strShareState = strShareState + "[" + context.getString(R.string.share_reject) + "]";
                    }
                }
                holder.dSharedStatustxtv.setText("");
                //holder.cloudInfoLl.setVisibility(View.GONE);;
                holder.btnShareDev1.setVisibility(View.GONE);;
                holder.btnTurnToPushSet1.setVisibility(View.GONE);

            } else {
                holder.dSharedStatustxtv.setText("");
                holder.cloudInfoLl.setVisibility(View.VISIBLE);;
            }
        }
        holder.lsiDevInfo.setTitle(devName + " " + strShareState);

        holder.lsiDevInfo.setTitle(devName + " " + strShareState);
        holder.lsiDevInfo.setTag(devId + ":state");


        holder.lsiDevInfo.setTag(devId);
        holder.btnInterDevLinkage.setTag("inter_dev_linkage:" + devId);
        //如果是来自分享的设备，需要隐藏分享管理

        //the device from shared,need hide the share management
        if (!DevDataCenter.getInstance().isLoginByAccount() || (xmDevInfo != null && xmDevInfo.isShareDev())) {
            holder.btnTurnToShareManage.setVisibility(View.VISIBLE);
        } else {
            holder.btnTurnToShareManage.setVisibility(View.GONE);
        }

        // for offline
        if (xmDevInfo != null && xmDevInfo.getDevState() == 0) {
            holder.btnTurnToPushSet1.setVisibility(View.GONE);
        }

        //如果有缓存的话，直接判断，没有的话需要从影子服务器获取
        if (isSupportInterDevLink.containsKey(devId)) {
            holder.btnInterDevLinkage.setVisibility(View.VISIBLE);
        } else {
            holder.btnInterDevLinkage.setVisibility(View.GONE);
            DevShadowManager.getInstance().getDevCfgsFromShadowService(devId, ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
        }

        if (DevDataCenter.getInstance().isLowPowerDev(devId)) {
            holder.btnWakUpMaster.setVisibility(View.VISIBLE);
        } else {
            holder.btnWakUpMaster.setVisibility(View.GONE);
        }

        holder.devNameTxtv.setText(devName);
        holder.status_txtv.setTag(DEV_STATE[(int) data.get(position).get("devState")]);
        holder.status_txtv.setText(DEV_STATE[(int) data.get(position).get("devState")]);

        holder.openPopup.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public void setData(ArrayList<HashMap<String, Object>> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void release() {
        if (devStateNotifyManager != null) {
            devStateNotifyManager.release();
        }

        DevShadowManager.getInstance().removeDevShadowListener(onDevShadowManagerListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ListSelectItem lsiDevInfo;
        Button btnTurnToAlarmMsg;//跳转到报警消息  jump to alarm message
        Button btnTurnToPushSet;//跳转到推送设置   jump to push Settings
        Button btnTurnToCloudService;//跳转到云服务 jump to the cloud service
        Button btnModifyDevName;//修改设备名称  change the device name
        Button btnTurnToShareManage;//跳转到分享管理  go to Share Management
        Button btnLocalDevUserPwd;//本地设备登录名和密码 local device login name and password
        Button btnWakUpMaster;//唤醒主控
        Button btnPingTest;//Ping
        Button btnSdPlayback;//SD卡录像回放 SD Playback
        Button btnInterDevLinkage;//门锁和其他摇头机之间的联动，该功能通过影子服务来判断是否支持 "The linkage between the door lock and other pan-tilt cameras is determined by the shadow service to ascertain its support."
        Button btnDevAbility;//设备能力集
        Button btnUpdateDevToken;//更新设备Token

        LinearLayout btnShareDev1;
        LinearLayout btnTurnToAlarmMsg1;
        LinearLayout btnTurnToPushSet1;
        LinearLayout settingIcon;
        LinearLayout cloudInfoLl;
        ImageView btnTurnToCloudService1;

        ImageView openAiSetting, bannerImg;

        ImageView openPopup;
        TextView status_txtv;
        TextView dSharedStatustxtv;
        TextView devNameTxtv;


        public ViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemDevClickListener != null) {
                        int index = getAdapterPosition();
                        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(index).get("devId"));
                        onItemDevClickListener.onItemClick(index, xmDevInfo);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onItemDevClickListener != null) {
                        int index = getAdapterPosition();
                        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(index).get("devId"));
                        return onItemDevClickListener.onLongItemClick(index, xmDevInfo);
                    }
                    return false;
                }
            });
            XMDevInfo xmDevInfo = null;
            if(getAdapterPosition()!=-1){
                xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
            }
            lsiDevInfo = itemView.findViewById(R.id.lsi_dev_info);
            btnTurnToAlarmMsg = itemView.findViewById(R.id.btn_turn_to_alarm_msg);
            XMDevInfo finalXmDevInfo2 = xmDevInfo;
            btnTurnToAlarmMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDevClickListener != null) {
                        int index = getAdapterPosition();
                        onItemDevClickListener.onTurnToAlarmMsg(index, finalXmDevInfo2);
                    }
                }
            });

            //该功能只有JF账号登录后才会显示
            btnTurnToAlarmMsg.setVisibility(DevDataCenter.getInstance().isLoginByAccount() ? View.VISIBLE : View.GONE);

            btnTurnToPushSet = itemView.findViewById(R.id.btn_turn_to_push_set);
            XMDevInfo finalXmDevInfo = xmDevInfo;
            btnTurnToPushSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onTurnToPushSet(getAdapterPosition(), finalXmDevInfo);
                    }
                }
            });

            btnTurnToPushSet.setVisibility(DevDataCenter.getInstance().isLoginByAccount() ? View.VISIBLE : View.GONE);

            btnTurnToCloudService = itemView.findViewById(R.id.btn_turn_to_cloud_service);
            XMDevInfo finalXmDevInfo3 = xmDevInfo;
            btnTurnToCloudService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onTurnToCloudService(getAdapterPosition(), finalXmDevInfo3);
                    }
                }
            });

            btnTurnToCloudService.setVisibility(DevDataCenter.getInstance().isLoginByAccount() ? View.VISIBLE : View.GONE);
            btnModifyDevName = itemView.findViewById(R.id.btn_modify_dev_name);
            btnModifyDevName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onModifyDevName(getAdapterPosition(), xmDevInfo);
                    }
                }
            });

            btnTurnToShareManage = itemView.findViewById(R.id.btn_share_dev);
            btnTurnToShareManage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onShareDevManage(getAdapterPosition(), xmDevInfo);
                    }
                }
            });
            btnTurnToShareManage.setVisibility(DevDataCenter.getInstance().isLoginByAccount() ? View.VISIBLE : View.GONE);

            btnLocalDevUserPwd = itemView.findViewById(R.id.btn_edit_user_password);
            btnLocalDevUserPwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onTurnToEditLocalDevUserPwd(getAdapterPosition(), xmDevInfo);
                    }
                }
            });

            //唤醒设备（包括主控)

            btnWakUpMaster = itemView.findViewById(R.id.btn_wake_up_master);
            btnWakUpMaster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onWakeUpDev(getAdapterPosition(), xmDevInfo);
                    }
                }
            });


            //SD卡录像回放
            btnSdPlayback = itemView.findViewById(R.id.btn_sd_playback);
            btnSdPlayback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onTurnToSdPlayback(getAdapterPosition(), xmDevInfo);
                    }
                }
            });

            //设备之间联动
            btnInterDevLinkage = itemView.findViewById(R.id.btn_inter_device_linkage);
            btnInterDevLinkage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    Bundle bundle = isSupportInterDevLink.get(xmDevInfo.getDevId());
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onTurnToInterDevLinkage(getAdapterPosition(), xmDevInfo, bundle);
                    }
                }
            });

            //Ping

            btnPingTest = itemView.findViewById(R.id.btn_ping);
            btnPingTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onPingTest(getAdapterPosition(), xmDevInfo);
                    }
                }
            });

            //Device Ability
            btnDevAbility = itemView.findViewById(R.id.btn_dev_ability);
            btnDevAbility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onTurnToDevAbility(getAdapterPosition(), xmDevInfo);
                    }
                }
            });

            //从服务器端获取设备最新Token
            //Obtain the latest device token from the server
            btnUpdateDevToken = itemView.findViewById(R.id.btn_update_dev_token);
            btnUpdateDevToken.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onToGetDevTokenFromServer(getAdapterPosition(), xmDevInfo);
                    }
                }
            });

            //该功能只有JF账号登录后才会显示
            btnUpdateDevToken.setVisibility(DevDataCenter.getInstance().isLoginByAccount() ? View.VISIBLE : View.GONE);

             btnShareDev1   = itemView.findViewById(R.id.btn_share_dev1);
             btnTurnToAlarmMsg1  = itemView.findViewById(R.id.btn_turn_to_alarm_msg1); ;
             btnTurnToPushSet1  = itemView.findViewById(R.id.btn_turn_to_push_set1); ;
             btnTurnToCloudService1  = itemView.findViewById(R.id.btn_turn_to_cloud_service1);;
             cloudInfoLl  = itemView.findViewById(R.id.cloud_info_ll);;
             settingIcon  = itemView.findViewById(R.id.setting_icon);
             openAiSetting  = itemView.findViewById(R.id.open_ai_setting);;
             openPopup  = itemView.findViewById(R.id.open_popup);;
             status_txtv  = itemView.findViewById(R.id.status_txtv);;
             dSharedStatustxtv  = itemView.findViewById(R.id.is_shared_device_txtv);;
             devNameTxtv  = itemView.findViewById(R.id.dev_name);;
             bannerImg  = itemView.findViewById(R.id.banner_img);;

            XMDevInfo finalXmDevInfo1 = xmDevInfo;
            btnTurnToPushSet1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onTurnToPushSet(getAdapterPosition(), finalXmDevInfo1);
                    }
                }
            });
            XMDevInfo finalXmDevInfo4 = xmDevInfo;
            btnTurnToAlarmMsg1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onTurnToAlarmMsg(getAdapterPosition(), finalXmDevInfo4);
                    }
                }
            });
            settingIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    onItemDevClickListener.openSettingActivity(getAdapterPosition(),xmDevInfo );
                }
            });
            XMDevInfo finalXmDevInfo5 = xmDevInfo;
            btnTurnToCloudService1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onTurnToCloudService(getAdapterPosition(), finalXmDevInfo5);
                    }
                }
            });

            btnShareDev1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onShareDevManage(getAdapterPosition(), xmDevInfo);
                    }
                }
            });
            openPopup.setVisibility(View.VISIBLE);

            openPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    /*if (onItemDevClickListener != null) {
                        onItemDevClickListener.onShareDevManage(getAdapterPosition(), xmDevInfo);
                    }*/
                    //showPopupMenu(view.getContext(), view, getAdapterPosition(), xmDevInfo);

                    if(xmDevInfo.getDevState()!=0) {
                        showPopupMenu(view.getContext(), view, getAdapterPosition(), xmDevInfo);
                    } else{
                        Toast.makeText(openPopup.getContext(), openPopup.getContext().getString(R.string.dev_offline), Toast.LENGTH_LONG).show();
                    }
                }
            });

            openPopup.setVisibility(View.VISIBLE);

        }
    }

    /**
     * WeBSocket 已经连接
     */
    @Override
    public void onConnected() {
        //订阅MQTT 传入设备列表
        devStateNotifyManager.subscribeDevIdsByMqtt(DevDataCenter.getInstance().getDevList());
    }

    /**
     * WeBSocket 已经断开
     */
    @Override
    public void onDisconnected() {

    }

    /**
     * WeBSocket 已经重连成功
     */
    @Override
    public void onReconnected() {
        //订阅MQTT 传入设备列表
        devStateNotifyManager.subscribeDevIdsByMqtt(DevDataCenter.getInstance().getDevList());
    }

    /**
     * WeBSocket 正在连接中
     */
    @Override
    public void onReconnecting() {

    }

    /**
     * 设备错误状态上报
     */
    @Override
    public void onDevErrorState(String devId, String errorId, Object errorMsg, JSONObject originalData) {

    }

    /**
     * 设备功能状态上报
     */
    @Override
    public void onDevFunState(String devId, String funId, Object value, JSONObject originalData) {

    }

    /**
     * 设备休眠状态上报
     */
    @Override
    public void onDevSleepState(String devId, Object value, JSONObject originalData) {
        LogUtils.debugInfo(APP_WEB_SOCKET, originalData.toJSONString());
        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(devId);
        if (xmDevInfo == null) {
            return;
        }

        if (DeviceConstant.DevStateEnum.PREPARE_SLEEP.getDevState().equals(value)) {//准备休眠中
            xmDevInfo.setDevState(XMDevInfo.PREPARE_SLEEP);
        } else if (DeviceConstant.DevStateEnum.LIGHT_SLEEP.getDevState().equals(value)) {//浅度休眠
            xmDevInfo.setDevState(XMDevInfo.SLEEP);
        } else if (DeviceConstant.DevStateEnum.DEEP_SLEEP.getDevState().equals(value)) {//深度休眠
            xmDevInfo.setDevState(XMDevInfo.SLEEP_UNWAKE);
        } else if (DeviceConstant.DevStateEnum.WAKE_UP.getDevState().equals(value)) {//已唤醒
            xmDevInfo.setDevState(XMDevInfo.WAKE_UP);
        }

        if (recyclerView != null) {
            ListSelectItem lsiDevInfo = recyclerView.findViewWithTag(devId + ":state");
            if (lsiDevInfo != null) {
                lsiDevInfo.setRightText(DEV_STATE[xmDevInfo.getDevState()]);
            }
        }
    }

    private OnItemDevClickListener onItemDevClickListener;

    public interface OnItemDevClickListener {
        void onItemClick(int position, XMDevInfo xmDevInfo);

        boolean onLongItemClick(int position, XMDevInfo xmDevInfo);

        void onTurnToAlarmMsg(int position,XMDevInfo xmDevInfo);

        void onTurnToCloudService(int position, XMDevInfo xmDevInfo);

        void onTurnToPushSet(int position, XMDevInfo xmDevInfo);

        void onModifyDevName(int position, XMDevInfo xmDevInfo);
        void openSettingActivity(int position, XMDevInfo xmDevInfo);

        /**
         * 分享管理
         * share management
         *
         * @param position
         * @param xmDevInfo
         */
        void onShareDevManage(int position, XMDevInfo xmDevInfo);

        /**
         * 编辑本地设备登录名和密码
         * edit the login name and password of the local device
         *
         * @param position
         * @param xmDevInfo
         */
        void onTurnToEditLocalDevUserPwd(int position, XMDevInfo xmDevInfo);

        /**
         * 唤醒设备(包括主控)
         *
         * @param position
         * @param xmDevInfo
         */
        void onWakeUpDev(int position, XMDevInfo xmDevInfo);

        /**
         * 跳转到SD卡录像回放页面
         *
         * @param position
         * @param xmDevInfo
         */
        void onTurnToSdPlayback(int position, XMDevInfo xmDevInfo);

        /**
         * Ping
         *
         * @param position
         * @param xmDevInfo
         */
        void onPingTest(int position, XMDevInfo xmDevInfo);

        void onTurnToInterDevLinkage(int position, XMDevInfo xmDevInfo, Bundle bundle);

        /**
         * 跳转到设备能力集
         *
         * @param position
         * @param xmDevInfo
         */
        void onTurnToDevAbility(int position, XMDevInfo xmDevInfo);

        /**
         * 从服务器获取设备Token
         *
         * @param position
         * @param xmDevInfo
         */
        void onToGetDevTokenFromServer(int position, XMDevInfo xmDevInfo);
    }

    private void showPopupMenu( Context context ,View view, int position, XMDevInfo xmDevInfo) {
        View popupView = LayoutInflater.from(view.getContext())
                .inflate(R.layout.custom_device_item_popup, null);

        // Create a PopupWindow
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int desiredWidth = (int) (view.getContext().getResources().getDisplayMetrics().widthPixels * 0.7); // 80% of screen width
        popupWindow.setWidth(desiredWidth); // Set the width explicitly


        // Set focusable so the popup will close when touched outside
        popupWindow.setFocusable(true);

        popupWindow.setOnDismissListener(() -> setDimBackground((Activity) view.getContext(), 1f));

        setDimBackground((Activity) view.getContext(), 0.5f);


        // Get the root view of the current activity
        View rootView = ((Activity) view.getContext()).getWindow().getDecorView().getRootView();

        // Calculate the center of the screen
        int screenWidth = rootView.getWidth();
        int screenHeight = rootView.getHeight();
        int xOff = (screenWidth - popupWindow.getWidth()) / 2;
        int yOff = (screenHeight - popupWindow.getHeight()) / 2;

        // Show the popup at the center of the screen
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);


        // Show the popup
        //popupWindow.showAsDropDown(view);

        // Handle button clicks
        TextView pTitleTxtv = popupView.findViewById(R.id.popup_title);
        TextView pidTxvV = popupView.findViewById(R.id.popup_id);
        TextView messageButton = popupView.findViewById(R.id.message_button);
        TextView shareButton = popupView.findViewById(R.id.share_button);
        TextView settingsButton = popupView.findViewById(R.id.settings_button);
        TextView cloudStorageButton = popupView.findViewById(R.id.cloud_storage_button);
        TextView deleteButton = popupView.findViewById(R.id.delete_button);

        pTitleTxtv.setText(xmDevInfo.getDevName());
        pidTxvV.setText(xmDevInfo.getDevId());
        pidTxvV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToCopy = pidTxvV.getText().toString();

                // Copy the text to the clipboard
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);
                clipboard.setPrimaryClip(clip);

                // Show a Toast message to confirm
                Toast.makeText(view.getContext(), "Text copied to clipboard!", Toast.LENGTH_SHORT).show();

            }
        });

        messageButton.setOnClickListener(v -> {
            popupWindow.dismiss();
            if (onItemDevClickListener != null) {
                onItemDevClickListener.onTurnToPushSet(position,xmDevInfo);
            }
        });

        shareButton.setOnClickListener(v -> {
            popupWindow.dismiss();
            if (onItemDevClickListener != null) {
                onItemDevClickListener.onShareDevManage(position,xmDevInfo);
            }
        });

        settingsButton.setOnClickListener(v -> {
            popupWindow.dismiss();
            if (onItemDevClickListener != null) {
                onItemDevClickListener.openSettingActivity(position,xmDevInfo);
            }
        });

        cloudStorageButton.setOnClickListener(v -> {
            Toast.makeText(view.getContext(), "Cloud Storage Clicked", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });

        deleteButton.setOnClickListener(v -> {
            popupWindow.dismiss();
            if (onItemDevClickListener != null) {
                onItemDevClickListener.onLongItemClick(position, xmDevInfo);
            }
        });
    }

    private void setDimBackground(Activity activity, float dimAmount) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.alpha = dimAmount; // Set dim level (0.0f to 1.0f)
        window.setAttributes(layoutParams);
    }

}
