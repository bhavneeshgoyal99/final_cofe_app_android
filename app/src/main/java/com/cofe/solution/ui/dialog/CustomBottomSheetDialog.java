package com.cofe.solution.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class CustomBottomSheetDialog extends BottomSheetDialog {

    private boolean canDismiss = true; // Control dismissal condition

    public CustomBottomSheetDialog(@NonNull Context context) {
        super(context);
    }

    public void setCanDismiss(boolean canDismiss) {
        this.canDismiss = canDismiss;
    }

    @Override
    public void onBackPressed() {
        if (canDismiss) {
            super.onBackPressed();
        } else {
            hideBottomSheet();
        }
    }

    public void hideBottomSheet() {
        if (getBehavior() != null) {
            getBehavior().setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    public void showBottomSheet() {
        if (getBehavior() != null) {
            getBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }


}
