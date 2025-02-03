package com.cofe.solution.base;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.animation.ObjectAnimator;
import android.widget.FrameLayout;

public class CustomBottomSheet {

    private Dialog dialog;
    private View customView;
    private boolean isPersistent = true; // Control hide vs dismiss
    private GestureDetector gestureDetector;
    private View anchorView; // Reference to the anchor view

    public CustomBottomSheet(Context context, View customView, View anchorView) {
        this.customView = customView;
        this.anchorView = anchorView;
        initDialog(context);
        setupGestureListener();
    }

    private void initDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(customView);
        dialog.setCancelable(false); // Prevents default dismiss on outside touch

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.TOP);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Disable animation to apply custom animations
            window.setWindowAnimations(0);
        }

        // Adjust BottomSheet height dynamically based on anchorView
        customView.post(() -> adjustHeight());

        // Handle outside touch manually
        FrameLayout parentView = (FrameLayout) dialog.findViewById(android.R.id.content);
        if (parentView != null) {
            parentView.setOnClickListener(v -> {
                if (!isPersistent) {
                    dismiss();
                } else {
                    hide();
                }
            });
        }

        // Initially hide the bottom sheet
        customView.setVisibility(View.INVISIBLE);
    }

    private void adjustHeight() {
        int screenHeight = customView.getResources().getDisplayMetrics().heightPixels;
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        int anchorBottom = location[1] + anchorView.getHeight();

        // Adjust height to cover the remaining space from anchorView to bottom
        int newHeight = screenHeight - anchorBottom;
        customView.getLayoutParams().height = newHeight;
        customView.requestLayout();
    }

    private void setupGestureListener() {
        gestureDetector = new GestureDetector(customView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getY() < e2.getY() && Math.abs(velocityY) > 500) { // Swipe down
                    if (!isPersistent) {
                        dismiss();
                    } else {
                        hide();
                    }
                    return true;
                }
                return false;
            }
        });

        customView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    public void setPersistent(boolean persistent) {
        this.isPersistent = persistent;
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
        adjustHeight(); // Ensure the height is correctly set before showing
        customView.setVisibility(View.VISIBLE);
        slideUp(customView);
    }

    public void hide() {
        slideDown(customView);
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void slideUp(View view) {
        view.setTranslationY(view.getHeight());
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", view.getHeight(), 0);
        animator.setDuration(300);
        animator.start();
    }

    private void slideDown(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0, view.getHeight());
        animator.setDuration(300);
        animator.start();
        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (isPersistent) {
                    view.setVisibility(View.INVISIBLE);
                } else {
                    dismiss();
                }
            }
        });
    }
}