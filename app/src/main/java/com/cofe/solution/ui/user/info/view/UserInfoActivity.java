package com.cofe.solution.ui.user.info.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basic.G;
import com.cofe.solution.base.SharedPreference;
import com.cofe.solution.ui.device.add.list.view.DevListActivity;
import com.lib.MsgContent;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.manager.account.BaseAccountManager;
import com.manager.account.XMAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.device.DeviceManager;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.widget.XTitleBar;

import com.cofe.solution.R;
import com.cofe.solution.app.SDKDemoApplication;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.device.push.view.DevPushService;
import com.cofe.solution.ui.user.info.listener.UserInfoContract;
import com.cofe.solution.ui.user.info.presenter.UserInfoPresenter;
import com.cofe.solution.ui.user.login.view.UserLoginActivity;
import com.cofe.solution.utils.SPUtil;
import io.reactivex.annotations.Nullable;

/**
 * 用户信息界面,包括ID,用户名,邮箱,手机,性别,注册时间
 * User information interface, including ID, username, email, mobile phone, gender, registration time
 * Created by jiangping on 2018-10-23.
 */
public class UserInfoActivity extends DemoBaseActivity<UserInfoPresenter> implements View.OnClickListener, UserInfoContract.IUserInfoView {
    private TextView tvUserId;

    private TextView tvUserName;
    private TextView tvUserEmail;

    private TextView tvUserPhone;
    RelativeLayout passwordRl,emailRl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_account_information);
        initView();
        initData();
    }

    private void initView() {
        tvUserId = findViewById(R.id.account_id_txtv);
        tvUserName = findViewById(R.id.nick_nametxtv);

        tvUserEmail = findViewById(R.id.email_txtv);
        tvUserPhone = findViewById(R.id.phone_txtv);
        passwordRl = findViewById(R.id.pass_rl);
        emailRl = findViewById(R.id.email_rl);

        passwordRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPasswordManagementSettings(passwordRl);
            }
        });
        emailRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        RelativeLayout btnLogout = findViewById(R.id.logout_rl);
        btnLogout.setOnClickListener(this);
    }

    private void initData() {
        if (DevDataCenter.getInstance().isLoginByAccount()) {
            loaderDialog.setMessage();
            //findViewById(R.id.layout_user_Info).setVisibility(View.VISIBLE);
            tryToGetUserInfo();
        }
    }

    private void tryToGetUserInfo() {
        loaderDialog.setMessage();
        if (!presenter.getInfo()) {
            showToast(getString(R.string.user_info_not_login), Toast.LENGTH_LONG);
            finish();
            Intent intent = new Intent();
            intent.setClass(this, UserLoginActivity.class);
            startActivity(intent);
        }
    }

    private void tryToLogout() {
        XMAccountManager.getInstance().logout();
        if (XMAccountManager.getInstance().getUserName() == null) {

            SharedPreference cookies = new SharedPreference(getApplicationContext());
            cookies.saveLoginStatus(1);

            Intent intent = new Intent(UserInfoActivity.this, UserLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }
    }

    /*获取用户信息并更新数据至界面*/
    @Override
    public void onUpdateView() {
        loaderDialog.dismiss();
        if (presenter != null) {
            tvUserId.setText(presenter.getUserId());
            tvUserName.setText(presenter.getUserName());

            if (SPUtil.isEmpty(presenter.getEmail())) {
                //findViewById(R.id.layoutUserEmail).setVisibility(View.GONE);
            } else {
                tvUserEmail.setText(presenter.getEmail());
            }
            if (SPUtil.isEmpty(presenter.getPhoneNo())) {
                //findViewById(R.id.layoutUserPhone).setVisibility(View.GONE);
            } else {
                tvUserPhone.setText(presenter.getPhoneNo());
            }
        } else {
            showToast(getString(R.string.failed_rertive_profile_data), Toast.LENGTH_LONG);
            finish();
        }
    }

    @Override
    public UserInfoPresenter getPresenter() {
        return new UserInfoPresenter(this);
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.logout_rl) {
            loaderDialog.setMessage();
            tryToLogout();
        }
    }

    private void openPasswordManagementSettings(View anchorView) {
        // Inflate the custom popup layout
        View popupView = LayoutInflater.from(anchorView.getContext())
                .inflate(R.layout.edit_device_name, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                (int) (anchorView.getContext().getResources().getDisplayMetrics().widthPixels * 0.8), // 80% of screen width
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        popupWindow.setFocusable(true);

        setDimBackground((Activity) anchorView.getContext(), 0.5f);

        popupWindow.setOnDismissListener(() -> setDimBackground((Activity) anchorView.getContext(), 1f));

        View rootView = ((Activity) anchorView.getContext()).getWindow().getDecorView().getRootView();
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
        TextView label = popupView.findViewById(R.id.popup_label);

        label.setText(anchorView.getContext().getString(R.string.enter_password));
        EditText editText = popupView.findViewById(R.id.popup_edittext);
        EditText oldPwdTextView = popupView.findViewById(R.id.popup_oldPwdText);
        Button button = popupView.findViewById(R.id.popup_button);

        // Handle button click
        button.setOnClickListener(v -> {
            String userInput = editText.getText().toString().trim();
            String oldPwdText = oldPwdTextView.getText().toString().trim();
            loaderDialog.show();
            //if (!oldPwdText.isEmpty() ) {
            if (!userInput.isEmpty()) {
                XMAccountManager.getInstance().modifyPwd(presenter.getUserName(), oldPwdText, userInput, new BaseAccountManager.OnAccountManagerListener() {
                    @Override
                    public void onSuccess(int msgId) {
                        loaderDialog.dismiss();
                        popupWindow.dismiss();
                        showToast(getString(R.string.password_modified_successfully), Toast.LENGTH_LONG);
                        tryToLogout();
                    }

                    @Override
                    public void onFailed(int msgId, int errorId) {
                        loaderDialog.dismiss();
                        popupWindow.dismiss();
                        showToast(getString(R.string.password_modified_failed), Toast.LENGTH_LONG);

                    }

                    @Override
                    public void onFunSDKResult(Message message, MsgContent msgContent) {


                    }
                });

            } else {
                Toast.makeText(anchorView.getContext(), "Please enter password", Toast.LENGTH_SHORT).show();
            }
            popupWindow.showAsDropDown(anchorView, 0, 0);


        });
    }
    private void setDimBackground(Activity activity, float dimAmount) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.alpha = dimAmount; // Set dim level (0.0f to 1.0f)
        window.setAttributes(layoutParams);
    }

}