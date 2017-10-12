package com.cookie_apps.myseniorapp1.MyClass;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by acount on 05/31/16.
 */
public class DialogAlert {
    AlertDialog.Builder dialogBuilder;
    private int click=0;

    public DialogAlert(final Activity activity, final String title) {

        dialogBuilder = new android.support.v7.app.AlertDialog.Builder(activity);
        dialogBuilder.setTitle(title);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (click == 1) {
                    activity.finish();
                }
            }
        });
        dialogBuilder.show();
    }

    public void setClick(final int click) {
        // 0 dismiss
        // 1 finish

        this.click = click;
    }
}
