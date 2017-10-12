package com.cookie_apps.myseniorapp1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.cookie_apps.myseniorapp1.MyClass.ConnectionAlert;
import com.cookie_apps.myseniorapp1.MyClass.DialogAlert;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by acount on 15/01/16.
 */
public class ScanBarcode extends Activity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView mScannerView;
    private ProgressDialog loadingDialog;
    private String department = null;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        getUserDepartment();
        //Toast.makeText(this, "scan", Toast.LENGTH_LONG);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(final Result rawResult) {
        // Do something with the result here
        //Log.v("TAG", rawResult.getContents()); // Prints scan results
        //Log.v("TAG", rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)

        Log.e("RESULT TAG", rawResult.getContents()); // Prints scan results
        Log.e("RESULT TAG", rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)

        //Toast.makeText( this, "scan results = " + rawResult.getContents() + " scan format = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_LONG);
        loadingDialog = ProgressDialog.show(ScanBarcode.this, "", "กรุณารอสักครู่...", true);
        new CountDownTimer(5000, 1000) { // delay

            public void onTick(long millisUntilFinished) {
                Log.e("CountDownTimer", "onTick 5 seconds");
            }

            public void onFinish() {
                checkDepartment(rawResult.getContents());
                loadingDialog.dismiss();
            }

        }.start();
    }

    private void checkDepartment(final String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Articles");
        query.whereContains("code", id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e==null) {
                    if (department != null) {
                        if (!object.getString("department").contains(department)) {
                            new DialogAlert(ScanBarcode.this, "ครุภัณฑ์ชิ้นนี้ไม่ใช่ของภาควิชาของคุณ");
                        } else {
                            if (isOnline()) {
                                Intent intent = new Intent(ScanBarcode.this, ShowDataActivity.class);
                                intent.putExtra("result", id);
                                startActivity(intent);
                            } else {
                                new DialogAlert(ScanBarcode.this, "การเชื่อมต่อมีปัญหา กรุณาตรวจสอบการเชื่อมต่อของคุณ");
                            }

                            finish();
                        }
                    }
                } else {
                    new DialogAlert(ScanBarcode.this, "ไม่พบข้อมูลของครุภัณฑ์ชิ้นนี้");
                }
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private void getUserDepartment() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Google");
        query.fromLocalDatastore();
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    department = object.getString("department");
                } else {
                    // something wrong
                }
            }
        });
    }
}
