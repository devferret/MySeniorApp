package com.cookie_apps.myseniorapp1;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

/**
 * Created by acount on 23/04/16.
 */
public class MyApplication extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("Application", "MyApplication");

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "qbK4mnFRaC42Po6IZ9T1y4z0seKRkGTmJbLAyn6X", "uqOo78pdtqoKgWffa4YLJIT5E9MQpvTF2lhtIkZq");
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }
}
