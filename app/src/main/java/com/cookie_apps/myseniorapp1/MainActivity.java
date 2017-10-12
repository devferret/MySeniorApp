package com.cookie_apps.myseniorapp1;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.MarkEnforcingInputStream;
import com.cookie_apps.myseniorapp1.Adapter.DialogGooglePlus;
import com.cookie_apps.myseniorapp1.Fragment.BlankFragment;
import com.cookie_apps.myseniorapp1.Fragment.ScanBarcodeFragment;
import com.cookie_apps.myseniorapp1.Fragment.SearchFragment;
import com.cookie_apps.myseniorapp1.Fragment.SummaryFragment;
import com.cookie_apps.myseniorapp1.MyClass.ConnectionAlert;
import com.cookie_apps.myseniorapp1.MyClass.Font;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static Context context;

    FragmentPagerItemAdapter adapter;
    ViewPager viewPager;
    SmartTabLayout viewPagerTab;
    TextView actionbar_title;
    ImageView btnGooglePlus;

    String[] string_tabs = {"สแกนบาร์โค้ด", "ค้นหาครุภัณฑ์", "สรุป"};
    int[] id_drawable_tabs = {R.drawable.barcode_scan, R.drawable.magnify, R.drawable.chart_pie, R.drawable.account};
    int[] id_drawable_tabs_black = {R.drawable.barcode_scan_black, R.drawable.magnify_black, R.drawable.chart_pie_black, R.drawable.account_black};

    public boolean isLogin = false;

    public String email = "";

    LocationManager MyGps;

    ParseObject google_account;

    public String department = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        btnGooglePlus = (ImageView) findViewById(R.id.btnGooglePlus);
        btnGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline())
                    signInWithGplus();
                else new ConnectionAlert(MainActivity.this);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        bindView();

        signInWithGplus();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGPS();
    }

    private void bindView() {
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.ViewPagerTab);
    }

    private void settingTabPager() {
        Bundle b = new Bundle();
        b.putString("department", department);

        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("", ScanBarcodeFragment.class)
                .add("", SearchFragment.class, b)
                .add("", SummaryFragment.class)
                .create());

        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
        viewPagerTab.setOnPageChangeListener(this);

        actionbar_title.setText(string_tabs[0]);
        Font.setFontFace(actionbar_title);

        for (int i = 0; i < adapter.getCount(); i++) {
            ImageView im = ((ImageView) viewPagerTab.getTabAt(i).findViewById(R.id.custom_img));
            im.setAlpha((i > 0) ? 0.5f : 1f);
            im.setImageResource((i > 0) ? id_drawable_tabs_black[i] : id_drawable_tabs[i]);
            Font.setFontFace(im);

            TextView tv = ((TextView) viewPagerTab.getTabAt(i).findViewById(R.id.custom_txt));
            tv.setAlpha((i > 0) ? 0.5f : 1f);
            tv.setText(string_tabs[i]);
            tv.setTextColor((i > 0) ? getResources().getColor(R.color.colorBlack) : getResources().getColor(R.color.colorWhite));
            Font.setFontFace(tv);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        actionbar_title.setText(string_tabs[position]);
        Font.setFontFace(actionbar_title);

        for (int i = 0; i < adapter.getCount(); i++) {
            ImageView iv = ((ImageView) viewPagerTab.getTabAt(i).findViewById(R.id.custom_img));
            iv.setAlpha((i == position) ? 1f : 0.5f);
            iv.setImageResource((i == position) ? id_drawable_tabs[i] : id_drawable_tabs_black[i]);
            Font.setFontFace(iv);

            TextView tv = ((TextView) viewPagerTab.getTabAt(i).findViewById(R.id.custom_txt));
            tv.setAlpha((i == position) ? 1f : 0.5f);
            tv.setText(string_tabs[i]);
            tv.setTextColor((i == position) ? getResources().getColor(R.color.colorWhite) : getResources().getColor(R.color.colorBlack));
            Font.setFontFace(tv);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "MainActivity";

    private static final int PROFILE_PIC_SIZE = 400;

    // Google client to interact with Google API
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    private GoogleApiClient mGoogleApiClient;

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    public void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;

//        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        if (isOnline()) getProfileInformation();

        // Update the UI after signin
    }

    private void getProfileInformation() {
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            final String personName = currentPerson.getDisplayName();
            final String personPhotoUrl = currentPerson.getImage().getUrl();
//            String personGooglePlusProfile = currentPerson.getUrl();
//            String personGooglePlusCover = currentPerson.getCover().getCoverPhoto().getUrl();
            email = Plus.AccountApi.getAccountName(mGoogleApiClient);

            Log.e(TAG, "Email: " + email + " Name: " + personName + ", Image: " + personPhotoUrl);

            // by default the profile url gives 50x50 px image only
            // we can replace the value with whatever dimension we want by
            // replacing sz=X

            Log.e("Login", personName);

            putAccount(email, personName);

            btnGooglePlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DialogGooglePlus(MainActivity.this, personPhotoUrl, personName);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Person information is null", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            try {
                // Store the ConnectionResult for later usage
                mConnectionResult = result;
                startIntentSenderForResult(result.getResolution().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
                if (mSignInClicked) {
                    // The user has already clicked 'sign-in' so we attempt to
                    // resolve all
                    // errors until the user is signed in, or they cancel.
                    resolveSignInError();
                }
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
            }
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult != null && mConnectionResult.hasResolution()) {
//            try {
                mIntentInProgress = true;
//                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
//            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
//            }
        } else {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void putAccount(final String email, final String name) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Account");
        query.whereEqualTo("email", email);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e != null) {
                    checkDepartment(name);
                } else {
                    if (object.getString("department") == null) {
                        checkDepartment(name);
                        object.put("department", department);
                        object.saveEventually();
                    } else {
                        department = object.getString("department");
                        pinUserInDevice(name);
                    }
                }
            }
        });
    }

    public void checkGPS() {
        MyGps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean gpsEnabled, networkEnabled;
        gpsEnabled = MyGps.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkEnabled = MyGps.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gpsEnabled && !networkEnabled) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("GPS ถูกปิดการใช้งานในอุปกรณ์ของคุณ กรุณาเปิด GPS เพื่อใช้งานแอพพลิเคชั่น")
                    .setCancelable(false)
                    .setPositiveButton("ตกลง",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }
                            });
//            alertDialogBuilder.setNegativeButton("ยกเลิก",
//                    new DialogInterface.OnClickListener(){
//                        public void onClick(DialogInterface dialog, int id){
//                            dialog.cancel();
//                        }
//                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    public void checkDepartment(final String personName) {

        final String[] items = {
                "CO", "IE", "BME"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("เลือกภาควิชา");
        builder.setCancelable(false);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, final int item) {
                // Do something with the selection
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage("คุณสามารถเห็นข้อมูลครุภัณฑ์ได้เฉพาะของภาควิชา " + items[item] + " เท่านั้น?")
                        .setCancelable(false)
                        .setPositiveButton("ตกลง",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        department = items[item];

                                        ParseObject account = new ParseObject("Account");
                                        account.put("email", email);
                                        account.put("name", personName);
                                        account.put("department", department);
                                        account.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                Log.e("new account", "save successful");
                                            }
                                        });

                                        pinUserInDevice(personName);
                                    }
                                });
                alertDialogBuilder.setNegativeButton("ยกเลิก",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                checkDepartment(personName);
                            }
                        });
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void pinUserInDevice(final String name) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Google");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.e("objects in back", objects.size()+ "");
                    for (int i=0; i<objects.size(); i++) {
                        Log.e("object"+i, objects.get(i).getString("name"));
                        objects.get(i).deleteEventually();
                    }
                } else {
                    Log.e("objects in back", "nothings");
                }

                google_account = new ParseObject("Google");
                google_account.put("test", "test");
                google_account.put("name", name);
                google_account.put("department", department);
                Log.e("PIN", "test" + name + department);
                google_account.pinInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            settingTabPager();
                        } else {
                            Log.e("ParseException pin G+", e.toString());
                        }
                    }
                });
            }
        });
    }
}
