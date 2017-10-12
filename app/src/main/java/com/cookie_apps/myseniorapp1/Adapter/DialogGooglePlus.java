package com.cookie_apps.myseniorapp1.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cookie_apps.myseniorapp1.MainActivity;
import com.cookie_apps.myseniorapp1.MyClass.Font;
import com.cookie_apps.myseniorapp1.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acount on 16/03/16.
 */
public class DialogGooglePlus {
    Dialog dialog;
    ImageView img_profile;
    TextView tv_name, tv_logout;

    public DialogGooglePlus(final Context context, String personPhotoUrl, String name) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_google);
        dialog.setCanceledOnTouchOutside(true);

        img_profile = (ImageView) dialog.findViewById(R.id.img_profile);
        tv_name = (TextView) dialog.findViewById(R.id.tv_name);
        tv_logout = (TextView) dialog.findViewById(R.id.tv_logout);

        Font.setFontFace(tv_name);
        Font.setFontFace(tv_logout);

        Glide.with(context).load(personPhotoUrl + "0").into(img_profile);
        tv_name.setText(name);

        dialog.show();

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Google");
                query.fromLocalDatastore();
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            Log.e("objects in back", objects.size()+ "");
                            for (int i=0; i<objects.size(); i++) {
                                Log.e("delete object "+i, objects.get(i).getString("name"));
                                objects.get(i).deleteEventually();
                            }

                            ((MainActivity) context).signOutFromGplus();
                            //Close application
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        } else {
                            Log.e("objects in back", "nothings");
                        }
                    }

//                    public void done(ParseObject object, ParseException e) {
//                        if (e == null) {
//                            Log.e("ออกจาก", object.getString("name"));
//                            object.deleteEventually();
//
//                            ((MainActivity) context).signOutFromGplus();
//
//                            //Close application
//                            android.os.Process.killProcess(android.os.Process.myPid());
//                            System.exit(1);
//                        } else {
//                            // something wrong
//                            Log.e("ParseException unpin G+", e.toString());
//                        }
//                    }
                });
            }
        });

    }
}
