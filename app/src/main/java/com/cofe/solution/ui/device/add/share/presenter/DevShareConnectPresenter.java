package com.cofe.solution.ui.device.add.share.presenter;


import com.alibaba.fastjson.JSON;
import com.cofe.solution.R;
import com.cofe.solution.ui.device.add.share.listener.DevShareConnectContract;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.share.DevShareQrCodeInfo;
import com.lib.sdk.bean.share.SearchUserInfoBean;
import com.manager.account.share.ShareInfo;
import com.manager.account.share.ShareManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;

import com.utils.AESECBUtils;
import com.utils.ParseUrlUtils;
import com.utils.XUtils;
import com.xm.activity.base.XMBasePresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


import static com.lib.EFUN_ATTR.LOGIN_USER_ID;
import static com.lib.sdk.bean.share.DevShareQrCodeInfo.APP_DOWNLOAD_URL;
import static com.lib.sdk.bean.share.DevShareQrCodeInfo.URL_KEY_SHARE_INFO;
import static com.manager.account.share.ShareManager.OPERATING.SHARE_DEV;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Log;

import org.checkerframework.checker.units.qual.A;

/**
 * 分享的设备界面,显示相关的列表菜单
 * The shared device interface displays the relevant list menu
 */
public class DevShareConnectPresenter extends XMBasePresenter<ShareManager> implements DevShareConnectContract.IDevShareConnectPresenter, ShareManager.OnShareManagerListener {

    private DevShareConnectContract.IDevShareConnectView iDevShareConnectView;
    private List<SearchUserInfoBean> userQueryBeans;
    String finalPermissions;
    ArrayList<String> permissionList = new ArrayList<>();
    public DevShareConnectPresenter(DevShareConnectContract.IDevShareConnectView iDevShareConnectView) {
        this.iDevShareConnectView = iDevShareConnectView;
        manager = ShareManager.getInstance(iDevShareConnectView.getContext());
        manager.init();
        manager.addShareManagerListener(this);
    }

    /**
     *         List<String> allPermissions = Arrays.asList(
     *                 "DP_ModifyConfig", "DP_ModifyPwd", "DP_CloudServer", "DP_Intercom",
     *                 "DP_PTZ", "DP_LocalStorage", "DP_ViewCloudVideo", "DP_DeleteCloudVideo",
     *                 "DP_AlarmPush", "DP_DeleteAlarmInfo"
     *         );
     *
     *         {
     *         "DP_ModifyConfig":1,
     *         "DP_ModifyPwd":1,"DP_ViewCloudVideo":1,
     *         "DP_DeleteCloudVideo":1,"DP_LocalStorage":1,"DP_CloudServer":1,
     *         "DP_AlarmPush":1,
     *         "DP_Intercom":1,"DP_PTZ":1,
     *         "DP_DeleteAlarmInfo":1
     *         }
     * @param permissionList
     */
    public void setPermissionList(ArrayList<String> permissionList){
        this.permissionList = permissionList;

        Map<String, String> keywordToPermissionMap = new HashMap<>();
        keywordToPermissionMap.put("Change Device Config", "DP_ModifyConfig");
        keywordToPermissionMap.put("Cloud Video", "DP_ViewCloudVideo");
        keywordToPermissionMap.put("Intercom", "DP_Intercom");
        keywordToPermissionMap.put("PTZ", "DP_PTZ");
        keywordToPermissionMap.put("Local Storage", "DP_LocalStorage");
        keywordToPermissionMap.put("Push", "DP_AlarmPush");

        Map<String, Integer> permissionMap = new HashMap<>();
        for (String key : keywordToPermissionMap.values()) {
            permissionMap.put(key, 0);
        }


        for (String item : permissionList) {
            if (keywordToPermissionMap.containsKey(item)) {
                String permissionKey = keywordToPermissionMap.get(item);
                permissionMap.put(permissionKey, 1);
            }
        }
        boolean cloudPermission =  false;
        boolean pushPermission =  false;
        boolean ConfigPermission =  false;
        for(String key : permissionMap.keySet()) {
            if (key.contains("DP_ViewCloudVideo")) {
                if (permissionMap.get(key) == 1) {
                    cloudPermission = true;
                }
            } else if (key.contains("DP_AlarmPush")) {
                if (permissionMap.get(key) == 1) {
                    pushPermission = true;
                }
            } else if (key.contains("DP_ModifyConfig")) {
                if (permissionMap.get(key) == 1) {
                    ConfigPermission = true;
                }
            }
        }
        if(ConfigPermission){
            permissionMap.put("DP_ModifyPwd",1);
        } else{
            permissionMap.put("DP_ModifyPwd",0);
        }

        if(pushPermission){
            permissionMap.put("DP_DeleteAlarmInfo",1);
        } else{
            permissionMap.put("DP_DeleteAlarmInfo",0);
        }

        if(cloudPermission){
            permissionMap.put("DP_DeleteCloudVideo",1);
            permissionMap.put("DP_CloudServer",1);
        } else{
            permissionMap.put("DP_DeleteCloudVideo",0);
            permissionMap.put("DP_CloudServer",0);
        }

        // Convert to JSON String
        finalPermissions = new org.json.JSONObject(permissionMap).toString();
        System.out.println("Final JSON: " +  finalPermissions );

    }


    @Override
    public void searchShareAccount(String account) {
        manager.userQuery(account);
    }

    @Override
    public void shareDevToOther(String accountId) {
        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(getDevId());
        if (xmDevInfo != null) {
            manager.shareDev(getDevId(), accountId, xmDevInfo.getDevUserName(), xmDevInfo.getDevPassword(), xmDevInfo.getDevType(), "");
        }
    }

    @Override
        public Bitmap getShareDevQrCode(Context context) {
            String loginUserId = FunSDK.GetFunStrAttr(LOGIN_USER_ID);
            String pwd = FunSDK.DevGetLocalPwd(getDevId());
            int devType = DevDataCenter.getInstance().getDevType(getDevId());
            DevShareQrCodeInfo devShareQrCodeInfo = new DevShareQrCodeInfo();
            devShareQrCodeInfo.setDevType(devType);//设备类型
            devShareQrCodeInfo.setUserId(loginUserId);//账号登录userId
            String loginName = FunSDK.DevGetLocalUserName(getDevId());//获取本地的设备登录密码
            devShareQrCodeInfo.setDevId(getDevId());//设置设备登录密码
            devShareQrCodeInfo.setPwd(pwd);//设置设备登录密码
            devShareQrCodeInfo.setLoginName(TextUtils.isEmpty(loginName) ? "admin" : loginName); //设备设备登录名
            devShareQrCodeInfo.setShareTimes(System.currentTimeMillis() / 1000);//设置分享时间（该时间是用来在扫描二维码添加的时候判断是否过期了，具体的过期时长可自定义，比如30分钟）
            String devToken = FunSDK.DevGetLocalEncToken(getDevId());//获取设备的登录Token（支持Token的设备才有）
            if (!TextUtils.isEmpty(devToken)) {
                devShareQrCodeInfo.setDevToken(devToken);//设置设备的登录Token
            }
            //String permissionValue =         "{\"DP_ModifyConfig\": 1,\"DP_ModifyPwd\": 1,\"DP_CloudServer\": 1,\"DP_Intercom\": 1,\"DP_PTZ\": 1,\"DP_LocalStorage\": 1,\"DP_ViewCloudVideo\": 1,\"DP_DeleteCloudVideo\": 1,\"DP_AlarmPush\": 1,\"DP_DeleteAlarmInfo\": 1}";
            devShareQrCodeInfo.setPermissions(finalPermissions);
            /**
             * 如果要设置访问权限，可以调用以下方法
             * devShareQrCodeInfo.setPermissions(“权限信息”)，这个权限信息自定义，比如
             * {
             * "DP_ModifyConfig": 0,//修改设备配置
             * "DP_ModifyPwd": 0,//修改设备密码 暂不提供修改
             * "DP_CloudServer": 0,//访问云服务 暂不提供修改
             * "DP_Intercom": 1,//对讲
             * "DP_PTZ": 1,//云台
             * "DP_LocalStorage": 1,//本地存储
             * "DP_ViewCloudVideo": 0,//查看云视频
             * "DP_DeleteCloudVideo": 0,//删除云视频 暂不提供修改
             * "DP_AlarmPush": 0,//推送（包括查看报警消息）
             * "DP_DeleteAlarmInfo": 0//删除报警消息（包括图片）暂不提供修改
             * }，该信息是透传的，生成二维码后扫描获取到该信息，直接解析就行；
             * */

            String info = JSON.toJSONString(devShareQrCodeInfo);//注意：该数据建议要加密处理后再生成二维码
            Log.d("share Generate QR code Share  ","QR code data "  + info);
            Log.d("share Generate QR code Share  ","QR code data devId"  + devShareQrCodeInfo.getDevId());

            Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_);//获取logo
            System.out.println("encInfo:" + info);
            Bitmap bitmap = null;
            try {
                bitmap = createQRCodeBitmap(info, logo, BarcodeFormat.QR_CODE, 600);//生成二维码
            } catch (WriterException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

    @Override
    public void release() {
        if (manager != null) {
            manager.unInit();
        }
    }

    @Override
    public void onShareResult(ShareInfo shareInfo) {
        if (shareInfo == null) {
            return;
        }

        //查询到分享的账号
        if (shareInfo.getOperating() == ShareManager.OPERATING.SEARCH_USER) {
            if (shareInfo.isSuccess()) {
                //成功获取到 需要json解析获取到分享账号Id
                if (shareInfo.getResultJson() != null) {
                    userQueryBeans = JSON.parseArray(shareInfo.getResultJson(), SearchUserInfoBean.class);
                }

                if (iDevShareConnectView != null) {
                    if (userQueryBeans != null && !userQueryBeans.isEmpty()) {
                        iDevShareConnectView.onSearchShareAccountResult(true, userQueryBeans.get(0));
                    } else {
                        iDevShareConnectView.onSearchShareAccountResult(true, null);
                    }
                }
            } else {
                //获取失败
                if (iDevShareConnectView != null) {
                    iDevShareConnectView.onSearchShareAccountResult(false, null);
                }
            }
        } else if (shareInfo.getOperating() == SHARE_DEV) {
            if (iDevShareConnectView != null) {
                iDevShareConnectView.onShareDevResult(shareInfo.isSuccess());
            }
        }
    }

    @Override
    protected ShareManager getManager() {
        return manager;
    }

    /**
     * 创建二维码
     * @param text
     * @param logo
     * @param format
     * @param qrSize
     * @return
     * @throws WriterException
     */
    private Bitmap createQRCodeBitmap(String text, Bitmap logo, BarcodeFormat format, int qrSize) throws WriterException {
        Matrix matrix = new Matrix();
        float sx = 80.0F / (float)logo.getWidth();
        float sy = 80.0F / (float)logo.getHeight();
        matrix.setScale(sx, sy);
        logo = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, false);
        MultiFormatWriter wirter = new MultiFormatWriter();
        Hashtable<EncodeHintType, Object> hintTypes = new Hashtable();
        hintTypes.put(EncodeHintType.CHARACTER_SET, CharacterSetECI.UTF8);
        hintTypes.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        BitMatrix bitMatrix = wirter.encode(text, format, qrSize, qrSize, hintTypes);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int halfW = width / 2;
        int halfH = height / 2;
        int[] pixels = new int[width * height];

        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                if (x > halfW - 40 && x < halfW + 40 && y > halfH - 40 && y < halfH + 40) {
                    pixels[y * width + x] = logo.getPixel(x - halfW + 40, y - halfH + 40);
                } else if (bitMatrix.get(x, y)) {
                    pixels[y * width + x] = -16777216;
                } else {
                    pixels[y * width + x] = -1;
                }
            }
        }

        logo = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        logo.setPixels(pixels, 0, width, 0, 0, width, height);
        return logo;
    }
}
