package com.cofe.solution.base;

import static com.xm.activity.base.XMBasePresenter.LIFE_CYCLE.CREATE;
import static com.xm.activity.base.XMBasePresenter.LIFE_CYCLE.DESTROY;
import static com.xm.activity.base.XMBasePresenter.LIFE_CYCLE.START;
import static com.xm.activity.base.XMBasePresenter.LIFE_CYCLE.STOP;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cofe.solution.R;
import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.manager.XMFunSDKManager;
import com.manager.base.BaseManager;
import com.utils.StatusBarUtils;
import com.utils.XUtils;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.dialog.IAnimController;
import com.xm.ui.widget.dialog.LoadingDialog;
import com.xm.ui.widget.icseelogoview.animcontroller.ICSeeLogoAnimController;

import io.reactivex.annotations.Nullable;
import pl.droidsonroids.gif.GifImageView;

public abstract class XMBaseFragment<T extends XMBasePresenter> extends AppCompatActivity implements XTitleBar.OnLeftClickListener{

    protected T presenter;
    public abstract T getPresenter();
    private LoadingDialog waitDialog = null;
    protected int screenWidth;
    protected int screenHeight;
    private boolean isInit;
    /**
     *阿里组件化路由地址
     */
    public static String aRouterPath;
    /**
     * 是不是组件功能
     */
    protected boolean isModuleFunction;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        screenWidth = XUtils.getScreenWidth(this);
        screenHeight = XUtils.getScreenHeight(this);
        Intent intent = getIntent();
        String devId = intent.getStringExtra("devId");
        presenter = getPresenter();
        if (presenter == null) {
            class SimplePresenter extends XMBasePresenter {

                @Override
                protected BaseManager getManager() {
                    return null;
                }
            }
            presenter = (T) new SimplePresenter();
        }
        presenter.setDevId(devId);
        presenter.setLifeCycle(CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter.setLifeCycle(START);
        }

        if (!isInit) {
            if (XMFunSDKManager.getInstance().isXMStatusBar()) {
                initStatusBar();
            }

            try {
                ApplicationInfo appInfo = getPackageManager()
                        .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                if (appInfo.metaData != null
                        && appInfo.metaData.containsKey("Support_XM_Language")) {
                    boolean isSupport = appInfo.metaData.getBoolean("Support_XM_Language");
                    if (isSupport) {
                        initXMLanguage(getRootLayout());
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            isInit = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (presenter != null) {
            presenter.setLifeCycle(STOP);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (presenter != null) {
            presenter.setLifeCycle(STOP);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.setLifeCycle(DESTROY);
        }
    }

    @Override
    public void onLeftclick() {
        finish();
    }

    protected String translation(String content) {
        return FunSDK.TS(content);
    }

    public void showToast(String content, int duration) {
        Toast.makeText(this, content, duration).show();
    }

    public void showWaitDialog() {
        showWaitDialog(getString(R.string.waiting));
    }

    public void showWaitDialog(String content) {
        showWaitDialog(content,true);
    }

    public void showWaitDialog(String content,boolean isCancel) {
        if (waitDialog != null && waitDialog.isShowing()) {
            waitDialog.show(content);
            return;
        }

        try {
            IAnimController iAnimController = new ICSeeLogoAnimController();
            iAnimController.createAnim(new GifImageView(this));
            waitDialog = LoadingDialog.getInstance(this);
            waitDialog.setPromptTextColor(getResources().getColor(R.color.default_normal_text_color));
            waitDialog.embedAnimation(iAnimController);
            waitDialog.setCancelable(isCancel);
            waitDialog.show(content);
        }catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
    }

    public void hideWaitDialog() {
        if (waitDialog != null && presenter.getLifeCycle() != DESTROY) {
            waitDialog.dismiss();
        }
    }

    public void turnToActivity(Class _class) {
        Intent intent = new Intent(this,_class);
        if (presenter != null && !StringUtils.isStringNULL(presenter.getDevId())) {
            intent.putExtra("devId",presenter.getDevId());
        }
        startActivity(intent);
    }

    public void turnToActivity(Class _class,String key,Bundle data) {
        Intent intent = new Intent(this,_class);
        intent.putExtra(key,data);
        if (presenter != null && !StringUtils.isStringNULL(presenter.getDevId())) {
            intent.putExtra("devId",presenter.getDevId());
        }
        startActivity(intent);
    }

    public void turnToActivity(Class _class,Object[][] objects) {
        Intent intent = new Intent(this,_class);
        if (objects != null) {
            for (int i = 0; i < objects.length; ++i) {
                if (objects[i][1] instanceof Integer) {
                    intent.putExtra((String) objects[i][0],(Integer) objects[i][1]);
                }else if (objects[i][1] instanceof String) {
                    intent.putExtra((String) objects[i][0],(String) objects[i][1]);
                }else if (objects[i][1] instanceof Boolean) {
                    intent.putExtra((String) objects[i][0],(Boolean) objects[i][1]);
                }else if (objects[i][1] instanceof Double) {
                    intent.putExtra((String) objects[i][0],(Double) objects[i][1]);
                }else if (objects[i][1] instanceof Float) {
                    intent.putExtra((String) objects[i][0],(Float) objects[i][1]);
                }
            }
        }

        if (presenter != null && !StringUtils.isStringNULL(presenter.getDevId())) {
            intent.putExtra("devId",presenter.getDevId());
        }
        startActivity(intent);
    }

    /**
     * 沉浸式状态栏
     */
    private void initStatusBar() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //黑色文字
                StatusBarUtils.setLightStatusBar(this, true);
                //白色背景
                StatusBarUtils.transparentStatusBar(this);
            } else {
                //6.0以下默认白色文字
                StatusBarUtils.setStatusBarColor(this, R.color.black);
            }

            StatusBarUtils.setRootView(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化XM语言包
     * @param layout
     */
    private void initXMLanguage(ViewGroup layout) {
        if (layout == null) {
            return;
        }

        int count = layout.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = layout.getChildAt(i);
            if (v instanceof ViewGroup) {
                initXMLanguage((ViewGroup) v);
            } else {
                if (v instanceof TextView) {
                    if (((TextView) v).getText() != null) {
                        String text = FunSDK.TS(((TextView) v).getText().toString());
                        ((TextView) v).setText(text);
                    }

                    if (((TextView) v).getHint() != null) {
                        ((TextView) v).setHint(FunSDK.TS(((TextView) v).getHint().toString()));
                    }
                }
            }
        }
    }

    protected ViewGroup getRootLayout() {
        ViewGroup layout = (ViewGroup) this.findViewById(R.id.xm_root_layout);
        if (layout == null) {
            View v = this.getCurrentFocus();
            if (v != null) {
                ViewParent vp = v.getParent();
                if (vp == null) {
                    layout = (ViewGroup) v;
                } else {
                    layout = (ViewGroup) vp;
                    while ((vp = vp.getParent()) != null) {
                        layout = (ViewGroup) vp;
                    }
                }
            }
        }
        return layout;
    }

    protected ViewGroup getRootLayout(View base) {
        ViewGroup layout = (ViewGroup) base.findViewById(R.id.xm_root_layout);
        if (layout == null) {
            View v = this.getCurrentFocus();
            if (v != null) {
                ViewParent vp = v.getParent();
                if (vp == null) {
                    layout = (ViewGroup) v;
                } else {
                    layout = (ViewGroup) vp;
                    while ((vp = vp.getParent()) != null) {
                        layout = (ViewGroup) vp;
                    }
                }
            }
        }
        return layout;
    }

}
