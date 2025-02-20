package com.cofe.solution.ui.activity;

import static com.manager.account.share.ShareInfo.SHARE_ACCEPT;
import static com.manager.account.share.ShareInfo.SHARE_NOT_YET_ACCEPT;
import static com.manager.account.share.ShareInfo.SHARE_REJECT;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.adapter.ItemSetAdapter;
import com.cofe.solution.ui.adapter.SharedUserAdapter;
import com.cofe.solution.ui.device.add.share.listener.DevShareAccountListContract;
import com.cofe.solution.ui.device.add.share.presenter.DevShareAccountListPresenter;
import com.cofe.solution.ui.dialog.LoaderDialog;
import com.lib.sdk.bean.share.MyShareUserInfoBean;
import com.lib.sdk.bean.share.Permission;
import com.manager.account.share.ShareManager;

import java.util.ArrayList;
import java.util.List;

public class MySharedUserActivity extends DemoBaseActivity<DevShareAccountListPresenter> implements ItemSetAdapter.OnItemSetClickListener, DevShareAccountListContract.IDevShareAccountListView {
    TextView tvTitleHeader;
    ImageView back_button;
    RecyclerView rvSharedUser;
    String devId;

    LoaderDialog loaderDialog;

    private List<MyShareUserInfoBean> myShareUserInfoBeans;

    private SharedUserAdapter sharedUserAdapter;

    @Override
    public DevShareAccountListPresenter getPresenter() {
        return new DevShareAccountListPresenter(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_shared_user);

        initUis();
    }

    private void initUis()
    {
        loaderDialog = new LoaderDialog(this);
        loaderDialog.setMessage(getString(R.string.please_wait));

        Intent intent = getIntent();
        if (intent != null) {
            // Retrieve the string extra using the same key
            devId = intent.getStringExtra("dev_id");
            if (devId != null) {
                ShareManager.getInstance(this).getMyShareDevUserList(devId);
            }
        }

        tvTitleHeader=findViewById(R.id.toolbar_title);
        back_button=findViewById(R.id.back_button);
        rvSharedUser=findViewById(R.id.rvSharedUser);

        tvTitleHeader.setText(getString(R.string.shared_users));


        rvSharedUser.setLayoutManager(new LinearLayoutManager(this));
        sharedUserAdapter=new SharedUserAdapter(this, new SharedUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String shareId, List<Permission> permissions) {

                Log.d("Permission",permissions.get(0).getLabel());
                Log.d("Permission", String.valueOf(permissions.get(0).isEnabled()));
                Log.d("Permission",permissions.toString());
                Log.d("Permission", String.valueOf(permissions.size()));
                showActionDialog(shareId,permissions);
            }
        });
        rvSharedUser.setAdapter(sharedUserAdapter);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void showActionDialog(String shareId, List<Permission> permissions) {
        // Create and configure the dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_action);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // Find views in the custom layout

        ImageView ivCancel = dialog.findViewById(R.id.ivCancel);
        Button btnCancelSharing = dialog.findViewById(R.id.btnCancelSharing);
        Button btnChangePermission = dialog.findViewById(R.id.btnChangePermission);

        // Set up click listeners
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();

            }
        });
        btnChangePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> permissionStrings = new ArrayList<>();

                for (Permission permission : permissions) {
                    // Convert to a string format: "name:isGranted"
                    permissionStrings.add(permission.getLabel() + ":" + permission.isEnabled());
                }

                Log.d("Permission", permissionStrings.get(0));
                Log.d("Permission", String.valueOf(permissionStrings.size()));
                dialog.dismiss();
                Intent intent=new Intent(MySharedUserActivity.this,ShareUserChangePermissionActivity.class);
                intent.putStringArrayListExtra("permissions_list",permissionStrings);
                intent.putExtra("share_id",shareId);
                startActivity(intent);
            }
        });
        btnCancelSharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loaderDialog.show();
                presenter.cancelShare(shareId);
                dialog.dismiss();
            }
        });

        // Adjust the dialog width and apply margins
        if (dialog.getWindow() != null) {
            int margin = (int) (20 * getResources().getDisplayMetrics().density); // Convert 20dp to pixels
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = getResources().getDisplayMetrics().widthPixels - (2 * margin);
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        // Show the dialog
        dialog.show();
    }

    @Override
    public void onItem(int position) {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onSearchMyShareUsersResult(List<MyShareUserInfoBean> myShareUserInfoBeans) {
        loaderDialog.dismiss();
        if (myShareUserInfoBeans != null) {
            this.myShareUserInfoBeans = myShareUserInfoBeans;
            List<String> data = new ArrayList<>();
            for (int i = 0; i < myShareUserInfoBeans.size(); ++i) {
                MyShareUserInfoBean myShareUserInfoBean = myShareUserInfoBeans.get(i);
                if (myShareUserInfoBean != null) {
                    int shareState = myShareUserInfoBean.getShareState();
                    String strShareState = "";
                    if (shareState == SHARE_ACCEPT) {
                        strShareState = getString(R.string.share_accept);
                    } else if (shareState == SHARE_NOT_YET_ACCEPT) {
                        strShareState = getString(R.string.share_not_yet_accept);
                    } else if (shareState == SHARE_REJECT) {
                        strShareState = getString(R.string.share_reject);
                    }

                    data.add(myShareUserInfoBean.getAccount() + "[" + strShareState + "]");
                }
            }

            sharedUserAdapter.setData(myShareUserInfoBeans);
        } else {
            // muSharedDeviceAdapter.setData(null);
            //ToastUtils.showLong(R.string.not_found);
        }
    }

    @Override
    public void onCancelShareResult(boolean isSuccess) {
        loaderDialog.dismiss();
        ToastUtils.showLong(isSuccess ? getString(R.string.cancel_share_s) : getString(R.string.cancel_share_f));
        if (isSuccess) {
            ShareManager.getInstance(this).getMyShareDevUserList(devId);
        }
    }
}