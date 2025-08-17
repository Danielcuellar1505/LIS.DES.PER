package com.example.lisdesper.Helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;

public class ModalHelper {

    public static void showMessageDialog(Context context, String title, String message, long autoDismissMillis) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .create();
        dialog.show();

        if (autoDismissMillis > 0) {
            new Handler(Looper.getMainLooper()).postDelayed(dialog::dismiss, autoDismissMillis);
        }
    }

    public static void showCustomDialog(Context context, int layoutResId, String title,
                                        String positiveText, Runnable onPositive,
                                        String negativeText, Runnable onNegative) {
        View view = LayoutInflater.from(context).inflate(layoutResId, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle(title);

        if (positiveText != null) {
            builder.setPositiveButton(positiveText, (dialog, which) -> {
                if (onPositive != null) onPositive.run();
            });
        }

        if (negativeText != null) {
            builder.setNegativeButton(negativeText, (dialog, which) -> {
                if (onNegative != null) onNegative.run();
            });
        }

        builder.show();
    }
}
