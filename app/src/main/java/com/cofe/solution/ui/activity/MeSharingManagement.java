package com.cofe.solution.ui.activity;

import static com.manager.account.share.ShareInfo.SHARE_ACCEPT;
import static com.manager.account.share.ShareInfo.SHARE_NOT_YET_ACCEPT;
import static com.manager.account.share.ShareInfo.SHARE_REJECT;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.adapter.FromSharedDeviceAdapter;
import com.cofe.solution.ui.adapter.ItemSetAdapter;
import com.cofe.solution.ui.adapter.MuSharedDeviceAdapter;
import com.cofe.solution.ui.device.add.share.listener.DevShareAccountListContract;
import com.cofe.solution.ui.device.add.share.listener.ShareDevListContract;
import com.cofe.solution.ui.device.add.share.presenter.DevShareAccountListPresenter;
import com.cofe.solution.ui.device.add.share.presenter.ShareDevListPresenter;
import com.cofe.solution.ui.dialog.LoaderDialog;
import com.lib.sdk.bean.share.MyShareUserInfoBean;
import com.lib.sdk.bean.share.OtherShareDevUserBean;
import com.manager.XMFunSDKManager;
import com.manager.account.share.ShareManager;
import com.utils.TimeMillisUtil;

import java.util.ArrayList;
import java.util.List;

public class MeSharingManagement extends DemoBaseActivity<DevShareAccountListPresenter> implements ItemSetAdapter.OnItemSetClickListener, DevShareAccountListContract.IDevShareAccountListView, ShareDevListContract.IShareDevListView {
    TextView tvTitleHeader;
    TextView tvMySharing;
    TextView tvFromSharing;
    ImageView back_button;

    private List<MyShareUserInfoBean> myShareUserInfoBeans;

    List<String> data;

    LinearLayout llFromSharedDevice;
    LinearLayout llMySharing;
    RelativeLayout rlMySharings;
    RelativeLayout rlFromSharedDevice;

    RecyclerView rvMySharings;
    RecyclerView rvFromSharedDevice;
    TextView llFromSharedDeviceCount;
    TextView tvMySharingCount;

    private FromSharedDeviceAdapter fromSharedDeviceAdapter;
    private MuSharedDeviceAdapter muSharedDeviceAdapter;
    private List<OtherShareDevUserBean> otherShareDevUserBeans;


    // Presenter for "Other Share Dev User List"
    private ShareDevListPresenter shareDevListPresenter;
    LoaderDialog loaderDialog;


    @Override
    public DevShareAccountListPresenter getPresenter() {
        return new DevShareAccountListPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_me_sharing_management);



        initUis();
    }

    private void initUis() {
        loaderDialog = new LoaderDialog(this);
        loaderDialog.setMessage("Please wait...");
        // Initialize the second presenter
        shareDevListPresenter = new ShareDevListPresenter(this);
        //
        data = new ArrayList<>();
        myShareUserInfoBeans = new ArrayList<>();
        tvTitleHeader = findViewById(R.id.toolbar_title);
        tvTitleHeader.setText("Sharing Management");

        back_button = findViewById(R.id.back_button);
        llFromSharedDevice = findViewById(R.id.llFromSharedDevice);
        llMySharing = findViewById(R.id.llMySharing);
        rlMySharings = findViewById(R.id.rlMySharings);
        rlFromSharedDevice = findViewById(R.id.rlFromSharedDevice);
        rvMySharings = findViewById(R.id.rvMySharings);
        rvFromSharedDevice = findViewById(R.id.rvFromSharedDevice);
        tvMySharing = findViewById(R.id.tvMySharing);
        tvFromSharing = findViewById(R.id.tvFromSharing);
        llFromSharedDeviceCount = findViewById(R.id.llFromSharedDeviceCount);
        tvMySharingCount = findViewById(R.id.tvMySharingCount);

        rvMySharings.setLayoutManager(new LinearLayoutManager(this));
        muSharedDeviceAdapter = new MuSharedDeviceAdapter(MeSharingManagement.this, new MuSharedDeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String dev_id) {
                Intent intent = new Intent(MeSharingManagement.this, MySharedUserActivity.class);
                intent.putExtra("dev_id", dev_id);
                startActivity(intent);
            }
        });
        rvMySharings.setAdapter(muSharedDeviceAdapter);

        //String result=ShareManager.getInstance(this).getMyShareDevUserList(presenter.getDevId());
        //  Log.d("RES",result);
        //myShareUserInfoBeans=

        rvFromSharedDevice.setLayoutManager(new LinearLayoutManager(this));
        fromSharedDeviceAdapter = new FromSharedDeviceAdapter(MeSharingManagement.this, new FromSharedDeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String shareId, int position, List<OtherShareDevUserBean> data) {
                showCancelSharingDialog(shareId, position, data);
            }
        });
        rvFromSharedDevice.setAdapter(fromSharedDeviceAdapter);
        tvTitleHeader.setText("Sharing Management");

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rlFromSharedDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show llFromSharedDevice with slide-in animation from the left
                if (llFromSharedDevice.getVisibility() == View.GONE) {
                    llFromSharedDevice.setVisibility(View.VISIBLE);
                    animateSlideInFromLeft(llFromSharedDevice);
                }

                // Hide llMySharing with slide-out animation to the left
                if (llMySharing.getVisibility() == View.VISIBLE) {
                    animateSlideOutToLeft(llMySharing, () -> {
                        llMySharing.setVisibility(View.GONE);
                    });
                }

                // Update other UI properties
                tvMySharing.setTextColor(getResources().getColor(R.color.cover_gray));
                tvFromSharing.setTextColor(getResources().getColor(R.color.demo_title));
                rlFromSharedDevice.setBackgroundResource(R.drawable.shape_bg_round_white_5);
                rlMySharings.setBackground(null);
               /* tvMySharing.setTextColor(getResources().getColor(R.color.cover_gray));
                tvFromSharing.setTextColor(getResources().getColor(R.color.demo_title));
                llFromSharedDevice.setVisibility(View.VISIBLE);
                llMySharing.setVisibility(View.GONE);
                rlFromSharedDevice.setBackgroundResource(R.drawable.shape_bg_round_white_5);
                rlMySharings.setBackground(null);*/
            }
        });

        rlMySharings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// Show llMySharing with slide-in animation from the left
                if (llMySharing.getVisibility() == View.GONE) {
                    llMySharing.setVisibility(View.VISIBLE);
                    animateSlideInFromLeft(llMySharing);
                }

                // Hide llFromSharedDevice with slide-out animation to the left
                if (llFromSharedDevice.getVisibility() == View.VISIBLE) {
                    animateSlideOutToLeft(llFromSharedDevice, () -> {
                        llFromSharedDevice.setVisibility(View.GONE);
                    });
                }

                // Update other UI properties
                tvMySharing.setTextColor(getResources().getColor(R.color.demo_title));
                tvFromSharing.setTextColor(getResources().getColor(R.color.cover_gray));
                rlMySharings.setBackgroundResource(R.drawable.shape_bg_round_white_5);
                rlFromSharedDevice.setBackground(null);
               /* tvMySharing.setTextColor(getResources().getColor(R.color.demo_title));
                tvFromSharing.setTextColor(getResources().getColor(R.color.cover_gray));
                llFromSharedDevice.setVisibility(View.GONE);
                llMySharing.setVisibility(View.VISIBLE);
                rlFromSharedDevice.setBackground(null);
                rlMySharings.setBackgroundResource(R.drawable.shape_bg_round_white_5);*/

            }
        });
        String timeMillis = TimeMillisUtil.getTimMillis();
        Log.d("MeSharingManagement", "timeMillis: " + timeMillis);
        Log.d("MeSharingManagement", "uuid: " + XMFunSDKManager.getInstance().getAppUuid());
        Log.d("MeSharingManagement", "appKey: " + XMFunSDKManager.getInstance().getAppKey());
        Log.d("MeSharingManagement", "appSecret: " + XMFunSDKManager.getInstance().getAppSecret());
        Log.d("MeSharingManagement", "movedCard: " + XMFunSDKManager.getInstance().getAppMovecard());


       /* shareDevListPresenter.searchShareDevList();
        presenter.searchUsersByShareThisDev();*/
        ShareManager.getInstance(this).getMyShareDevList();
        ShareManager.getInstance(this).getOtherShareDevList();
    }

    // Slide-out animation
    private void animateSlideOut(View view, Runnable endAction) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, view.getWidth());
        animator.setDuration(300); // Duration in milliseconds
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setTranslationX(0); // Reset position
                if (endAction != null) endAction.run();
            }
        });
        animator.start();
    }

    // Slide-in animation
    private void animateSlideIn(View view) {
        view.setTranslationX(-view.getWidth()); // Start position
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", -view.getWidth(), 0);
        animator.setDuration(300); // Duration in milliseconds
        animator.start();
    }

    // Slide-in from left animation
    private void animateSlideInFromLeft(View view) {
        view.setTranslationX(-view.getWidth()); // Start position: off-screen to the left
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", -view.getWidth(), 0);
        animator.setDuration(500); // Duration in milliseconds
        animator.start();
    }

    // Slide-out to left animation
    private void animateSlideOutToLeft(View view, Runnable endAction) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, -view.getWidth());
        animator.setDuration(500); // Duration in milliseconds
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setTranslationX(0); // Reset position
                if (endAction != null) endAction.run();
            }
        });
        animator.start();
    }

    private void showCancelSharingDialog(String shareId, int position, List<OtherShareDevUserBean> data) {
        // Create and configure the dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_cancel_sharing);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // Find views in the custom layout

        TextView tvCancel = dialog.findViewById(R.id.tvCancel);
        TextView tvOk = dialog.findViewById(R.id.tvOk);

        // Set up click listeners
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OtherShareDevUserBean otherShareDevUserBean = data.get(position);
                shareDevListPresenter.acceptShare(otherShareDevUserBean);
                dialog.dismiss();
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loaderDialog.show();
                OtherShareDevUserBean otherShareDevUserBean = data.get(position);
                shareDevListPresenter.rejectShare(otherShareDevUserBean);
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
    public void onSearchShareDevListResult(List<OtherShareDevUserBean> otherShareDevUserBeans) {
        if (loaderDialog != null) {
            loaderDialog.dismiss();
        }
        if (otherShareDevUserBeans != null && !otherShareDevUserBeans.isEmpty()) {
            llFromSharedDeviceCount.setText("Get " + otherShareDevUserBeans.size() + " times device share");
            fromSharedDeviceAdapter.setData(otherShareDevUserBeans);
        }
    }

    @Override
    public void onRejectShareResult(boolean isSuccess) {
        loaderDialog.dismiss();
        ToastUtils.showLong(isSuccess ? getString(R.string.reject_share_s) : getString(R.string.reject_share_f));
        if (isSuccess) {
            shareDevListPresenter.searchShareDevList();
        }
    }

    @Override
    public void onAcceptShareResult(boolean isSuccess) {
        //ToastUtils.showLong(isSuccess ? getString(R.string.accept_share_s) : getString(R.string.accept_share_f));
        if (isSuccess) {
            shareDevListPresenter.searchShareDevList();
        }
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
            tvMySharingCount.setText("Initiated " + myShareUserInfoBeans.size() + " items device share");
            muSharedDeviceAdapter.setData(myShareUserInfoBeans);
        } else {
            // muSharedDeviceAdapter.setData(null);
            //ToastUtils.showLong(R.string.not_found);
        }
    }

    @Override
    public void onCancelShareResult(boolean isSuccess) {
        if (isSuccess) {
            presenter.searchUsersByShareThisDev();
        }

    }


}