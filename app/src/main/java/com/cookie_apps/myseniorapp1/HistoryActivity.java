package com.cookie_apps.myseniorapp1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cookie_apps.myseniorapp1.MyClass.Font;
import com.cookie_apps.myseniorapp1.MyClass.MyParseDateConverter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HistoryActivity extends AppCompatActivity
{
    TextView actionbar_title;
    ListView history_list;
    ImageView btnBack;
    ArrayList<String> dateList = new ArrayList<String>();
    Spinner spinnerYear;
    List<String> yearsDisplay, years;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        actionbar_title = (TextView) findViewById( R.id.actionbar_title );
        history_list = (ListView) findViewById( R.id.history_list );
        btnBack = (ImageView) findViewById( R.id.btnBack );

        retrievedData(getIntent().getExtras().getString("code"));

        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        Font.setFontFace(actionbar_title);

        spinnerYear = (Spinner) findViewById(R.id.spinnerYear);
        yearsDisplay = new ArrayList<String>();
        yearsDisplay.add("เลือกปีงบประมาณ");
        yearsDisplay.add("ตุลาคม 2557 - กันยายน 2558");
        yearsDisplay.add("ตุลาคม 2558 - กันยายน 2559");
        yearsDisplay.add("ตุลาคม 2559 - กันยายน 2560");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, yearsDisplay);

        years = new ArrayList<String>();
        years.add("2557");
        years.add("2558");
        years.add("2559");
        spinnerYear.setAdapter(dataAdapter);
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("click", position + "");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("no click", "");
            }
        });
    }

    private void retrievedData(String id)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("History");
        query.whereEqualTo("code", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e)
            {
                if (e==null && objects.size()>0)
                {
                    for (int i = 0; i < objects.size(); i++)
                    {
                        dateList.add(new MyParseDateConverter().convert(objects.get(i).getCreatedAt().toString(), 0));
                    }
                    ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(HistoryActivity.this, android.R.layout.simple_list_item_1, dateList);
                    history_list.setAdapter( listAdapter );
                    history_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            Intent intent = new Intent(HistoryActivity.this, ShowDataActivity.class);
                            intent.putExtra("result", objects.get(position).getObjectId());
                            intent.putExtra("key", "objectId");
                            intent.putExtra("viewer", true);
                            startActivity(intent);
                        }
                    });
                    actionbar_title.setText(objects.get(0).getString("list"));
                } else if (e==null && objects.size()==0) {
                    actionbar_title.setText("ไม่มีประวัติการแก้ไข");
                } else {
                    Log.e("ParseException", e.toString());
                }
            }
        });
    }
}
