package com.cookie_apps.myseniorapp1.MyClass;

import android.app.Activity;
import android.support.v7.app.*;
import android.content.DialogInterface;

/**
 * Created by acount on 04/05/16.
 */
public class ConnectionAlert {

    AlertDialog.Builder dialogBuilder;

    public ConnectionAlert(final Activity activity) {

        dialogBuilder = new android.support.v7.app.AlertDialog.Builder(activity);
        dialogBuilder.setTitle("การเชื่อมต่อมีปัญหา กรุณาลองใหม่อีกครั้ง");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // activity.finish();
            }
        });
        dialogBuilder.show();
    }

    public void setTitle(String title) {
        dialogBuilder.setTitle(title);
    }
}
