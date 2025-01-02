package com.cofe.solution.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import com.cofe.solution.R;

public class DialogWaitting {


    private Dialog dialog = null;
    private TextView textView = null;

    public DialogWaitting(Context context) {
        dialog = new Dialog(context, R.style.dialog_translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_waiting);
        textView = (TextView) dialog.findViewById(R.id.waittingText);
    }

    public void show() {
        textView.setText("");
        dialog.show();
    }

    public void show(String hint) {
        textView.setText(hint);
        dialog.show();
    }

    public void show(int hint) {
        textView.setText(hint);
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }
}
