package com.cofe.solution.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cofe.solution.R;

public class CustomBottomDialog extends Dialog {

    private View anchorView;
    private View externalView;
    private boolean canDismiss = true; // Flag to control dismissal



    public CustomBottomDialog(Context context, View anchorView) {
        super(context);
        this.anchorView = anchorView;

        // Set Dialog properties
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_bottom_sheet);
        setCancelable(true);
       // setCanceledOnTouchOutside(false);
        //hide(); // Just hide the dialog instead of dismissing
        ImageView imgB = findViewById(R.id.cancle_button);
        imgB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                safeHide();
            }
        });

        // Set transparent background
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setWindowAnimations(R.style.CustomDialog);
            window.setGravity(Gravity.BOTTOM);

            // Get the anchor view's position
            int[] anchorLocation = new int[2];
            anchorView.getLocationOnScreen(anchorLocation);
            int anchorBottom = anchorLocation[1] + anchorView.getHeight();

            // Calculate available height from anchor to bottom of screen
            int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
            int dialogHeight = screenHeight - anchorBottom;

            // Apply calculated height
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = dialogHeight;
            window.setAttributes(params);


        }

        //setOnTouchListener();

    }


    private void setOnTouchListener() {
        View contentView = findViewById(R.id.dialog_root_view);
        if (contentView != null) {
            contentView.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    safeHide();
                    return true;
                }
                return false;
            });
        }
    }

    public void safeHide() {
        if (getWindow() != null) {
            getWindow().getDecorView().setVisibility(View.INVISIBLE);
        }
    }

    public void safeShow() {
        if (getWindow() != null) {
            getWindow().getDecorView().setVisibility(View.VISIBLE);
        }
    }


    // Set whether the dialog can be dismissed
    public void setCanDismiss(boolean canDismiss) {
        Log.d("setCanDismiss" ," >"   +canDismiss  );
        this.canDismiss = canDismiss;
    }

    // Method to set an additional external view dynamically
    public void setExternalView(View externalView) {
        this.externalView = externalView;
        RelativeLayout container = findViewById(R.id.parent_rl);
        if (container != null && externalView != null) {
            container.addView(externalView);
        }
    }
}
