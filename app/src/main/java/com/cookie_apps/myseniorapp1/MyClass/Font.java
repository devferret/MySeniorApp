package com.cookie_apps.myseniorapp1.MyClass;

/**
 * Created by User on 26/10/2558.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cookie_apps.myseniorapp1.MainActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;


public class Font
{

    public static void setFontFace(View v){

        Typeface myFontNormal = getFont("fonts/ThaiSansNeue-SemiBold.otf", MainActivity.context);
        int type = 0;

        if(v instanceof TextView){
            TextView view = (TextView) v;
            switch (type){
                case 0:view.setTypeface(myFontNormal);
            }

        }else if(v instanceof EditText){
            EditText view = (EditText) v;
            switch (type){
                case 0:view.setTypeface(myFontNormal);
                    break;
            }

        }else if(v instanceof Button){
            Button view = (Button) v;
            switch (type){
                case 0:view.setTypeface(myFontNormal);
                    break;
            }

        }else if(v instanceof RadioButton){
            RadioButton view = (RadioButton) v;
            switch (type){
                case 0:view.setTypeface(myFontNormal);
                    break;
            }

        }else if(v instanceof CheckBox){
            CheckBox view = (CheckBox) v;
            switch (type){
                case 0:view.setTypeface(myFontNormal);
                    break;
            }

        }else if(v instanceof Switch){
            Switch view = (Switch) v;
            switch (type){
                case 0:view.setTypeface(myFontNormal);
                    break;
            }

        }else if(v instanceof ToggleButton){
            ToggleButton view = (ToggleButton) v;
            switch (type){
                case 0:view.setTypeface(myFontNormal);
                    break;
            }

        }else if(v instanceof CheckedTextView){
            CheckedTextView view = (CheckedTextView) v;
            switch (type){
                case 0:view.setTypeface(myFontNormal);
                    break;
            }
        }

    }


    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

        private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();
        public static Typeface getFont(String name, Context context) {
            Typeface tf = fontCache.get(name);
            if(tf == null) {
                try {
                    tf = Typeface.createFromAsset(context.getAssets(), name);
                }
                catch (Exception e) {
                    return null;
                }
                fontCache.put(name, tf);
            }
            return tf;
        }


}



