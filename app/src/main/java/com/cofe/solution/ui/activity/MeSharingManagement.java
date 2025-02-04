package com.cofe.solution.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.R;
import com.cofe.solution.ui.adapter.FromSharedDeviceAdapter;
import com.cofe.solution.ui.adapter.LanguageAdapter;
import com.cofe.solution.ui.adapter.MuSharedDeviceAdapter;

public class MeSharingManagement extends AppCompatActivity {
    TextView tvTitleHeader;
    TextView tvMySharing;
    TextView tvFromSharing;
    ImageView back_button;
    LinearLayout llFromSharedDevice;
    LinearLayout llMySharing;
    RelativeLayout rlMySharings;
    RelativeLayout rlFromSharedDevice;

    RecyclerView rvMySharings;
    RecyclerView rvFromSharedDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_me_sharing_management);

        initUis();
    }

    private void initUis()
    {
        tvTitleHeader=findViewById(R.id.toolbar_title);
        back_button=findViewById(R.id.back_button);
        llFromSharedDevice=findViewById(R.id.llFromSharedDevice);
        llMySharing=findViewById(R.id.llMySharing);
        rlMySharings=findViewById(R.id.rlMySharings);
        rlFromSharedDevice=findViewById(R.id.rlFromSharedDevice);
        rvMySharings=findViewById(R.id.rvMySharings);
        rvFromSharedDevice=findViewById(R.id.rvFromSharedDevice);
        tvMySharing=findViewById(R.id.tvMySharing);
        tvFromSharing=findViewById(R.id.tvFromSharing);

        rvMySharings.setLayoutManager(new LinearLayoutManager(this));
        rvMySharings.setAdapter(new MuSharedDeviceAdapter(MeSharingManagement.this));

        rvFromSharedDevice.setLayoutManager(new LinearLayoutManager(this));
        rvFromSharedDevice.setAdapter(new FromSharedDeviceAdapter(MeSharingManagement.this, new FromSharedDeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick() {
                showCancelSharingDialog();
            }
        }));
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

    private void showCancelSharingDialog() {
        // Create and configure the dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_cancel_sharing);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // Find views in the custom layout

        TextView tvCancel = dialog.findViewById(R.id.tvCancel);

        // Set up click listeners
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
}