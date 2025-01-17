package com.cofe.solution.ui.dialog;


import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.cofe.solution.R;

public class DialogWaiting {


    private Dialog dialog = null;
    private TextView textView = null;
    private ImageView imageView = null;
    String className = "";
    String messge = "";
    Context context;

    
    public DialogWaiting(Context context) {
        this.context = context;
        className = context.getClass().getName();
        dialog = new Dialog(context, R.style.dialog_translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_waiting);

        textView = (TextView) dialog.findViewById(R.id.waittingText);
        imageView = dialog.findViewById(R.id.progress_logo);
    }

    public void show(String message) {
        this.messge = message;
        ObjectAnimator rotation = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        rotation.setDuration(2000); // Duration for one full rotation (in milliseconds)
        rotation.setInterpolator(new LinearInterpolator()); // Smooth, continuous rotation
        rotation.setRepeatCount(ObjectAnimator.INFINITE); // Infinite repetition
        rotation.start();

        textView.setText(message);
        dialog.show();
    }

    public void show() {
        show("");
    }

    public void show(int hint) {
        show(hint + "");
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }
}
