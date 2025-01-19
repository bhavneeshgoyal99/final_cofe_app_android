package com.cofe.solution.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.cofe.solution.R;

public class LoaderDialog {
    private Dialog dialog;
    private TextView tvMessage;
    Context context;
    public LoaderDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loader);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // Set dialog width to 60% of screen width
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.4);
        dialog.getWindow().setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView gifLoader = dialog.findViewById(R.id.gifLoader);
        tvMessage = dialog.findViewById(R.id.tvMessage);

        // Load GIF using Glide
        Glide.with(context)
                .asGif()
                .load(R.drawable.animated) // Replace with your GIF resource
                .into(gifLoader);
    }

    public void setMessage(String message) {
        if(message.equals("")) {
            message = context.getString(R.string.loading);
        }
        tvMessage.setText(message);
        show();
    }

    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
