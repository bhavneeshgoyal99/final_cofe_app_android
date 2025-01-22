package com.cofe.solution.base;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cofe.solution.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private View anchorView;
    private View externalView; // The view to be dynamically added

    public CustomBottomSheetDialogFragment(View anchorView) {
        this.anchorView = anchorView;
    }

    // Allow setting an external view dynamically
    public void setExternalView(View externalView) {
        this.externalView = externalView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.custom_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the container inside the BottomSheet where external view will be added
        RelativeLayout container = view.findViewById(R.id.parent_ll);

        // Add the external view if available
        if (container != null && externalView != null) {
            container.addView(externalView);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
