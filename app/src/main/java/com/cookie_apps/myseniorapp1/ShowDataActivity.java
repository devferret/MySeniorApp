package com.cookie_apps.myseniorapp1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cookie_apps.myseniorapp1.MyClass.DialogAlert;
import com.cookie_apps.myseniorapp1.MyClass.Font;
import com.cookie_apps.myseniorapp1.MyClass.MyParseDateConverter;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.Plus;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ShowDataActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    ImageView img1, img2, img3, img4, btnBack;
    TextView txtCode, txtName, txtBrand, txtModel, txtPrice, txtReceived, txtCompany, txtBuilding, txtStatus, txtCheckDate, txtUser, txtDesc;
    TextView actionbar_title, txtEdit;
    FloatingActionButton fab_camera, fab_history;

    String[] status = {"", "#4CAF50>ใช้งานอยู่", "#FF9800>ชำรุด/เสื่อมสภาพ", "#9E9E9E>ไม่จำเป็นใช้งาน", "#F44336>หาไม่พบ"};
    String[] _status = {"", "ใช้งานอยู่", "ชำรุด/เสื่อมสภาพ", "ไม่จำเป็นใช้งาน", "หาไม่พบ"};

    String[] status_dialog = {"#4CAF50>ใช้งานอยู่", "#FF9800>ชำรุด/เสื่อมสภาพ", "#9E9E9E>ไม่จำเป็นใช้งาน", "#F44336>หาไม่พบ"};
    String[] _status_dialog = {"ใช้งานอยู่", "ชำรุด/เสื่อมสภาพ", "ไม่จำเป็นใช้งาน", "หาไม่พบ"};

    int INTENT_CAMERA_CODE = 11;

    int imageLatest = 0;
    String[] newImageFileName = {"", "", "", ""};
    boolean[] newImageUpdated = {false, false, false, false};

    String code = "";
    boolean isViewer = false;
    String key = "", result = "";

    String name = "";

    GoogleApiClient mGoogleApiClient;
    int myBuilding = 1;

    LocationManager MyGps;

    ProgressDialog loadingDialog;

    ScrollView scrollView;
    LinearLayout linearLayout;

    String department = null;

    String year = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        Bundle bundle = getIntent().getExtras();
        code = bundle.getString("result");
        isViewer = bundle.getBoolean("viewer");
        key = bundle.getString("key");
        result = bundle.getString("result");
        name = getUser();

        buildGoogleApiClient();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else {
            Log.e("error", "Map connection error");
        }

        bindView();
    }

    private void bindView() {
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        actionbar_title.setText("กำลังโหลดข้อมูล...");
        txtEdit = (TextView) findViewById(R.id.txtEdit);
        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);
        txtCode = (TextView) findViewById(R.id.txtCode);
        txtName = (TextView) findViewById(R.id.txtName);
        txtBrand = (TextView) findViewById(R.id.txtBrand);
        txtModel = (TextView) findViewById(R.id.txtModel);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtReceived = (TextView) findViewById(R.id.txtReceived);
        txtCompany = (TextView) findViewById(R.id.txtCompany);
        txtBuilding = (TextView) findViewById(R.id.txtBuilding);
        txtStatus = (TextView) findViewById(R.id.txtStatus);

        txtCheckDate = (TextView) findViewById(R.id.txtCheckDate);
        txtUser = (TextView) findViewById(R.id.txtUser);
        txtDesc = (TextView) findViewById(R.id.txtDesc);

        fab_camera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab_history = (FloatingActionButton) findViewById(R.id.fab_history);

        Font.setFontFace(actionbar_title);
        Font.setFontFace(txtEdit);
        Font.setFontFace(txtName);
        Font.setFontFace(txtBrand);
        Font.setFontFace(txtModel);
        Font.setFontFace(txtCode);
        Font.setFontFace(txtPrice);
        Font.setFontFace(txtReceived);
        Font.setFontFace(txtCompany);
        Font.setFontFace(txtBuilding);
        Font.setFontFace(txtStatus);
        Font.setFontFace(txtCheckDate);
        Font.setFontFace(txtUser);
        Font.setFontFace(txtDesc);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (isViewer) txtEdit.setVisibility(View.GONE);
        fab_camera.setVisibility(View.GONE);
        fab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureintent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_SIZE_LIMIT, 2048 * 2048);
                startActivityForResult(captureintent, INTENT_CAMERA_CODE);
            }
        });
        fab_history.setVisibility(View.GONE);
        fab_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowDataActivity.this, HistoryActivity.class);
                intent.putExtra("code", code);
                startActivity(intent);
            }
        });
    }

    private void retrievedData(String key, String id) {
        Log.e("ID", id);

        ParseQuery<ParseObject> query;

        if (key.matches("code")) query = ParseQuery.getQuery("Articles");
        else query = ParseQuery.getQuery("History");

        query.whereContains(key, id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject object, final ParseException e) {
                if (e == null) {
                    // check department
                    if (department != null) {
                        if (!object.getString("department").contains(department)) {
                            new DialogAlert(ShowDataActivity.this, "ครุภัณฑ์ชิ้นนี้ไม่ใช่ของภาควิชาของคุณ");
//                            Toast.makeText(ShowDataActivity.this, "ครุภัณฑ์ชิ้นนี้ไม่ใช่ของภาควิชาของคุณ", Toast.LENGTH_LONG).show();
                            finish();
                        }

                        if (isViewer)
                            actionbar_title.setText(new MyParseDateConverter().convert(object.getCreatedAt().toString(), 0));
                        else
                            actionbar_title.setText((object.getString("list") != null) ? object.getString("list") : "<font color=#F44336>name not found</font>");

//                    imageLatest = object.getInt("imageLatest");

                        setImageResource(object.getParseFile("image1"), img1);
                        setImageResource(object.getParseFile("image2"), img2);
                        setImageResource(object.getParseFile("image3"), img3);
                        setImageResource(object.getParseFile("image4"), img4);

                        Log.e("imageLatest", imageLatest + "");

                        if (object.getParseFile("image1") != null)
                            newImageFileName[0] = object.getParseFile("image1").getName().split("-")[object.getParseFile("image1").getName().split("-").length - 1];
                        if (object.getParseFile("image2") != null)
                            newImageFileName[1] = object.getParseFile("image2").getName().split("-")[object.getParseFile("image2").getName().split("-").length - 1];
                        if (object.getParseFile("image3") != null)
                            newImageFileName[2] = object.getParseFile("image3").getName().split("-")[object.getParseFile("image3").getName().split("-").length - 1];
                        if (object.getParseFile("image4") != null)
                            newImageFileName[3] = object.getParseFile("image4").getName().split("-")[object.getParseFile("image4").getName().split("-").length - 1];

                        int status_position = 0;
                        for (int i = 0; i < 4; i++) {
                            if (object.getString("status") != null && object.getString("status").matches(_status[i]))
                                status_position = i;
                        }

                        txtCode.setText(Html.fromHtml("<font color=#3F51B5>รหัสครุภัณฑ์: </font> <font>" + ((object.getString("code") != null) ? object.getString("code") : "ไม่มีรหัสครุภัณฑ์") + "</font>"), TextView.BufferType.SPANNABLE);
                        txtName.setText(Html.fromHtml("<font color=#3F51B5>รายการ: </font> <font>" + ((object.getString("list") != null) ? object.getString("list") : "ไม่มีชื่อครุภัณฑ์") + "</font>"), TextView.BufferType.SPANNABLE);
                        txtBrand.setText(Html.fromHtml("<font color=#3F51B5>ยี่ห้อ: </font> <font>" + ((object.getString("brand") != null) ? object.getString("brand") : "ไม่มียี่ห้อ</font>") + "</font>"), TextView.BufferType.SPANNABLE);
                        txtModel.setText(Html.fromHtml("<font color=#3F51B5>รุ่น: </font> <font>" + ((object.getString("model") != null) ? object.getString("model") : "ไม่มีรุ่น</font>") + "</font>"), TextView.BufferType.SPANNABLE);
                        txtPrice.setText(Html.fromHtml("<font color=#3F51B5>ราคา: </font> <font>" + ((object.getString("price") != null) ? object.getString("price") : "ไม่มีราคา</font>") + "</font>"), TextView.BufferType.SPANNABLE);
                        txtReceived.setText(Html.fromHtml("<font color=#3F51B5>วิธีได้มา: </font> <font>" + ((object.getString("received") != null) ? object.getString("received") : "ไม่มีข้อมูล</font>") + "</font>"), TextView.BufferType.SPANNABLE);
                        txtCompany.setText(Html.fromHtml("<font color=#3F51B5>บริษัท / ห้างร้าน: </font> <font>" + ((object.getString("company") != null) ? object.getString("company") : "ไม่มีข้อมูล</font>") + "</font>"), TextView.BufferType.SPANNABLE);
                        txtBuilding.setText(Html.fromHtml("<font color=#3F51B5>อาคาร: </font> <font>" + ((object.getInt("building") != 0) ? object.getInt("building") : "ไม่มีข้อมูล</font>") + "</font>"), TextView.BufferType.SPANNABLE);
                        txtStatus.setText(Html.fromHtml("<font color=#303F9F>สภาพของสินทรัพย์: </font> " + ((status_position != 0) ? "<font color=" + status[status_position] + "</font>" : "ไม่มีข้อมูล")));

                        txtCheckDate.setText(Html.fromHtml("<font color=#303F9F>วันที่ตรวจ: </font>" + ((status_position != 0) ? new MyParseDateConverter().convert(object.getUpdatedAt().toString(), 1) : "ยังไม่ได้ตรวจ")));
                        txtUser.setText(Html.fromHtml("<font color=#303F9F>ผู้ตรวจ: </font> <font>" + ((object.getString("user") != null) ? object.getString("user") : "ไม่มีข้อมูล</font>") + "</font>"), TextView.BufferType.SPANNABLE);
//                    Log.e("USER", object.getString("user") == null ? "no user" : object.getString("user"));

                        txtDesc.setText(Html.fromHtml("<font color=#303F9F>รายละเอียด: </font> <font>" + ((object.getString("desc") != null) ? object.getString("desc") : "ไม่มีข้อมูล</font>") + "</font>"), TextView.BufferType.SPANNABLE);

                        if (!isViewer) {
                            editDialog(txtBrand, object.getString("brand"), "ยี่ห้อ");
                            editDialog(txtModel, object.getString("model"), "รุ่น");
                            editDialog(txtDesc, object.getString("desc"), "รายละเอียด");

                            txtStatus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowDataActivity.this);
                                    builder.setTitle("เลือกสภาพของสินทรัพย์");
                                    builder.setItems(_status_dialog, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int item) {
                                            // Do something with the selection
                                            txtStatus.setText(Html.fromHtml("<font color=#303F9F>สภาพของสินทรัพย์: </font> <font color=" + status_dialog[item]));
                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            });
                        }

                        if (!isViewer) fab_camera.setVisibility(View.VISIBLE);
                        if (!isViewer) fab_history.setVisibility(View.VISIBLE);

                        txtEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.e("Save", "now saving data.");
                                Log.e("myBuilding", myBuilding + "");

                                loadingDialog = ProgressDialog.show(ShowDataActivity.this, "",
                                        "กรุณารอสักครู่...", true);

                                object.put("code", txtCode.getText().toString().split("รหัสครุภัณฑ์: ")[1]);
                                object.put("list", txtName.getText().toString().split("รายการ: ")[1]);
                                object.put("brand", txtBrand.getText().toString().split("ยี่ห้อ: ")[1]);
                                object.put("model", txtModel.getText().toString().split("รุ่น: ")[1]);
                                object.put("price", txtPrice.getText().toString().split("ราคา: ")[1]);
                                object.put("received", txtReceived.getText().toString().split("วิธีได้มา: ")[1]);
                                object.put("company", txtCompany.getText().toString().split("บริษัท / ห้างร้าน: ")[1]);
                                object.put("status", txtStatus.getText().toString().split("สภาพของสินทรัพย์: ")[1]);
                                object.put("building", myBuilding);
                                object.put("user", getUser());

                                object.put("desc", txtDesc.getText().toString().split("รายละเอียด: ")[1]);

                                txtBuilding.setText(Html.fromHtml("<font color=#3F51B5>อาคาร: </font>" + myBuilding));
                                txtUser.setText(Html.fromHtml("<font color=#3F51B5>ผู้ตรวจ: </font>" + getUser()));

                                saveAllImage(object);
                            }
                        });
                    } else {
                        Toast.makeText(ShowDataActivity.this, "ไม่พบข้อมูลภาควิชาของคุณ", Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    Log.e("error", "Data not found.");
                    finish();
                }
            }
        });
    }

    private void pickPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra("outputX", dpToPixel(24)).putExtra("outputY", dpToPixel(24));
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    public int dpToPixel(int dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }

    private void setImageResource(final ParseFile parseFile, final ImageView imageView) {
        if (parseFile != null) {
            Glide.with(ShowDataActivity.this).load(parseFile.getUrl()).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // กดดูรูป
                    startActivity(new Intent(ShowDataActivity.this, ImageActivity.class).putExtra("image", parseFile.getUrl()));
                }
            });
            imageLatest++;
        }
    }

    private void editDialog(final TextView thisEditText, final String objectString, final String hint) {
        thisEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(ShowDataActivity.this);
                View promptView = layoutInflater.inflate(R.layout.dialog_input, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShowDataActivity.this);
                alertDialogBuilder.setView(promptView);

                final TextView textView = (TextView) promptView.findViewById(R.id.textView);
                final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
                Font.setFontFace(textView);
                Font.setFontFace(editText);
                textView.setText(hint);
                if (objectString != null)
                    editText.setText(objectString);
                editText.setHint(hint);
                // setup a dialog window
                alertDialogBuilder.setCancelable(false).setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        thisEditText.setText(Html.fromHtml("<font color=#303F9F>" + hint + ": </font> <font>" + ((editText.getText().toString() != null) ? editText.getText().toString() : "#F44336>ไม่มีข้อมูล</font>")));
                    }
                }).setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });
    }

    private void alertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(ShowDataActivity.this).create();
        alertDialog.setTitle("ดำเนินการเสร็จแล้ว");
        alertDialog.setMessage("บันทึกข้อมูลครุภัณฑ์เรียบร้อย");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "ตกลง",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ShowDataActivity.this.onResume();
                    }
                });
        alertDialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == INTENT_CAMERA_CODE) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                ImageView[] imageViews = {img1, img2, img3, img4};
                String fileName = getDateString() + ".jpg";

                if (imageLatest > 3) {
                    for (int i = 0; i < 3; i++) {
                        imageViews[i].setImageDrawable(imageViews[i + 1].getDrawable());
                        newImageFileName[i] = newImageFileName[i + 1];
                    }
                    imageViews[3].setImageBitmap(photo);
                } else {
                    imageViews[imageLatest].setImageBitmap(photo);
                    newImageFileName[imageLatest] = fileName;
                    imageLatest++;
                }
            }
        }
    }

    private void saveAllImage(final ParseObject object) {
        ImageView[] imageViews = {img1, img2, img3, img4};
        final String[] imageKeys = {"image1", "image2", "image3", "image4"};
        final int[] processCount = {0};

        if (imageLatest > 0) {
            for (int i = 0; i < imageLatest; i++) {
                imageViews[i].setDrawingCacheEnabled(true);
                imageViews[i].buildDrawingCache();
                Bitmap bm = imageViews[i].getDrawingCache();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                final ParseFile file = new ParseFile(newImageFileName[i], byteArray);
                final int finalI = i;
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            object.put(imageKeys[finalI], file);

                            processCount[0]++;
                            if (processCount[0] == imageLatest) {
                                saveAndHistory(object);
                            } else {
//                            Log.e("processCount", processCount[0] + "");
                            }
                        } else {
                            Log.e("ParseException", "file image : " + e.toString());
                        }
                    }
                });
            }
        } else {
            saveAndHistory(object);
        }
    }

    private void saveAndHistory(final ParseObject object) {
//        Log.e("year", Calendar.getInstance().get(Calendar.YEAR) + " ");

        Date today = new Date(Calendar.getInstance().get(Calendar.YEAR)-1900, Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        Date date = new Date(Calendar.getInstance().get(Calendar.YEAR)-1900, 8, 30); // 30 sep

        if (today.after(date)) {
            Log.e("date", "after");
            year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 543 + 1);
        } else {
            Log.e("date", "before");
            year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 543);
        }

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // HISTORY
                    ParseObject history = new ParseObject("History");
                    for (Iterator it = object.keySet().iterator(); it.hasNext(); ) {
                        Object key = it.next();
                        history.put(key.toString(), object.get(key.toString()));
                        history.put("budgetYear", year);
                    }
                    history.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            loadingDialog.dismiss();
                            Log.e("HISTORY", "saved");
                        }
                    });

                    alertDialog();
                } else {
                    Log.e("ParseException", "save image : " + e.toString());
                }
            }
        });
    }

    public String getDateString() {
        String timeZone = "GMT+07:00";
        String format = "dMMyyyy_kkmmss";
        int where = 0;

        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
        calendar.add(Calendar.DAY_OF_YEAR, where);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        return simpleDateFormat.format(calendar.getTime());
    }

    private String getUser() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Google");
        query.fromLocalDatastore();
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Log.e("เทส", object.getString("test"));
                    Log.e("ผู้ตรวจ", object.getString("name"));
                    Log.e("ภาควิชา", object.getString("department"));

                    name = object.getString("name");
                    department = object.getString("department");
                } else {
                    // something wrong
                }
            }
        });

        return name;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Log.e("error", "Map failed to connect...");

    }

    @Override
    public void onConnected(Bundle arg0) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            //Log.e("my location", "Latitude: "+ String.valueOf(mLastLocation.getLatitude()) + " Longitude: "+ String.valueOf(mLastLocation.getLongitude()));

            LatLng myLatlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            calculateLocation(myLatlng);
        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();
    }

    private void calculateLocation(LatLng myLatlng) {
        Log.e("myLongitude", myLatlng.longitude + "");
        if (myLatlng.longitude > 100.325477) {
            Log.e("my location", "building 3");
            myBuilding = 3;
        } else if (myLatlng.longitude < 100.325477 && myLatlng.longitude > 100.324309) {
            Log.e("my location", "building 1");
            myBuilding = 1;
        } else {
            Log.e("my location", "building 2");
            myBuilding = 2;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (key != null) retrievedData(key, result);
        else retrievedData("code", result);

        checkGPS();
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
}
